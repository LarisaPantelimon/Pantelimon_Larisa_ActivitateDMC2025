package com.example.licenta_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.API.APIResponseCallback;
import com.example.API.APIService;
import com.example.Entities.Accounts;
import com.example.Entities.AppDatabase;
import com.example.Entities.TokenCallback;
import com.example.Login.LoginRequest;
import com.example.Login.LoginResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText emailEditText;
    private EditText passwordEditText;
    private MaterialButton loginButton;
    private TextView signUpText;
    private TextView forgotPasswordText;
    private CircularProgressIndicator progressIndicator;
    private boolean isRequestInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initializeViews();
        setupClickListeners();

        if (isUserLoggedIn()) {
            startMainActivity();
        }
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);
        forgotPasswordText = findViewById(R.id.forgotPassword);
        progressIndicator = findViewById(R.id.progressIndicator);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        signUpText.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        forgotPasswordText.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    public void fetchFCMToken(TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        // Fallback: proceed without token
                        callback.onTokenReceived(null);
                        return;
                    }
                    String token = task.getResult();
                    callback.onTokenReceived(token);
                });
    }

    private void attemptLogin() {
        if (isRequestInProgress) {
            Toast.makeText(this, R.string.request_in_progress, Toast.LENGTH_SHORT).show();
            return;
        }

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        hideKeyboard();
        showLoading(true);
        isRequestInProgress = true;

        // Update device token for push notifications
        fetchFCMToken(token -> {
            APIService apiService = new APIService(this);
            apiService.loginUser(new LoginRequest(email, password, token), new APIResponseCallback<LoginResponse>() {
                @Override
                public void onSuccess(LoginResponse result) {
                    showLoading(false);
                    isRequestInProgress = false;
                    if (result != null && result.isSuccess() && result.getAuthToken() != null) {
                        saveLoginState(result.getAuthToken(), email);
                        initializeOwnerAccount(email);
                        startMainActivity();
                    } else {
                        showError(result != null && result.getMessage() != null
                                ? result.getMessage()
                                : getString(R.string.unknown_error));
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    showLoading(false);
                    isRequestInProgress = false;
                    Log.e(TAG, "Login error: " + errorMessage);
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(R.string.error)
                            .setMessage(errorMessage != null ? errorMessage : getString(R.string.network_error))
                            .setPositiveButton(R.string.retry, (dialog, which) -> attemptLogin())
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            });
        });
    }

    private void initializeOwnerAccount(String email) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getDatabase(LoginActivity.this);
                Accounts existingAccount = db.accountsDao().getAccount(email, email);
                if (existingAccount == null) {
                    Accounts ownerAccount = new Accounts(
                            email,    // ownerEmail
                            email,    // emailAddress
                            "",       // publicKey
                            "",       // encryptedPrivateKey
                            ""        // ivBase64
                    );
                    db.accountsDao().insert(ownerAccount);
                    Log.d(TAG, "Created owner account for " + email);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize owner account: " + e.getMessage());
            }
        });
    }

    private void saveLoginState(String authToken, String email) {
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .edit()
                .putString("authToken", authToken)
                .putString("userEmail", email)
                .apply();
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError(getString(R.string.email_required));
            emailEditText.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError(getString(R.string.valid_email_required));
            emailEditText.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError(getString(R.string.password_required));
            passwordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void showLoading(boolean isLoading) {
        progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!isLoading);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        return prefs.contains("authToken") && prefs.contains("userEmail");
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}