package com.example.Login;

public class LoginRequest {
    private String email;
    private String password;
    private String tokenDevice;

    public LoginRequest(String email, String password, String tokenDevice) {
        this.email = email;
        this.password = password;
        this.tokenDevice=tokenDevice;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenDevice() {
        return tokenDevice;
    }

    public void setTokenDevice(String tokenDevice) {
        this.tokenDevice = tokenDevice;
    }
}
