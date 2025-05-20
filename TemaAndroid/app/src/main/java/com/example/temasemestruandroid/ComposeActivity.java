package com.example.temasemestruandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.temasemestruandroid.entities.DBHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ComposeActivity extends AppCompatActivity {

    EditText etTo, etSubject, etMessage;
    CheckBox cbImportant;
    RatingBar ratingBar;
    Button btnSend;
    DBHelper db;
    FirebaseFirestore firestore;
    String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etTo = findViewById(R.id.etTo);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        cbImportant = findViewById(R.id.cbImportant);
        ratingBar = findViewById(R.id.ratingBar);
        btnSend = findViewById(R.id.btnSend);
        MaterialButton btnBack = findViewById(R.id.btnBack);

        db = new DBHelper(this);
        firestore = FirebaseFirestore.getInstance();

        // Use consistent SharedPreferences key
        SharedPreferences prefs = getSharedPreferences("EmailAppPrefs", MODE_PRIVATE);
        sender = prefs.getString("loggedInUser", "");

        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            String to = etTo.getText().toString().trim();
            String subject = etSubject.getText().toString().trim();
            String message = etMessage.getText().toString().trim();
            boolean important = cbImportant.isChecked();
            float rating = ratingBar.getRating();

            if (to.isEmpty() || subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to local SQLite
            boolean localSuccess = db.insertEmail(sender, to, subject, message, important, rating);
            if (localSuccess) {
                Toast.makeText(this, "Email salvat local!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Eroare la salvarea locală!", Toast.LENGTH_SHORT).show();
            }

            // Save to Firebase
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("from", sender);
            emailData.put("to", to);
            emailData.put("subject", subject);
            emailData.put("message", message);
            emailData.put("important", important);
            emailData.put("rating", rating);
            emailData.put("timestamp", new Date());

            firestore.collection("emails").add(emailData)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Email trimis în Firebase!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ComposeActivity", "Firebase error: " + e.getMessage());
                        Toast.makeText(this, "Eroare la trimitere Firebase: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

            // Close the activity after attempting both saves
            finish();
        });
    }
}