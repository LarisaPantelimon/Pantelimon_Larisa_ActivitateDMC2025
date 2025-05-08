package com.example.licenta_mobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.API.APIResponseCallback;
import com.example.API.APIService;
import com.example.Login.BasicResponse;
import com.example.Login.ForgotPasswordRequest;
import com.example.Login.ForgotPasswordResponse;
import com.example.Login.ResetPasswordRequest;
import com.example.Login.ResetCodeRequest;
import com.example.Login.TrueBasicResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText codeEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private MaterialButton submitButton;
    private TextView backToLoginText;
    private CircularProgressIndicator progressIndicator;
    private LinearLayout emailLayout;
    private LinearLayout codeLayout;
    private LinearLayout passwordLayout;
    private APIService apiService;
    private String email; // Store email for later steps
    private boolean isRequestInProgress = false;
    private long lastRequestTime = 0;
    private static final long MIN_REQUEST_INTERVAL_MS = 1000; // 30 seconds
    private enum State { EMAIL, CODE, PASSWORD }
    private State currentState = State.EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        apiService = new APIService(this);
        initializeViews();
        setupClickListeners();
        updateUIForState();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        codeEditText = findViewById(R.id.codeEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        submitButton = findViewById(R.id.submitButton);
        backToLoginText = findViewById(R.id.backToLoginText);
        progressIndicator = findViewById(R.id.progressIndicator);
        emailLayout = findViewById(R.id.emailLayout);
        codeLayout = findViewById(R.id.codeLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
    }

    private void setupClickListeners() {
        submitButton.setOnClickListener(v -> handleSubmit());
        backToLoginText.setOnClickListener(v -> navigateToLogin());
    }

    private void handleSubmit() {
        if (isRequestInProgress) {
            Toast.makeText(this, R.string.request_in_progress, Toast.LENGTH_SHORT).show();
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime < MIN_REQUEST_INTERVAL_MS) {
            Toast.makeText(this, R.string.please_wait_before_retry, Toast.LENGTH_SHORT).show();
            return;
        }

        hideKeyboard();
        switch (currentState) {
            case EMAIL:
                requestVerificationCode();
                break;
            case CODE:
                verifyCode();
                break;
            case PASSWORD:
                resetPassword();
                break;
        }
    }

    private void requestVerificationCode() {
        email = emailEditText.getText().toString().trim();

        if (!validateEmail(email)) {
            return;
        }

        showLoading(true);
        isRequestInProgress = true;
        lastRequestTime = System.currentTimeMillis();

        apiService.sendPasswordResetRequest(new ForgotPasswordRequest(email),
                new APIResponseCallback<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(ForgotPasswordResponse result) {
                        showLoading(false);
                        isRequestInProgress = false;
                        if (result != null && result.isSuccess()) {
                            handleCodeRequestSuccess(result);
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
                        showError(errorMessage != null ? errorMessage : getString(R.string.network_error));
                    }
                });
    }

    private void verifyCode() {
        String code = codeEditText.getText().toString().trim();

        if (!validateCode(code)) {
            return;
        }

        showLoading(true);
        isRequestInProgress = true;
        lastRequestTime = System.currentTimeMillis();

        apiService.verifyResetCode(new ResetCodeRequest(email, code),
                new APIResponseCallback<TrueBasicResponse>() {
                    @Override
                    public void onSuccess(TrueBasicResponse result) {
                        showLoading(false);
                        isRequestInProgress = false;
                        if (result != null) {
                            currentState = State.PASSWORD;
                            updateUIForState();
                            Toast.makeText(ForgotPasswordActivity.this, R.string.code_verified, Toast.LENGTH_LONG).show();
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
                        showError(errorMessage != null ? errorMessage : getString(R.string.network_error));
                    }
                });
    }

    private void resetPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!validatePasswords(newPassword, confirmPassword)) {
            return;
        }

        showLoading(true);
        isRequestInProgress = true;
        lastRequestTime = System.currentTimeMillis();

        apiService.resetPassword(new ResetPasswordRequest(email, newPassword),
                new APIResponseCallback<BasicResponse>() {
                    @Override
                    public void onSuccess(BasicResponse result) {
                        showLoading(false);
                        isRequestInProgress = false;
                        if (result != null) {
                            Toast.makeText(ForgotPasswordActivity.this, R.string.password_reset_success, Toast.LENGTH_LONG).show();
                            new Handler(Looper.getMainLooper()).postDelayed(() -> navigateToLogin(), 2000);
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
                        showError(errorMessage != null ? errorMessage : getString(R.string.network_error));
                    }
                });
    }

    private boolean validateEmail(String email) {
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
        return true;
    }

    private boolean validateCode(String code) {
        if (code.isEmpty()) {
            codeEditText.setError(getString(R.string.code_required));
            codeEditText.requestFocus();
            return false;
        }
        if (code.length() != 6 || !code.matches("\\d+")) {
            codeEditText.setError(getString(R.string.valid_code_required));
            codeEditText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validatePasswords(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty()) {
            newPasswordEditText.setError(getString(R.string.password_required));
            newPasswordEditText.requestFocus();
            return false;
        }
        if (newPassword.length() < 8) {
            newPasswordEditText.setError(getString(R.string.password_too_short));
            newPasswordEditText.requestFocus();
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.passwords_do_not_match));
            confirmPasswordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void handleCodeRequestSuccess(ForgotPasswordResponse response) {
        if (response.isEmailSent()) {
            Toast.makeText(this, R.string.verification_code_sent, Toast.LENGTH_LONG).show();
            currentState = State.CODE;
            updateUIForState();
        } else {
            showError(getString(R.string.unknown_error));
        }
    }

    private void updateUIForState() {
        emailLayout.setVisibility(currentState == State.EMAIL ? View.VISIBLE : View.GONE);
        codeLayout.setVisibility(currentState == State.CODE ? View.VISIBLE : View.GONE);
        passwordLayout.setVisibility(currentState == State.PASSWORD ? View.VISIBLE : View.GONE);

        switch (currentState) {
            case EMAIL:
                submitButton.setText(R.string.send_code);
                break;
            case CODE:
                submitButton.setText(R.string.verify_code);
                codeEditText.requestFocus();
                break;
            case PASSWORD:
                submitButton.setText(R.string.reset_password);
                newPasswordEditText.requestFocus();
                break;
        }
    }

    private void showLoading(boolean isLoading) {
        progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        submitButton.setEnabled(!isLoading);
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

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class)
                .putExtra("reset_success", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (currentState == State.CODE || currentState == State.PASSWORD) {
            currentState = State.EMAIL;
            updateUIForState();
        } else {
            navigateToLogin();
        }
    }
}