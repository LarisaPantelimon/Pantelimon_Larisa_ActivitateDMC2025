package com.example.Entities;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AccountsWebRepository {
    private final AccountsWebDao accountsWebDao;
    private final ExecutorService executorService;

    public AccountsWebRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        this.accountsWebDao = database.accountsWebDao();
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // Insert operations
    public void insert(AccountsWeb accountsWeb) {
        executorService.execute(() -> accountsWebDao.insert(accountsWeb));
    }

    public void insertAll(List<AccountsWeb> accountsWebs) {
        executorService.execute(() -> accountsWebDao.insertAll(accountsWebs));
    }

    // Delete operations
    public void deleteByEmail(String email) {
        executorService.execute(() -> accountsWebDao.deleteByEmail(email));
    }

    public void deleteAll() {
        executorService.execute(() -> accountsWebDao.deleteAll());
    }

    // Update operations
    public void updatePublicKey(String email, String publicKey) {
        executorService.execute(() -> accountsWebDao.updatePublicKey(email, publicKey));
    }

    // Query operations
    public LiveData<List<AccountsWeb>> getAllAccountsWeb() {
        return accountsWebDao.getAllAccountsWeb();
    }

    public LiveData<AccountsWeb> getAccountsWebByEmail(String email) {
        return accountsWebDao.getAccountsWebByEmail(email);
    }

    public LiveData<String> getPublicKeyForEmail(String email) {
        return Transformations.map(accountsWebDao.getAccountsWebByEmail(email),
                accountsWeb -> accountsWeb != null ? accountsWeb.getPublicKey() : null
        );
    }

    public void getPublicKeyByEmail(String email, Consumer<String> callback) {
        executorService.execute(() -> {
            String publicKey = accountsWebDao.getPubKey(email);
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(publicKey));
        });
    }

    // Combined account queries
    public LiveData<AccountWithWeb> getAccountWithWeb(String email) {
        return accountsWebDao.getAccountWithWeb(email);
    }

    public LiveData<List<AccountWithWeb>> getAllAccountsWithWeb() {
        return accountsWebDao.getAllAccountsWithWeb();
    }

    // Shutdown executor when no longer needed
    public void close() {
        executorService.shutdown();
    }
}
