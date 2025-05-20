package com.example.temasemestruandroid;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.temasemestruandroid.entities.DBHelper;
import com.example.temasemestruandroid.entities.Email;
import com.example.temasemestruandroid.entities.EmailAdapter;

import java.util.ArrayList;

public class EmailListActivity extends AppCompatActivity {

    ListView emailListView;
    DBHelper db;
    ArrayList<Email> emailList;
    EmailAdapter adapter;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_list);

        emailListView = findViewById(R.id.emailListView);
        db = new DBHelper(this);

        // Obținem utilizatorul conectat
        user = getIntent().getStringExtra("username");
        if (user == null) user = "";

        // Preluăm toate emailurile pentru utilizatorul respectiv
        emailList = db.getEmailsForUser(user);

        // Setăm adapterul personalizat pentru ListView
        adapter = new EmailAdapter(this, emailList);
        emailListView.setAdapter(adapter);
    }
}
