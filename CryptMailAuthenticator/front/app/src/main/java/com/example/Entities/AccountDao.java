package com.example.Entities;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Map;

@Dao
public interface AccountDao {
    @Insert
    void insert(Accounts account);

    @Update
    void update(Accounts account);

    @Delete
    void delete(Accounts account);

    // Get all accounts for the current owner
    @Query("SELECT * FROM accounts WHERE email_owner = :ownerEmail ORDER BY email_address ASC")
    LiveData<List<Accounts>> getAccountsByOwner(String ownerEmail);

    // Search accounts for the current owner
    @Query("SELECT * FROM accounts WHERE email_owner = :ownerEmail AND email_address LIKE :searchQuery")
    LiveData<List<Accounts>> searchAccounts(String ownerEmail, String searchQuery);

    // Delete specific account by email address for the current owner
    @Query("DELETE FROM accounts WHERE email_owner = :ownerEmail AND email_address = :emailAddress")
    void deleteByEmail(String ownerEmail, String emailAddress);

    // Get single account by both emails (for verification)
    @Query("SELECT * FROM accounts WHERE email_owner = :ownerEmail AND email_address = :emailAddress LIMIT 1")
    Accounts getAccount(String ownerEmail, String emailAddress);

    // Get all public keys for the current owner
    @Query("SELECT public_key FROM accounts WHERE email_owner = :ownerEmail")
    LiveData<List<String>> getPublicKeysByOwner(String ownerEmail);

    // Update public key for a specific account
    @Query("UPDATE accounts SET public_key = :publicKey WHERE email_owner = :ownerEmail AND email_address = :emailAddress")
    void updatePublicKey(String ownerEmail, String emailAddress, String publicKey);

    // Update encrypted private key for a specific account
    @Query("UPDATE accounts SET private_key = :encryptedPrivateKey WHERE email_owner = :ownerEmail AND email_address = :emailAddress")
    void updatePrivateKey(String ownerEmail, String emailAddress, String encryptedPrivateKey);

    @Query("SELECT private_key FROM accounts WHERE email_address = :email AND email_owner = :emailOwner")
    String getPrivateKey(String email, String emailOwner);

    @Query("SELECT iv FROM accounts WHERE email_address = :email AND email_owner = :emailOwner")
    String getIv(String email, String emailOwner);

}