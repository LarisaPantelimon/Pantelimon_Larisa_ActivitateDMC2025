package com.example.Entities;

import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface AccountsWebDao {

    // Basic CRUD operations
    @Insert
    void insert(AccountsWeb accountsWeb);

    @Query("DELETE FROM accounts_web WHERE email_address = :email")
    void deleteByEmail(String email);

    @Query("UPDATE accounts_web SET public_key = :publicKey WHERE email_address = :email")
    void updatePublicKey(String email, String publicKey);

    @Query("SELECT public_key FROM accounts_web WHERE email_address=:email")
    String getPubKey(String email);

    // Query all web accounts
    @Query("SELECT * FROM accounts_web")
    LiveData<List<AccountsWeb>> getAllAccountsWeb();

    // Get specific web account by email
    @Query("SELECT * FROM accounts_web WHERE email_address = :email")
    LiveData<AccountsWeb> getAccountsWebByEmail(String email);

    // Combined queries with Accounts table (JOIN)
    @Transaction
    @Query("SELECT * FROM accounts INNER JOIN accounts_web " +
            "ON accounts.email_address = accounts_web.email_address " +
            "WHERE accounts.email_address = :email")
    LiveData<AccountWithWeb> getAccountWithWeb(String email);

    // Get all accounts with their web versions
    @Transaction
    @Query("SELECT * FROM accounts")
    LiveData<List<AccountWithWeb>> getAllAccountsWithWeb();

    // Bulk operations
    @Insert
    void insertAll(List<AccountsWeb> accountsWebs);

    @Query("DELETE FROM accounts_web")
    void deleteAll();
}

// Relationship class for JOIN queries
class AccountWithWeb {
    @Embedded
    public Accounts account;

    @Relation(
            parentColumn = "email_address",
            entityColumn = "email_address"
    )
    public AccountsWeb accountsWeb;
}