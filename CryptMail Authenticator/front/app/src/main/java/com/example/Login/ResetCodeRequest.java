package com.example.Login;

public class ResetCodeRequest {
    private final String email;
    private final String code;

    public ResetCodeRequest(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }
}