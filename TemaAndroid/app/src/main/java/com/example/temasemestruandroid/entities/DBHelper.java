package com.example.temasemestruandroid.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "EmailApp.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, full_name TEXT, birthday TEXT, gender TEXT, phone_number TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS emails (id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, receiver TEXT, subject TEXT, message TEXT, important INTEGER, rating REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS emails");
        onCreate(db);
    }

    public boolean registerUser(String username, String password, String fullName, String birthday, String gender, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", BCrypt.hashpw(password, BCrypt.gensalt()));
        cv.put("full_name", fullName);
        cv.put("birthday", birthday);
        cv.put("gender", gender);
        cv.put("phone_number", phoneNumber);
        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM users WHERE username=?", new String[]{username});
        if (cursor.moveToFirst()) {
            String storedHash = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            cursor.close();
            return BCrypt.checkpw(password, storedHash);
        }
        cursor.close();
        return false;
    }

    public boolean insertEmail(String from, String to, String subject, String message, boolean important, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sender", from);
        cv.put("receiver", to);
        cv.put("subject", subject);
        cv.put("message", message);
        cv.put("important", important ? 1 : 0);
        cv.put("rating", rating);
        return db.insert("emails", null, cv) != -1;
    }

    public Cursor getEmailsBySender(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM emails WHERE sender=?", new String[]{username});
    }

    public ArrayList<Email> getEmailsForUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Email> emailList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM emails WHERE sender=?", new String[]{username});
        while (cursor.moveToNext()) {
            String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
            String receiver = cursor.getString(cursor.getColumnIndexOrThrow("receiver"));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
            String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
            boolean important = cursor.getInt(cursor.getColumnIndexOrThrow("important")) == 1;
            float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
            emailList.add(new Email(sender, receiver, subject, message, important, rating));
        }

        cursor = db.rawQuery("SELECT * FROM emails WHERE receiver=?", new String[]{username});
        while (cursor.moveToNext()) {
            String sender = cursor.getString(cursor.getColumnIndexOrThrow("sender"));
            String receiver = cursor.getString(cursor.getColumnIndexOrThrow("receiver"));
            String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
            String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
            boolean important = cursor.getInt(cursor.getColumnIndexOrThrow("important")) == 1;
            float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
            emailList.add(new Email(sender, receiver, subject, message, important, rating));
        }

        cursor.close();
        return emailList;
    }

    public String[] getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"username", "full_name", "birthday", "gender", "phone_number"};
        String selection = "username = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String[] details = new String[5];
            details[0] = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            details[1] = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
            details[2] = cursor.getString(cursor.getColumnIndexOrThrow("birthday"));
            details[3] = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            details[4] = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
            cursor.close();
            return details;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}