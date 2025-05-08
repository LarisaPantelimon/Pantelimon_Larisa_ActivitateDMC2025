package com.example.Entities;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AccountRepository {
    private AccountDao accountDao;
    private LiveData<List<Accounts>> userAccounts;
    private String ownerEmail;

    public AccountRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        accountDao = db.accountsDao();

        // Get the logged-in user's email from SharedPreferences
        SharedPreferences prefs = application.getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        ownerEmail = prefs.getString("userEmail", "");

        // Initialize with accounts for the current owner
        userAccounts = accountDao.getAccountsByOwner(ownerEmail);
    }

    public LiveData<List<Accounts>> getUserAccounts() {
        return userAccounts;
    }

    public void insert(Accounts account) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Set owner email if not already set
            if (account.getEmailOwner() == null || account.getEmailOwner().isEmpty()) {
                account.setEmailOwner(ownerEmail);
            }
            accountDao.insert(account);
        });
    }

    public void update(Accounts account) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.update(account);
        });
    }

    public void delete(Accounts account) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.delete(account);
        });
    }

    public void deleteByEmail(String emailAddress) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.deleteByEmail(ownerEmail, emailAddress);
        });
    }

    public LiveData<List<Accounts>> searchAccounts(String query) {
        return accountDao.searchAccounts(ownerEmail, "%" + query + "%");
    }

    public LiveData<List<String>> getPublicKeys() {
        return accountDao.getPublicKeysByOwner(ownerEmail);
    }

    public void updatePublicKey(String emailAddress, String publicKey) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.updatePublicKey(ownerEmail, emailAddress, publicKey);
        });
    }

    public void updatePrivateKey(String emailAddress, String encryptedPrivateKey) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            accountDao.updatePrivateKey(ownerEmail, emailAddress, encryptedPrivateKey);
        });
    }

    public Accounts getAccount(String emailAddress) {
        return accountDao.getAccount(ownerEmail, emailAddress);
    }

    public void getPrvKey(String email, String emailOwner, Consumer<PrivateKeyAndIv> callback) {
        if (email == null || emailOwner == null || email.isEmpty() || emailOwner.isEmpty()) {
            Log.e("AccountRepository", "Invalid parameters: email=" + email + ", emailOwner=" + emailOwner);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(null));
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                String queryEmail = email.trim();
                String queryOwnerEmail = emailOwner.trim();
                //Log.d("AccountRepository", "Querying with email='" + queryEmail + "', emailOwner='" + queryOwnerEmail + "'");

                String privateKey = accountDao.getPrivateKey(queryEmail, queryOwnerEmail);
                //Log.d("AccountRepository", "PrivateKey result: " + (privateKey != null ? privateKey : "null"));

                String iv = accountDao.getIv(queryEmail, queryOwnerEmail);
                //Log.d("AccountRepository", "IV result: " + (iv != null ? iv : "null"));

                PrivateKeyAndIv result = (privateKey != null && iv != null) ? new PrivateKeyAndIv(privateKey, iv) : null;

                new Handler(Looper.getMainLooper()).post(() -> callback.accept(result));
            } catch (Exception e) {
                Log.e("AccountRepository", "Query failed: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> callback.accept(null));
            }
        });
    }

}