package com.example.lab4;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText editTextSize;
    private Spinner spinnerColor;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextSize = findViewById(R.id.editTextSize);
        spinnerColor = findViewById(R.id.spinnerColor);
        buttonSave = findViewById(R.id.buttonSave);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.color_options, android.R.layout.simple_spinner_item
        );
        spinnerColor.setAdapter(adapter);

        buttonSave.setOnClickListener(v -> {
            String dimensiune = editTextSize.getText().toString();
            String culoare = spinnerColor.getSelectedItem().toString();

            SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("text_size", dimensiune);
            editor.putString("text_color", culoare);
            editor.apply();

            Toast.makeText(this, "Setări salvate!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

