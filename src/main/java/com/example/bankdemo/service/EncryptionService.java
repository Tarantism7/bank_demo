package com.example.bankdemo.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {
    private static final String SECRET = "1234567890123456";

    public String encrypt(String text) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(text.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed");
        }
    }

    public String decrypt(String text) {
        try {
            SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(
                    cipher.doFinal(Base64.getDecoder().decode(text)));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed");
        }
    }
}