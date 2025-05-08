package com.example.Login;

public class SendZPK {
    private String[] finalZPK;
    private String email;
    private String ownerEmail;
    private String requestId;

    public SendZPK(String[] finalZPK, String email, String ownerEmail, String requestId) {
        this.finalZPK = finalZPK;
        this.email = email;
        this.ownerEmail = ownerEmail;
        this.requestId=requestId;
    }

    public String[] getFinalZPK() {
        return finalZPK;
    }

    public void setFinalZPK(String[] finalZPK) {
        this.finalZPK = finalZPK;
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
}
