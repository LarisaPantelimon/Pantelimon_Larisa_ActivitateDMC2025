package com.example.temasemestruandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.temasemestruandroid.entities.EmailAdapter;
import com.example.temasemestruandroid.entities.DBHelper;
import com.example.temasemestruandroid.entities.Email;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Date;

public class InboxActivity extends AppCompatActivity {

    ListView listView;
    EmailAdapter adapter;
    ArrayList<Email> emailList = new ArrayList<>();
    DBHelper dbHelper;
    String username;
    FirebaseFirestore firestore;
    MaterialButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        listView = findViewById(R.id.listViewEmails);
        dbHelper = new DBHelper(this);
        firestore = FirebaseFirestore.getInstance();
        btnBack = findViewById(R.id.btnBack);

        // Use consistent SharedPreferences key
        SharedPreferences prefs = getSharedPreferences("EmailAppPrefs", MODE_PRIVATE);
        username = prefs.getString("loggedInUser", "");

        adapter = new EmailAdapter(this, emailList);
        listView.setAdapter(adapter);

        loadLocalEmails();   // Load sent emails from SQLite
        loadFirebaseEmails(); // Load received emails from Firebase

        btnBack.setOnClickListener(v -> finish());

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Email selectedEmail = emailList.get(position);
            Intent intent = new Intent(InboxActivity.this, EmailDetailActivity.class);
            intent.putExtra("email", selectedEmail);
            startActivity(intent);
        });
    }

    private void loadLocalEmails() {
        // Only load emails where the user is the sender
        Cursor cursor = dbHelper.getEmailsBySender(username);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
                String receiver = cursor.getString(cursor.getColumnIndexOrThrow("receiver"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                boolean important = cursor.getInt(cursor.getColumnIndexOrThrow("important")) == 1;
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));

                emailList.add(new Email(sender, receiver, subject, message, important, rating));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void loadFirebaseEmails() {
        firestore.collection("emails")
                .whereEqualTo("to", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String from = doc.getString("from");
                        String to = doc.getString("to");
                        String subject = doc.getString("subject");
                        String message = doc.getString("message");
                        boolean important = Boolean.TRUE.equals(doc.getBoolean("important"));
                        double rating = doc.getDouble("rating") != null ? doc.getDouble("rating") : 0;
                        Date timestamp = doc.getDate("timestamp");

                        Email email = new Email(from, to, subject, message, important, (float) rating);
                        emailList.add(email);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Eroare la Firebase", Toast.LENGTH_SHORT).show());
    }
}