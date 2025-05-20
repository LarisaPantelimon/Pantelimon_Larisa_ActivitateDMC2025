package com.example.temasemestruandroid;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.temasemestruandroid.entities.DBHelper;

import java.util.Calendar;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    EditText etUsername, etPassword, etConfirmPassword, etFullName, etPhoneNumber;
    Spinner spGender;
    Button btnRegister, btnSelectBirthday;
    DBHelper db;
    String selectedBirthday;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spGender = findViewById(R.id.spGender);
        btnRegister = findViewById(R.id.btnRegister);
        btnSelectBirthday = findViewById(R.id.btnSelectBirthday);
        db = new DBHelper(this);

        // Set up gender spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(adapter);

        // Set up birthday picker
        btnSelectBirthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegisterActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedBirthday = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        btnSelectBirthday.setText(selectedBirthday);
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String gender = spGender.getSelectedItem().toString();

            // Validate email format
            if (!EMAIL_PATTERN.matcher(username).matches()) {
                Toast.makeText(this, "Introdu un email valid!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    fullName.isEmpty() || phoneNumber.isEmpty() || selectedBirthday == null) {
                Toast.makeText(this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Parolele nu se potrivesc!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.registerUser(username, password, fullName, selectedBirthday, gender, phoneNumber)) {
                Toast.makeText(this, "Înregistrare reușită!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Email-ul există deja!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}