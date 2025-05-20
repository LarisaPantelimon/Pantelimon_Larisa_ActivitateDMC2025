package com.example.temasemestruandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.temasemestruandroid.entities.DBHelper;
import com.google.android.material.button.MaterialButton;

public class UserDetailsActivity extends AppCompatActivity {
    private TextView tvUsername, tvFullName, tvBirthday, tvGender, tvPhoneNumber;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvGender = findViewById(R.id.tvGender);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        MaterialButton btnBack = findViewById(R.id.btnBack);
        db = new DBHelper(this);

        // Retrieve username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("EmailAppPrefs", MODE_PRIVATE);
        String username = prefs.getString("loggedInUser", null);
        btnBack.setOnClickListener(v -> finish());

        if (username != null) {
            // Fetch user details from DBHelper
            String[] userDetails = db.getUserDetails(username);
            if (userDetails != null) {
                tvUsername.setText("Email: " + userDetails[0]);
                tvFullName.setText("Nume complet: " + userDetails[1]);
                tvBirthday.setText("Data nașterii: " + userDetails[2]);
                tvGender.setText("Gen: " + userDetails[3]);
                tvPhoneNumber.setText("Număr telefon: " + userDetails[4]);
            } else {
                Toast.makeText(this, "Utilizatorul nu a fost găsit!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Niciun utilizator autentificat!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
