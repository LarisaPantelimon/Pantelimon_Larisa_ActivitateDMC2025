package com.example.API;

public class RegisterResponse {
    private String message;
    private boolean success; // New field
    private boolean verificationRequired; // New field
    private String authToken; // New field

    // Constructor
    public RegisterResponse(String message, boolean success,
                            boolean verificationRequired, String authToken) {
        this.message = message;
        this.success = success;
        this.verificationRequired = verificationRequired;
        this.authToken = authToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success; // Now returns the actual value from API
    }

    public String getAuthToken() {
        return authToken; // Returns the actual token or null
    }

    public boolean isVerificationRequired() {
        return verificationRequired; // Returns actual API value
    }
}