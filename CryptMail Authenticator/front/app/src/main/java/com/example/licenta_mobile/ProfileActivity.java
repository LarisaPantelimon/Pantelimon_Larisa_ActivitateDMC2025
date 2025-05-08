package com.example.licenta_mobile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.API.APIResponseCallback;
import com.example.API.APIService;
import com.example.Login.InfoUser;
import com.example.Login.TrueBasicResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvName, tvEmail, tvPhone, tvGender, tvBirthday;
    private EditText etName, etEmail, etPhone;
    private Button btnEditSave;
    private APIService apiService;
    private String currentEmail;
    private boolean isEditMode = false;
    private Spinner spGender;
    private TextInputLayout birthdayLayout;
    private TextInputEditText etBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupButton();
        handleIntent(getIntent());

        // Initialize Spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_options,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spGender.setAdapter(adapter);
        spGender.setOnTouchListener((v, event) -> {
            return !isEditMode;  // Only allow touch events in edit mode
        });
        birthdayLayout = findViewById(R.id.birthdayLayout);
        etBirthday = findViewById(R.id.et_birthday);

        // Setup date picker
        setupDatePicker();
    }

    private void setupDatePicker() {
        etBirthday.setOnClickListener(v -> {
            if (isEditMode) {  // Only show picker in edit mode
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        // Get current date from TextView
        final Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            Date date = sdf.parse(tvBirthday.getText().toString());
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            // Use current date if parsing fails
            calendar.setTimeInMillis(System.currentTimeMillis());
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                    String formattedDate = sdf.format(selectedDate.getTime());

                    // Update both views
                    etBirthday.setText(formattedDate);
                    tvBirthday.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Prevent future dates selection
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // Only show if in edit mode
        if (isEditMode) {
            datePickerDialog.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void initializeViews() {
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhone = findViewById(R.id.tv_phone);
        tvGender = findViewById(R.id.tv_gender);
        tvBirthday = findViewById(R.id.tv_birthday);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        spGender = findViewById(R.id.sp_gender);
        etBirthday = findViewById(R.id.et_birthday);

        btnEditSave = findViewById(R.id.btn_edit_save);
        spGender.setEnabled(false);
        apiService = new APIService(this);
    }

    private void setupButton() {
        btnEditSave.setOnClickListener(v -> {
            if (!isEditMode) {
                enterEditMode();
            } else {
                saveChanges();
            }
        });
    }

    private void enterEditMode() {
        isEditMode = true;
        btnEditSave.setText("Save");

        // Copy current values to EditTexts
        etName.setText(tvName.getText().toString());
        etEmail.setText(tvEmail.getText().toString());
        etPhone.setText(tvPhone.getText().toString());
        etBirthday.setText(tvBirthday.getText().toString());
        String currentGender = tvGender.getText().toString();
        if (!currentGender.isEmpty()) {
            int position = ((ArrayAdapter)spGender.getAdapter()).getPosition(currentGender);
            spGender.setSelection(position >= 0 ? position : 0);
        }

        // Update visibility controls
        spGender.setVisibility(View.VISIBLE);
        spGender.setEnabled(true);  // Enable interaction
        tvGender.setVisibility(View.GONE);
        birthdayLayout.setVisibility(View.VISIBLE);
        etBirthday.setClickable(true);
        tvBirthday.setVisibility(View.GONE);

        // Switch visibility
        setFieldsVisibility(View.GONE, View.VISIBLE);
    }

    private void exitEditMode() {
        isEditMode = false;
        btnEditSave.setText("Edit");
        tvGender.setText(spGender.getSelectedItem().toString());

        // Update visibility
        spGender.setVisibility(View.GONE);
        spGender.setEnabled(false);
        tvGender.setVisibility(View.VISIBLE);

        birthdayLayout.setVisibility(View.GONE);
        etBirthday.setClickable(false);
        tvBirthday.setVisibility(View.VISIBLE);

        setFieldsVisibility(View.VISIBLE, View.GONE);
    }

    private void setFieldsVisibility(int textViewVisibility, int editTextVisibility) {
        tvName.setVisibility(textViewVisibility);
        tvEmail.setVisibility(textViewVisibility);
        tvPhone.setVisibility(textViewVisibility);
        tvGender.setVisibility(textViewVisibility);
        tvBirthday.setVisibility(textViewVisibility);

        etName.setVisibility(editTextVisibility);
        etEmail.setVisibility(editTextVisibility);
        etPhone.setVisibility(editTextVisibility);
        spGender.setVisibility(editTextVisibility);
        etBirthday.setVisibility(editTextVisibility);
    }

    private void saveChanges() {
        InfoUser updatedUser = new InfoUser(currentEmail,"","","","",0);
        updatedUser.setName(etName.getText().toString());
        updatedUser.setEmail(etEmail.getText().toString());
        if (!etPhone.getText().toString().matches("^\\d{10}$")) {
            Toast.makeText(ProfileActivity.this,"Phone number must be exactly 10 digits",Toast.LENGTH_SHORT).show();
            return;
        }
        updatedUser.setPhone(etPhone.getText().toString());
        updatedUser.setGender(spGender.getSelectedItem().toString());
        updatedUser.setBirthday(etBirthday.getText().toString());

        apiService.updateInfoUser(updatedUser, new APIResponseCallback<TrueBasicResponse>() {
            @Override
            public void onSuccess(TrueBasicResponse success) {
                runOnUiThread(() -> {
//                    if (success.) {
                        // Update UI with new values
                        tvName.setText(updatedUser.getName());
                        tvEmail.setText(updatedUser.getEmail());
                        tvPhone.setText(updatedUser.getPhone());
                        tvGender.setText(updatedUser.getGender());
                        tvBirthday.setText(updatedUser.getBirthday());

                        exitEditMode();
                        Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
//                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    System.out.println("Error: " + errorMessage);
                    Toast.makeText(ProfileActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void handleIntent(Intent intent) {
        String email = extractEmail(intent);

        if (email == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!email.equals(currentEmail)) {
            currentEmail = email;
            fetchUserInfo(currentEmail);
        }
    }

    private String extractEmail(Intent intent) {
        if (intent != null && intent.hasExtra("USER_EMAIL")) {
            return intent.getStringExtra("USER_EMAIL");
        }
        return getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .getString("email", null);
    }

    private void fetchUserInfo(String email) {
        apiService.getInfoUser(email, new APIResponseCallback<InfoUser>() {
            @Override
            public void onSuccess(InfoUser user) {
                runOnUiThread(() -> updateUI(user));
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> showError(errorMessage));
            }
        });
    }

    private void updateUI(InfoUser user) {
        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhone());
        tvGender.setText(user.getGender());
        tvBirthday.setText(user.getBirthday());

        // Initialize edit fields with same values
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhone());
        if (user.getGender() != null) {
            int position = ((ArrayAdapter)spGender.getAdapter()).getPosition(user.getGender());
            spGender.setSelection(position >= 0 ? position : 0);
        }
        etBirthday.setText(user.getBirthday());
    }

    private void showError(String errorMessage) {
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}