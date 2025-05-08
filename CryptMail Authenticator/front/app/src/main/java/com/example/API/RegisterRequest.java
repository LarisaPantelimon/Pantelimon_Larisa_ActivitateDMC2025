package com.example.API;

import java.io.Serializable;

public class RegisterRequest implements Serializable {
    private String email;
    private String password;
    private String fullName;
    private String gender;
    private String birthday;
    private String phone;
    private String tokenDevice;

    // Constructor
    public RegisterRequest(String email, String password, String fullName, String gender, String birthday, String phone, String tokenDevice) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.birthday = birthday;
        this.phone = phone;
        this.tokenDevice = tokenDevice;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPhone() {
        return phone;
    }

    public String getTokenDevice() {
        return tokenDevice;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTokenDevice(String tokenDevice) {
        this.tokenDevice = tokenDevice;
    }
}