package com.example.Login;

public class SendCredentials {
    private String email;
    private String ownerEmail;
    private String publicKey;
    private String action;

    public SendCredentials(String email, String ownerEmail, String publicKey, String action) {
        this.email = email;
        this.ownerEmail = ownerEmail;
        this.publicKey = publicKey;
        this.action=action;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
