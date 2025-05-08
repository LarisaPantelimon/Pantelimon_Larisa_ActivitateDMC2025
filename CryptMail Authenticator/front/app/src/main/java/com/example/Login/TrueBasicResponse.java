package com.example.Login;

public class TrueBasicResponse {
    private boolean code;
    private String message;

    public TrueBasicResponse(boolean code, String message) {
        this.code = code;
        this.message = message;
    }

    public boolean isCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
