package com.example.Entities;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AccountViewModel extends AndroidViewModel {
    private AccountRepository repository;
    private final LiveData<List<Accounts>> userAccounts;
    private final LiveData<List<String>> publicKeys;

    public AccountViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountRepository(application);
        userAccounts = repository.getUserAccounts();
        publicKeys = repository.getPublicKeys();
    }

    public LiveData<List<Accounts>> getUserAccounts() {
        return userAccounts;
    }

    public LiveData<List<String>> getPublicKeys() {
        return publicKeys;
    }

    public void insert(Accounts account) {
        repository.insert(account);
    }

    public void update(Accounts account) {
        repository.update(account);
    }

    public void delete(Accounts account) {
        repository.delete(account);
    }

    public void deleteByEmail(String emailAddress) {
        repository.deleteByEmail(emailAddress);
    }

    public LiveData<List<Accounts>> searchAccounts(String query) {
        return repository.searchAccounts(query);
    }

    public void updatePublicKey(String emailAddress, String publicKey) {
        repository.updatePublicKey(emailAddress, publicKey);
    }

    public void updatePrivateKey(String emailAddress, String encryptedPrivateKey) {
        repository.updatePrivateKey(emailAddress, encryptedPrivateKey);
    }

    public Accounts getAccount(String emailAddress) {
        return repository.getAccount(emailAddress);
    }

    public String getCurrentUserEmail() {
        SharedPreferences prefs = getApplication().getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        return prefs.getString("userEmail", "");
    }
    public void getPrvKey(String email, String emailOwner, Consumer<PrivateKeyAndIv> callback) {
        Log.d("AccountViewModel", "Requesting key for email=" + email + ", ownerEmail=" + emailOwner);
        repository.getPrvKey(email, emailOwner, callback);
    }
}