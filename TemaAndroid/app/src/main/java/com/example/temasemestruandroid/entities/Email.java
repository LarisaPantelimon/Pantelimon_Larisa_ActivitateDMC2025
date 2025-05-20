package com.example.temasemestruandroid.entities;

import java.io.Serializable;

public class Email implements Serializable{
    public String sender;
    public String receiver;
    public String subject;
    public String message;
    public boolean important;
    public float rating;

    public Email(String sender, String receiver, String subject, String message, boolean important, float rating) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.message = message;
        this.important = important;
        this.rating = rating;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

