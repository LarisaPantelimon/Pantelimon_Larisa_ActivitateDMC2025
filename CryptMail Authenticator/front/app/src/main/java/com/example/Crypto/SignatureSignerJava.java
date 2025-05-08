package com.example.Crypto;

import android.os.Build;
import android.util.Log;

import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Hex;

public class SignatureSignerJava {
    private static final String TAG = "SignatureSignerJava";
    private static final int ZKP_ROUNDS = 40;

    public static String signString(String pemPrivateKey, String data) {
        try {
            if (pemPrivateKey == null || data == null) {
                Log.e(TAG, "Invalid input: pemPrivateKey or data is null");
                return null;
            }
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            return signBytes(pemPrivateKey, dataBytes);
        } catch (Exception e) {
            Log.e(TAG, "Failed to sign string: " + e.getMessage(), e);
            return null;
        }
    }

    private static String signBytes(String pemPrivateKey, byte[] data) throws Exception {
        PrivateKey privateKey = parsePemPrivateKey(pemPrivateKey);
        PSSParameterSpec pssSpec = new PSSParameterSpec(
                "SHA-512", "MGF1", new MGF1ParameterSpec("SHA-512"), 32, 1
        );
        Signature signer = Signature.getInstance("SHA512withRSA/PSS");
        signer.setParameter(pssSpec);
        signer.initSign(privateKey);
        signer.update(data);
        byte[] signature = signer.sign();
        return Hex.toHexString(signature);
    }

//    private static PrivateKey parsePemPrivateKey(String pemKey) throws Exception {
//        if (pemKey == null || pemKey.trim().isEmpty()) {
//            throw new IllegalArgumentException("PEM key is null or empty");
//        }
//        String cleanedKey = pemKey
//                .replace("-----BEGIN PRIVATE KEY-----", "")
//                .replace("-----END PRIVATE KEY-----", "")
//                .replaceAll("[\\r\\n]+", "");
//        byte[] encoded;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            encoded = Base64.getDecoder().decode(cleanedKey);
//        } else {
//            throw new UnsupportedOperationException("Base64 decoding requires API 26+");
//        }
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        return keyFactory.generatePrivate(keySpec);
//    }

//    public static String[] generateZKPResponses(String pemPrivateKey, String[] encryptedChallenges) throws Exception {
//        PrivateKey privateKey = parsePemPrivateKey(pemPrivateKey);
//        java.security.interfaces.RSAPrivateKey rsaPrivKey = (java.security.interfaces.RSAPrivateKey) privateKey;
//        BigInteger d = rsaPrivKey.getPrivateExponent();
//        BigInteger n = rsaPrivKey.getModulus();
//        Log.d(TAG, "Modulus: " + n.toString().substring(0, 20) + "...");
//
//        if (encryptedChallenges.length != ZKP_ROUNDS) {
//            throw new IllegalArgumentException("Expected " + ZKP_ROUNDS + " challenges, got " + encryptedChallenges.length);
//        }
//
//        String[] responses = new String[ZKP_ROUNDS];
//        for (int i = 0; i < ZKP_ROUNDS; i++) {
//            if (encryptedChallenges[i].equals("0")) {
//                responses[i] = "0"; // For c_i = 0
//                Log.d(TAG, "Index " + i + ": encC=0, s=0");
//                continue;
//            }
//            BigInteger encC = new BigInteger(encryptedChallenges[i], 16);
//            BigInteger s = encC.modPow(d, n);
//            Log.d(TAG, "Index " + i + ": encC=" + encC.toString(16) + ", s=" + s.toString(16));
//            responses[i] = s.toString(16);
//        }
//        return responses;
//    }

    public static String[] generateZKPResponses(String pemPrivateKey, String encM, String webPublicKeyPem, String cHex, String email) throws Exception {
        PrivateKey privateKey = parsePemPrivateKey(pemPrivateKey);
        java.security.interfaces.RSAPrivateKey rsaPrivKey = (java.security.interfaces.RSAPrivateKey) privateKey;
        BigInteger d = rsaPrivKey.getPrivateExponent();
        BigInteger n = rsaPrivKey.getModulus(); // n_app
        Log.d(TAG, "n_app: " + n.toString(16).substring(0, 20) + "...");

        // Extract n_web from PEM public key
        BigInteger nWeb = extractModulus(webPublicKeyPem); // n_web
        // Optional: Extract e_web (not currently used)
        // BigInteger eWeb = extractPublicExponent(webPublicKeyPem); // e_web
        Log.d(TAG, "n_web: " + nWeb.toString(16).substring(0, 20) + "...");

        BigInteger encMBig = new BigInteger(encM, 16); // encM
        BigInteger c = new BigInteger(cHex, 16); // c

        // Optional: Validate c by recomputing Hash(encM, n_web, email)
        String hashInput = encM + nWeb.toString(16) + email; // Use hex for n_web
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(hashInput.getBytes(StandardCharsets.UTF_8));
        BigInteger computedC = new BigInteger(1, hash).mod(n);
        if (!computedC.equals(c)) {
            Log.w(TAG, "Challenge c does not match computed hash: expected=" + computedC.toString(16) + ", got=" + cHex);
            // Proceed with provided c, or throw exception if strict validation needed
            // throw new Exception("Invalid challenge c");
        }

        // Homomorphic computation: encMC = encM^c mod n_web
        BigInteger encMC = encMBig.modPow(c, nWeb);

        // RSA proof: s = c^d_app mod n_app
        BigInteger s = c.modPow(d, n);

        Log.d(TAG, "encMC=" + encMC.toString(16) + ", s=" + s.toString(16));
        return new String[]{encMC.toString(16), s.toString(16)};
    }

    // Parse PEM public key
    public static RSAPublicKey parsePemPublicKey(String pemKey) throws Exception {
        // Clean PEM format
        String cleanedKey = pemKey
                .replace("\\n", "\n")   // Converts literal backslash-n to newline
                .replace("\r", "")      // Remove CR if present
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Remove all whitespace and newlines

        byte[] encoded = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            encoded = Base64.getDecoder().decode(cleanedKey);
        }

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Use system provider

        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    // Extract modulus (n)
    private static BigInteger extractModulus(String pemPublicKey) throws Exception {
        RSAPublicKey publicKey = parsePemPublicKey(pemPublicKey);
        return publicKey.getModulus(); // n
    }

    // Extract public exponent (e)
//    private static BigInteger extractPublicExponent(String pemPublicKey) throws Exception {
//        RSAPublicKey publicKey = parsePemPublicKey(pemPublicKey);
//        return publicKey.getPublicExponent(); // e
//    }

    // Parse PEM private key
    private static PrivateKey parsePemPrivateKey(String pemPrivateKey) throws Exception {
        try (PEMParser pemParser = new PEMParser(new StringReader(pemPrivateKey))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            if (object instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) object);
            } else if (object instanceof PEMKeyPair) {
                return converter.getKeyPair((PEMKeyPair) object).getPrivate();
            } else {
                throw new Exception("Invalid PEM private key format");
            }
        }
    }
}