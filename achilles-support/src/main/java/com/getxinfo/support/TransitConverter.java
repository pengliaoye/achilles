package com.getxinfo.support;

import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;

import javax.persistence.AttributeConverter;

public class TransitConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String val) {
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Plaintext plaintext = Plaintext.of(val);
        String cipherText = vaultOps.opsForTransit().encrypt("user", plaintext).getCiphertext();
        return cipherText;
    }

    @Override
    public String convertToEntityAttribute(String val) {
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Ciphertext ciphertext = Ciphertext.of(val);
        String plaintext = vaultOps.opsForTransit().decrypt("user", ciphertext).asString();
        return plaintext;
    }

}