package com.example.licenta_mobile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.API.APIResponseCallback;
import com.example.API.APIService;
import com.example.API.RegisterRequest;
import com.example.API.RegisterResponse;
import com.example.Entities.TokenCallback;
import com.example.Login.BasicResponse;
import com.example.Login.TrueBasicResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private TextInputEditText fullNameEditText, birthdayEditText, phoneEditText;
    private AutoCompleteTextView genderAutoComplete;
    private MaterialButton signUpButton;
    private TextView loginText;
    private CircularProgressIndicator progressIndicator;
    private static final int REQUEST_CODE_VERIFY_EMAIL = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        setupGenderDropdown();
        setupBirthdayPicker();
        setupPhoneNumberFormatting();
        setupClickListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        fullNameEditText = findViewById(R.id.FullNameEditText);
        genderAutoComplete = findViewById(R.id.genderAutoComplete);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginText = findViewById(R.id.loginText);
        progressIndicator = findViewById(R.id.progressIndicator);
    }

    private void setupGenderDropdown() {
        String[] genders = {"Male", "Female", "Other", "Prefer not to say"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, genders);
        genderAutoComplete.setAdapter(adapter);
        genderAutoComplete.setKeyListener(null);
        genderAutoComplete.setOnClickListener(v -> genderAutoComplete.showDropDown());
        genderAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGender = adapter.getItem(position);
            genderAutoComplete.setText(selectedGender, false);
        });
    }

    private void setupBirthdayPicker() {
        birthdayEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        birthdayEditText.setText(selectedDate);
                    },
                    year, month, day
            );
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });
    }

    private void setupPhoneNumberFormatting() {
        // Restrict input to digits only
        InputFilter digitFilter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        phoneEditText.setFilters(new InputFilter[]{digitFilter, new InputFilter.LengthFilter(10)});

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                phoneEditText.removeTextChangedListener(this);
                String phone = s.toString().replaceAll("[^\\d]", "");
                if (phone.length() > 10) {
                    phone = phone.substring(0, 10);
                }
                if (!phone.equals(s.toString().trim())) {
                    s.replace(0, s.length(), phone);
                }
                phoneEditText.setSelection(phoneEditText.getText().length());
                phoneEditText.addTextChangedListener(this);
            }
        });
    }

    private void setupClickListeners() {
        signUpButton.setOnClickListener(v -> attemptSignUp());
        loginText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    public void fetchFCMToken(TokenCallback callback) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        callback.onTokenReceived(null);
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM Token", token);
                    callback.onTokenReceived(token);
                });
    }

    private void attemptSignUp() {
        String email = emailEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String gender = genderAutoComplete.getText().toString().trim();
        String birthday = birthdayEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim().replaceAll("[^\\d]", "");
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (!validateInputs(email, fullName, gender, birthday, phone, password, confirmPassword)) {
            return;
        }

        showLoading(true);

        fetchFCMToken(token -> {
            RegisterRequest request = new RegisterRequest(
                    email, password, fullName, gender, birthday, phone, token
            );

            APIService apiService = new APIService(this);
            apiService.registerUser(request, new APIResponseCallback<RegisterResponse>() {
                @Override
                public void onSuccess(RegisterResponse response) {
                    showLoading(false);
                    if (response.isSuccess()) {
                        handleSignUpSuccess(response, request);
                    } else {
                        showError(response.getMessage());
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    showLoading(false);
                    showError(errorMessage);
                }
            });
        });
    }

    private boolean validateInputs(String email, String fullName, String gender,
                                   String birthday, String phone, String password,
                                   String confirmPassword) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email required");
            return false;
        }
        if (fullName.isEmpty()) {
            fullNameEditText.setError("Full name is required");
            return false;
        }
        if (gender.isEmpty()) {
            genderAutoComplete.setError("Gender is required");
            return false;
        }
        if (birthday.isEmpty()) {
            birthdayEditText.setError("Birthday is required");
            return false;
        }
        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number is required");
            return false;
        }
        String phoneDigits = phone.replaceAll("[^\\d]", "");
        if (phoneDigits.length() != 10) {
            phoneEditText.setError("Phone must be 10 digits (0XXXXXXXXX)");
            return false;
        }
        if (!phoneDigits.startsWith("0")) {
            phoneEditText.setError("Phone must start with 0");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }
        if (password.length() < 8) {
            passwordEditText.setError("Password must be at least 8 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords must match");
            return false;
        }
        return true;
    }

    private void handleSignUpSuccess(RegisterResponse response, RegisterRequest request) {
        String userEmail = emailEditText.getText().toString().trim();
        Intent intent = new Intent(this, EmailVerificationActivity.class)
                .putExtra("email", userEmail)
                .putExtra("register_request", (Serializable) request);
        startActivityForResult(intent, REQUEST_CODE_VERIFY_EMAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VERIFY_EMAIL) {
            if (resultCode == RESULT_OK && data != null) {
                String userEmail = data.getStringExtra("email");
                String authToken = data.getStringExtra("auth_token");
                if (userEmail != null) {
                    saveAuthData(authToken, userEmail);
                    Toast.makeText(this, "Email verified, account created", Toast.LENGTH_SHORT).show();
                    startMainActivity();
                } else {
                    showError("Verification completed, but email missing");
                }
            } else {
                showError("Email verification canceled or failed");
                String email = data != null ? data.getStringExtra("email") : null;
                deleteTemporaryRegistration(email);
            }
        }
    }

    private void deleteTemporaryRegistration(String email) {
        if (email == null) return;
        APIService apiService = new APIService(this);
        apiService.deleteAccount(email, new APIResponseCallback<TrueBasicResponse>() {
            @Override
            public void onSuccess(TrueBasicResponse response) {
                Log.d("SignUpActivity", "Temporary registration deleted for " + email);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("SignUpActivity", "Failed to delete temporary registration: " + errorMessage);
            }
        });
    }

    private void saveAuthData(String authToken, String email) {
        getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .edit()
                .putString("authToken", authToken)
                .putString("userEmail", email)
                .apply();
    }

    private void showLoading(boolean isLoading) {
        progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        signUpButton.setEnabled(!isLoading);
    }

    private void showError(String message) {
        Toast.makeText(this, "Sign up failed: " + message, Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        System.out.println("A apasat tasta BACK");
        deleteTemporaryRegistration(emailEditText.getText().toString().trim());
        super.onBackPressed();
    }
}