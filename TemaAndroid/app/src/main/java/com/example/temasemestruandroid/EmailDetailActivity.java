package com.example.temasemestruandroid;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temasemestruandroid.entities.Email;
import com.google.android.material.button.MaterialButton;

public class EmailDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);

        // Retrieve email from Intent
        Email email = (Email) getIntent().getSerializableExtra("email");

        // Find views
        TextView tvSender = findViewById(R.id.tvSender);
        TextView tvReceiver = findViewById(R.id.tvReceiver);
        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvBody = findViewById(R.id.tvBody);
        CheckBox cbImportant = findViewById(R.id.cbImportant);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        // Set email details
        if (email != null) {
            tvSender.setText(email.getSender());
            tvReceiver.setText(email.getReceiver());
            tvSubject.setText(email.getSubject());
            tvBody.setText(email.getMessage());
            cbImportant.setChecked(email.isImportant());
            ratingBar.setRating(email.getRating());
        }

        // Back button
        btnBack.setOnClickListener(v -> finish());
    }
}
