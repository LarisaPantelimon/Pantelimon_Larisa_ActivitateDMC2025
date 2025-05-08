package com.example.licenta_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.API.APIService;
import com.example.API.APIResponseCallback;
import com.example.Login.BasicResponse;
import com.example.Login.PasswordResetRequest;
import com.example.Login.ResetPasswordRequest;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private Button resetButton;
    private String email;
    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // Get token from intent
        email = getIntent().getStringExtra("email");
        apiService = new APIService(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        resetButton = findViewById(R.id.resetButton);
    }

    private void setupClickListeners() {
        resetButton.setOnClickListener(v -> attemptPasswordReset());
    }

    private void attemptPasswordReset() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!validateInputs(newPassword, confirmPassword)) {
            return;
        }

        // Use confirmPasswordReset instead of resetPassword
        apiService.resetPassword(new ResetPasswordRequest(email,newPassword),
                new APIResponseCallback<BasicResponse>() {
                    @Override
                    public void onSuccess(BasicResponse response) {
                        if (response.isSuccess()) {
                            Toast.makeText(PasswordResetActivity.this,
                                    "Password reset successfully!",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PasswordResetActivity.this, LoginActivity.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(PasswordResetActivity.this,
                                    response.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(PasswordResetActivity.this,
                                "Password reset failed: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty()) {
            newPasswordEditText.setError("New password required");
            return false;
        }

        if (newPassword.length() < 8) {
            newPasswordEditText.setError("Password must be at least 8 characters");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Please confirm your password");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }
}