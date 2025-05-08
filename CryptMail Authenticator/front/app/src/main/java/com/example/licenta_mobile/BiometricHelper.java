package com.example.licenta_mobile;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import java.security.KeyStore;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class BiometricHelper {
    private static final String TAG = "BiometricHelper";
    private static final String KEYSTORE_ALIAS = "com.example.licenta_mobile.ENCRYPTION_KEY";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = KeyProperties.KEY_ALGORITHM_AES + "/" +
            KeyProperties.BLOCK_MODE_CBC + "/" +
            KeyProperties.ENCRYPTION_PADDING_PKCS7;

    private final Context context;
    private final Executor executor;

    public BiometricHelper(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Authenticates the user with biometrics and initializes a Cipher for encryption or decryption.
     *
     * @param cipherMode Cipher.ENCRYPT_MODE for encryption, Cipher.DECRYPT_MODE for decryption
     * @param iv         Initialization Vector for decryption (null for encryption)
     * @param callback   Callback to handle success or error
     */
    public void authenticate(int cipherMode, byte[] iv, BiometricCallback callback) {
        try {
            SecretKey secretKey = getOrCreateSecretKey();
            if (secretKey == null) {
                Log.e(TAG, "Failed to retrieve or create SecretKey");
                callback.onError(new Exception("Unable to retrieve or create encryption key"));
                return;
            }

            // Initialize Cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            if (cipherMode == Cipher.DECRYPT_MODE && iv != null) {
                AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            } else if (cipherMode == Cipher.ENCRYPT_MODE) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } else {
                Log.e(TAG, "Invalid cipher mode: " + cipherMode);
                callback.onError(new IllegalArgumentException("Invalid cipher mode"));
                return;
            }

            // Configure BiometricPrompt
            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Authentication")
                    .setSubtitle("Authenticate to access your secure keys")
                    .setNegativeButtonText("Cancel")
                    .build();

            BiometricPrompt.CryptoObject cryptoObject = new BiometricPrompt.CryptoObject(cipher);

            // Start authentication
            new BiometricPrompt((FragmentActivity) context, executor,
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            BiometricPrompt.CryptoObject resultCrypto = result.getCryptoObject();
                            if (resultCrypto != null && resultCrypto.getCipher() != null) {
                                Log.d(TAG, "Authentication succeeded, providing Cipher in " +
                                        (cipherMode == Cipher.ENCRYPT_MODE ? "ENCRYPT_MODE" : "DECRYPT_MODE"));
                                callback.onSuccess(secretKey, resultCrypto.getCipher());
                            } else {
                                Log.e(TAG, "Cipher unavailable after authentication");
                                callback.onError(new Exception("Cipher not available after authentication"));
                            }
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            Log.e(TAG, "Authentication error: " + errString + " (code: " + errorCode + ")");
                            callback.onError(new Exception("Authentication error: " + errString));
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            Log.e(TAG, "Authentication failed");
                            callback.onError(new Exception("Biometric authentication failed"));
                        }
                    }).authenticate(promptInfo, cryptoObject);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set up biometric authentication", e);
            callback.onError(e);
        }
    }

    /**
     * Retrieves an existing SecretKey from the Android Keystore or creates a new one.
     *
     * @return SecretKey or null if creation/retrieval fails
     */
    private SecretKey getOrCreateSecretKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);

            // Check if key exists
            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
                Log.d(TAG, "Creating new SecretKey with alias: " + KEYSTORE_ALIAS);
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);

                KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                        KEYSTORE_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setKeySize(256)
                        .setUserAuthenticationRequired(true)
                        .setRandomizedEncryptionRequired(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    builder.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG);
                } else {
                    builder.setUserAuthenticationValidityDurationSeconds(0);
                }

                keyGenerator.init(builder.build());
                return keyGenerator.generateKey();
            }

            Log.d(TAG, "Retrieving existing SecretKey with alias: " + KEYSTORE_ALIAS);
            return (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get or create SecretKey", e);
            return null;
        }
    }

    public interface BiometricCallback {
        void onSuccess(SecretKey secretKey, Cipher cipher);
        void onError(Exception error);
    }
}