package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String mesaj_implicit = getString(R.string.mesaj_implicit);
            String message = extras.getString("message", mesaj_implicit);
            int num1 = extras.getInt("num1", 0);
            int num2 = extras.getInt("num2", 0);

            String toastMessage = "Mesaj: " + message + "\nNum1: " + num1 + "\nNum2: " + num2;
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();

            TextView textViewNum1 = findViewById(R.id.textView);
            TextView textViewNum2 = findViewById(R.id.textView2);
            textViewNum1.setText("Num1: " + num1);
            textViewNum2.setText("Num2: " + num2);

            Button buttonSendToActivity2 = findViewById(R.id.button3);
            buttonSendToActivity2.setOnClickListener(view -> {
                // Calculăm suma celor două numere
                int sum = num1 + num2;

                Intent intent = new Intent();

                intent.putExtra("sum", sum);

                setResult(RESULT_OK,intent);
                finish();
            });
        }
    }
}