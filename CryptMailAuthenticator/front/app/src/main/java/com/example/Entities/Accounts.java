package com.example.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

@Entity(
        tableName = "accounts",
        indices = {@Index(value = "email_address", unique = true)}  // Makes email unique
)
public class Accounts {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "email_address")
    public String emailAddress;

    @ColumnInfo(name = "email_owner")
    public String emailOwner;

    @ColumnInfo(name = "public_key")
    public String publicKey;

    @ColumnInfo(name = "private_key")
    public String privateKey;

    @ColumnInfo(name = "iv")
    public String iv;

    @ColumnInfo(name = "has_pending_request", defaultValue = "0")
    private boolean hasPendingRequest;

    public Accounts(String emailAddress, String emailOwner,
                    String publicKey, String privateKey, String iv) {
        this.emailAddress = emailAddress;
        this.emailOwner = emailOwner;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.iv=iv;
        this.hasPendingRequest = false; // Default value
    }

    public boolean hasPendingRequest() {
        return hasPendingRequest;
    }

    public void setHasPendingRequest(boolean hasPendingRequest) {
        this.hasPendingRequest = hasPendingRequest;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getEmailOwner() {
        return emailOwner;
    }

    public void setEmailOwner(String emailOwner) {
        this.emailOwner = emailOwner;
    }
}
