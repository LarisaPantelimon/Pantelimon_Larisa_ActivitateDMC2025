package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1; // Codul de rezultat pentru activitatea MainActivity2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAdaugaPalton = findViewById(R.id.button);

        // Deschidem activitatea MainActivity2 atunci când butonul este apăsat
        btnAdaugaPalton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deschide activitatea MainActivity2 cu startActivityForResult
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    // Preia rezultatul din MainActivity2
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Preluăm rezultatul trimis din MainActivity2
                String mesaj = data.getStringExtra("mesaj");

                // Afisăm mesajul cu Toast
                Toast.makeText(MainActivity.this, mesaj, Toast.LENGTH_SHORT).show();

                // Preluăm fiecare valoare din mesaj
                String[] values = mesaj.split("\n");

                // Setăm valorile în TextBox-uri (EditText)
                TextView editTextCuloare = findViewById(R.id.textView);
                editTextCuloare.setText(values[0].replace("Culoare: ", ""));

                TextView editTextImpermeabil = findViewById(R.id.textView2);
                editTextImpermeabil.setText(values[1].replace("Impermeabil: ", ""));

                TextView editTextMarime = findViewById(R.id.textView3);
                editTextMarime.setText(values[2].replace("Mărime: ", ""));

                TextView editTextPret = findViewById(R.id.textView4);
                editTextPret.setText(values[3].replace("Preț: ", ""));

                TextView editTextMaterial = findViewById(R.id.textView5);
                editTextMaterial.setText(values[4].replace("Material: ", ""));
            }
        }
    }

}
