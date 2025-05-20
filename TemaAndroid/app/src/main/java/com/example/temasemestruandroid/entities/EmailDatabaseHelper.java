package com.example.temasemestruandroid.entities;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;

public class EmailDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "emails.db";
    private static final int DB_VERSION = 1;

    public EmailDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Emails(id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, subject TEXT, message TEXT)";
        db.execSQL(sql);

        // Seed data
        ContentValues cv = new ContentValues();
        cv.put("sender", "admin@server.com");
        cv.put("subject", "Bun venit!");
        cv.put("message", "Acesta este primul tău email.");
        db.insert("Emails", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Emails");
        onCreate(db);
    }

    public Cursor getAllEmails() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM Emails", null);
    }
}
