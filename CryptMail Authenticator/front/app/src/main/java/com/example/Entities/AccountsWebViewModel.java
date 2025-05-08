package com.example.Entities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.function.Consumer;

// AccountsWebViewModel.java
public class AccountsWebViewModel extends AndroidViewModel {
    private AccountsWebRepository repository;
    private LiveData<String> publicKeyLiveData;

    public AccountsWebViewModel(@NonNull Application application) {
        super(application);
        repository = new AccountsWebRepository(application);
    }

    public LiveData<String> getPublicKeyForEmail(String email) {
        publicKeyLiveData = repository.getPublicKeyForEmail(email);
        return publicKeyLiveData;
    }

    public void getPublicKeyForEmail(String email, Consumer<String> callback) {
        repository.getPublicKeyByEmail(email, callback);
    }
}