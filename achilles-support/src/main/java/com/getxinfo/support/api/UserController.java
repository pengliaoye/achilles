package com.getxinfo.support.api;

import com.getxinfo.support.Constants;
import com.getxinfo.support.business.UserService;
import com.getxinfo.support.dataaccess.User;
import com.getxinfo.support.util.IdUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    @Value("classpath:key.json")
    Resource keyFile;
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;

    @GetMapping("smscode")
    public ResponseResult<VerifyCodeResponse> sendSmsVerifyCode(@RequestParam String swissNumberStr) {
        if (isTelphoneValid(swissNumberStr)) {
            boolean sendInterval = testAndSetInterval(swissNumberStr);
            if (!sendInterval) {
                ValueOperations<String, String> valOps = redisTemplate.opsForValue();
                String code = IdUtil.generateCaptcha();
                valOps.set(Constants.SMS_CODE_PREFIX + swissNumberStr, code, 2, TimeUnit.MINUTES);

                return ResponseResult.ok(new VerifyCodeResponse(code));
            } else {
                return ResponseResult.error(ErrorCode.A0502);
            }
        } else {
            return ResponseResult.error(ErrorCode.A0151);
        }
    }

    public boolean testAndSetInterval(String swissNumberStr) {
        String intervalKey = Constants.SMS_SEND_INTERVAL_KEY + swissNumberStr;
        boolean within = redisTemplate.hasKey(intervalKey);
        if (!within) {
            ValueOperations<String, String> valOps = redisTemplate.opsForValue();
            valOps.set(intervalKey, "1", 1, TimeUnit.MINUTES);
        }
        return within;
    }

    public boolean isTelphoneValid(String swissNumberStr) {
        boolean valid = false;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, "CN");
            valid = phoneUtil.isValidNumber(swissNumberProto);
        } catch (NumberParseException e) {
            log.warn("NumberParseException was thrown: " + e.toString());
        }
        return valid;
    }

    @GetMapping("auth")
    public Object authentication(AuthParam authParam) throws JOSEException, ParseException, IOException {
        String swissNumberStr = authParam.getTelphone();
        String code = authParam.getCode();
        ValueOperations<String, String> valOps = redisTemplate.opsForValue();
        String expectCode = valOps.get(Constants.SMS_CODE_PREFIX + swissNumberStr);
        if (expectCode != null && expectCode.equals(code)) {
            redisTemplate.delete(Constants.SMS_CODE_PREFIX + swissNumberStr);

            User user = userService.findByTelphone(swissNumberStr);
            if (user == null) {
                User u = new User();
                u.setTelphone(swissNumberStr);
                user = userService.save(u);
            }
            String uid = String.valueOf(user.getId());
            String keyJson = StreamUtils.copyToString(keyFile.getInputStream(), StandardCharsets.UTF_8);
            RSAKey jwk = RSAKey.parse(keyJson);
            String accessToken = generateJwt(jwk, uid);
            return new AccessTokenResponse(accessToken);
        } else if (expectCode != null) {
            expireManyAttempts(swissNumberStr);
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError_code("A0131");
        return errorResponse;
    }

    public void expireManyAttempts(String swissNumberStr) {
        ValueOperations<String, String> valOps = redisTemplate.opsForValue();
        String failCountStr = valOps.get(Constants.CODE_FAIL_PREFIX + swissNumberStr);
        Long failCount = 0L;
        if (failCountStr != null) {
            failCount = Long.valueOf(failCountStr);
        }
        if (failCount >= 2) {
            redisTemplate.delete(Constants.SMS_CODE_PREFIX + swissNumberStr);
        } else {
            valOps.increment(Constants.CODE_FAIL_PREFIX + swissNumberStr);
        }
    }

    public String generateJwt(RSAKey rsaJWK, String uid) throws JOSEException {
        // Create RSA-signer with the private key
        JWSSigner signer = new RSASSASigner(rsaJWK);

// Prepare JWT with claims set
        Date now = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(uid)
                .issuer("https://getxinfo.com")
                .issueTime(now)
                .notBeforeTime(now)
                .expirationTime(new Date(new Date().getTime() + 24 * 60 * 60 * 1000))
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                claimsSet);

// Compute the RSA signature
        signedJWT.sign(signer);

// To serialize to compact form, produces something like
// eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
// mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
// maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
// -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
        String s = signedJWT.serialize();
        return s;
    }

}
