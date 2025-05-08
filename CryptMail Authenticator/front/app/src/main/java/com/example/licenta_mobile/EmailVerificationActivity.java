package com.example.licenta_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.API.APIService;
import com.example.API.APIResponseCallback;
import com.example.API.RegisterRequest;
import com.example.Login.BasicResponse;
import com.example.Login.EmailVerificationRequest;

public class EmailVerificationActivity extends AppCompatActivity {

    private EditText verificationCodeEditText;
    private Button verifyButton;
    private TextView resendCodeText, emailTextView;
    private String email;
    private RegisterRequest registerRequest;
    private APIService apiService;
    private boolean isResending = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        apiService = new APIService(this);
        initializeViews();
        handleIntent();
        setupClickListeners();
    }

    private void initializeViews() {
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
        verifyButton = findViewById(R.id.verifyButton);
        resendCodeText = findViewById(R.id.resendCodeText);
        emailTextView = findViewById(R.id.emailTextView);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        registerRequest = (RegisterRequest) intent.getSerializableExtra("register_request");
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        emailTextView.setText(getString(R.string.verification_sent_to, email));
    }

    private void setupClickListeners() {
        verifyButton.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(verifyButton.getWindowToken(), 0);
            verifyCode();
        });

        resendCodeText.setOnClickListener(v -> {
            if (!isResending) {
                resendVerificationCode();
            }
        });
    }

    private void verifyCode() {
        String verificationCode = verificationCodeEditText.getText().toString().trim();
        if (verificationCode.isEmpty()) {
            verificationCodeEditText.setError("Verification code required");
            verificationCodeEditText.requestFocus();
            return;
        }

        verifyButton.setEnabled(false);

        EmailVerificationRequest request = new EmailVerificationRequest(email, verificationCode);
        apiService.verifyEmail(request, new APIResponseCallback<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse response) {
                verifyButton.setEnabled(true);
                if (response.isSuccess()) {
                    handleVerificationSuccess(response);
                } else {
                    showVerificationError(response.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                verifyButton.setEnabled(true);
                showVerificationError("Network error: " + errorMessage);
            }
        });
    }

    private void handleVerificationSuccess(BasicResponse response) {
        Intent resultIntent = new Intent()
                .putExtra("email", email)
                .putExtra("auth_token", response.getAuthToken());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showVerificationError(String message) {
        Toast.makeText(this, "Verification failed: " + message, Toast.LENGTH_SHORT).show();
        verificationCodeEditText.requestFocus();
    }

    private void resendVerificationCode() {
        isResending = true;
        resendCodeText.setEnabled(false);

        apiService.resendVerificationCode(email, new APIResponseCallback<BasicResponse>() {
            @Override
            public void onSuccess(BasicResponse response) {
                isResending = false;
                resendCodeText.setEnabled(true);
                if (response.isSuccess()) {
                    Toast.makeText(EmailVerificationActivity.this, "New verification code sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EmailVerificationActivity.this, "Failed: " + response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                isResending = false;
                resendCodeText.setEnabled(true);
                Toast.makeText(EmailVerificationActivity.this, "Failed to resend code: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent().putExtra("email", email);
        setResult(RESULT_CANCELED, resultIntent);
        super.onBackPressed();
    }
}