package com.example.Crypto;

import android.content.Context;

import com.example.Entities.Accounts;
import com.example.Entities.AccountsWeb;
import com.example.Entities.AppDatabase;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.*;

public class RSAKeyGenerator {

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(4096); // Same as your JavaScript version
        return generator.generateKeyPair();
    }

    public static String convertToPEM(Key key) throws Exception {
        StringWriter writer = new StringWriter();
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
            if (key instanceof PrivateKey) {
                pemWriter.writeObject(new JcaPKCS8Generator((PrivateKey) key, null));
            } else {
                pemWriter.writeObject(key);
            }
        }
        return writer.toString();
    }

}
