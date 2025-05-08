package com.example.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "accounts_web",
        indices = {
                @Index(value = "email_address", unique = true, name = "idx_accounts_web_email_unique"),
                @Index(value = "email_address", name = "idx_accounts_web_email")
        },
        foreignKeys = @ForeignKey(
                entity = Accounts.class,
                parentColumns = "email_address",
                childColumns = "email_address",
                onDelete = CASCADE,
                onUpdate = CASCADE,
                deferred = true
        )
)
public class AccountsWeb {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "email_address")
    public String emailAddress;

    @ColumnInfo(name = "public_key")
    public String publicKey;

    // Constructor matching all non-auto-generated fields
    public AccountsWeb(String emailAddress, String publicKey) {
        this.emailAddress = emailAddress;
        this.publicKey = publicKey;
    }

    // Empty constructor required by Room
//    public AccountsWeb() {}

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
}