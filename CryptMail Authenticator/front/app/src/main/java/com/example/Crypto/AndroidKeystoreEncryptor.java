package com.example.Crypto;

import static android.content.ContentValues.TAG;

import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

public class AndroidKeystoreEncryptor {
    private static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/"
            + KeyProperties.BLOCK_MODE_CBC + "/"
            + KeyProperties.ENCRYPTION_PADDING_PKCS7;

    public static String encrypt(Key key, String plaintext) throws Exception {
        try {
            Log.d(TAG, "The goddamn key details: Algorithm = " + key.getAlgorithm() + ", Format = " + key.getFormat());

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(plaintext.getBytes());
            byte[] iv = cipher.getIV();

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

            // Extract IV (first 16 bytes for CBC)
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            // Extract encrypted data
            byte[] encrypted = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            throw new Exception("Decryption failed", e);
        }
    }
}