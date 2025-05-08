package com.example.Login;

public class InfoUser {
    private String emailaddress;
    private String fullName;
    private String gender;
    private String phoneNumber;
    private String birthday;

    private Integer statusCode;

    public InfoUser(String emailaddress, String fullName, String gender, String phoneNumber, String birthday, Integer statusCode) {
        this.emailaddress = emailaddress;
        this.fullName = fullName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.statusCode = statusCode;
    }

    public String getEmail() {
        return emailaddress;
    }

    public void setEmail(String emailaddress) {
        this.emailaddress = emailaddress;
    }

    public String getName() {
        return fullName;
    }

    public void setName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public void setPhone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
