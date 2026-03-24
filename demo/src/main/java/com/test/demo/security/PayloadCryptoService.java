package com.test.demo.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PayloadCryptoService {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.crypto.secret}")
    private String secret;

    public PayloadCryptoService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String encrypt(Object input) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getAesKey(), new GCMParameterSpec(TAG_LENGTH_BITS, iv));

            String json = objectMapper.writeValueAsString(input);
            byte[] encrypted = cipher.doFinal(json.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(iv) + ":" +
                    Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt payload", e);
        }
    }

    public <T> T decrypt(String payload, Class<T> targetType) {
        try {
            byte[] jsonBytes = decryptToJsonBytes(payload);
            return objectMapper.readValue(jsonBytes, targetType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse decrypted payload", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt payload", e);
        }
    }

    public byte[] decryptToJsonBytes(String payload) {
        try {
            String[] parts = payload.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid encrypted payload format");
            }

            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] encrypted = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, getAesKey(), new GCMParameterSpec(TAG_LENGTH_BITS, iv));

            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt payload", e);
        }
    }

    private SecretKeySpec getAesKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }
}
