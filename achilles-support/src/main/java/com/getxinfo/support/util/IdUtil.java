package com.getxinfo.support.util;

import java.util.UUID;

public class IdUtil {

    public static String generateCaptcha() {
        String captcha = UUID.randomUUID().toString()
                .replaceAll("-", "")
                .replaceAll("[a-z|A-Z]", "")
                .substring(0, 6);
        return captcha;
    }

}
