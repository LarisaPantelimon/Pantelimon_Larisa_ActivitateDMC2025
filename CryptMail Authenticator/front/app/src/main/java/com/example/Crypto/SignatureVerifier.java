package com.example.Crypto;

import android.os.Build;
import android.util.Log;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HexFormat;

public class SignatureVerifier {
    private static final String TAG = "SignatureVerifier";
    private static final int HEX_RADIX = 16;

    static {
        // Add Bouncy Castle as a security provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static boolean verifyRSAPSSSignature(String pemPublicKey,
                                                String signatureHex,
                                                String originalMessage,
                                                int saltLength) {
        try {
            // Parse the public key
            PublicKey publicKey = parsePemPublicKey(pemPublicKey);
            RSAKeyParameters rsaKey = convertToRSAKeyParameters(publicKey);

            // Convert inputs
            byte[] signatureBytes = Hex.decode(signatureHex);
            byte[] messageBytes = Hex.decode(originalMessage);

            // Compute SHA-512 hash of the original message
            SHA512Digest digest = new SHA512Digest();
            byte[] messageHash = new byte[digest.getDigestSize()];
            digest.update(messageBytes, 0, messageBytes.length);
            digest.doFinal(messageHash, 0);
            System.out.println("Hashed message (Java): " + Hex.toHexString(messageHash));

            // Set up PSS signer
            PSSSigner signer = new PSSSigner(new RSAEngine(), digest, 32); // Salt length 32
            signer.init(false, rsaKey); // false for verification
            signer.update(messageHash, 0, messageHash.length);

            // Verify signature
            boolean result = signer.verifySignature(signatureBytes);
            System.out.println("Verification result: " + result);
            return result;

        } catch (Exception e) {
            System.err.println("Verification failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static RSAKeyParameters convertToRSAKeyParameters(PublicKey publicKey) throws Exception {
        if (!(publicKey instanceof java.security.interfaces.RSAPublicKey)) {
            throw new IllegalArgumentException("Not an RSA public key");
        }
        java.security.interfaces.RSAPublicKey rsaPub = (java.security.interfaces.RSAPublicKey) publicKey;
        return new RSAKeyParameters(false, rsaPub.getModulus(), rsaPub.getPublicExponent());
    }
    public static PublicKey parsePemPublicKey(String pemKey) throws Exception {
        // Clean PEM format
        String cleanedKey = pemKey
                .replace("\\n", "\n")   // Converts literal backslash-n to newline
                .replace("\r", "")      // Remove CR if present
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Remove all whitespace and newlines


        System.out.println("Raw: " + pemKey);
        System.out.println("Fixed: " + cleanedKey);

        byte[] encoded = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            encoded = Base64.getDecoder().decode(cleanedKey);
        }

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Use system provider

        return keyFactory.generatePublic(keySpec);
    }



    private static byte[] hexStringToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }

        // Use platform HexFormat if available (Android 14+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return HexFormat.of().parseHex(hex);
        }

        // Manual conversion for older versions
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            bytes[i] = (byte) Integer.parseInt(hex.substring(index, index + 2), HEX_RADIX);
        }
        return bytes;
    }
    private static void printByteArrayAsJson(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (int i = 0; i < bytes.length; i++) {
            sb.append("  \"" + i + "\": " + (bytes[i] & 0xFF));
            if (i != bytes.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("}");
        System.out.println(sb.toString());
    }

}