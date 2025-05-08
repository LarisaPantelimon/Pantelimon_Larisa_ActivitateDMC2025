package com.example.Crypto;

import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;

public class EncryptionHelper {
    private static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/"
            + KeyProperties.BLOCK_MODE_GCM + "/"
            + KeyProperties.ENCRYPTION_PADDING_NONE;
    private static final int GCM_TAG_LENGTH = 128;

    public static String encrypt(Key key, String plaintext) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // Initialize without IV - let Android handle it
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(plaintext.getBytes());
            byte[] iv = cipher.getIV(); // Get the generated IV

            // Combine IV and encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.encodeToString(combined, Base64.DEFAULT);
        } catch (Exception e) {
            throw new Exception("Encryption failed", e);
        }
    }

    public static String decrypt(Key key, String encryptedData) throws Exception {
        try {
            byte[] combined = Base64.decode(encryptedData, Base64.DEFAULT);

            // Extract IV (first 12 bytes for GCM)
            byte[] iv = new byte[12];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            // Extract encrypted data
            byte[] encrypted = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            throw new Exception("Decryption failed", e);
        }
    }
}