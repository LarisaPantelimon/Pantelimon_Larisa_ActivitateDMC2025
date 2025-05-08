package com.example.Login;

public class ForgotPasswordResponse {
    private boolean success;
    private String message;
    private boolean emailSent;
    private String resetToken;  // Optional, only if implementing immediate reset
    private long tokenExpiry;  // Optional, token expiry timestamp

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public long getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(long tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }
}
