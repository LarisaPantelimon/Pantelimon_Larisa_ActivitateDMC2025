package com.example.Entities;

public class PrivateKeyAndIv {
    private final String privateKey;
    private final String iv;

    public PrivateKeyAndIv(String privateKey, String iv) {
        this.privateKey = privateKey;
        this.iv = iv;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getIv() {
        return iv;
    }
}