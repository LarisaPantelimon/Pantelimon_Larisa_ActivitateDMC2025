package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private EditText editTextCuloare;
    private Switch impermeabilSwitch;
    private RadioGroup marimeGroup;
    private CheckBox materialCheckBox1, materialCheckBox2, materialCheckBox3;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Inițializarea câmpurilor
        editTextCuloare = findViewById(R.id.editTextText);
        impermeabilSwitch = findViewById(R.id.switch1);
        marimeGroup = findViewById(R.id.radioGroup);
        materialCheckBox1 = findViewById(R.id.checkBox);
        materialCheckBox2 = findViewById(R.id.checkBox2);
        materialCheckBox3 = findViewById(R.id.checkBox3);
        sendButton = findViewById(R.id.button2);

        // Setăm onClickListener pentru butonul "Send"
        sendButton.setOnClickListener(v -> {
            // Preluăm valorile din câmpuri
            String culoare = editTextCuloare.getText().toString();
            boolean impermeabil = impermeabilSwitch.isChecked();

            // Obținem mărimea selectată
            int selectedRadioButtonId = marimeGroup.getCheckedRadioButtonId();
            String marime = "";
            if (selectedRadioButtonId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                marime = selectedRadioButton.getText().toString();
            }
            String pret = "300 RON";  // Exemplu de preț

            // Verificăm materialele selectate
            StringBuilder material = new StringBuilder();
            if (materialCheckBox1.isChecked()) material.append(getString(R.string.material1));
            if (materialCheckBox2.isChecked()) material.append(getString(R.string.material2));
            if (materialCheckBox3.isChecked()) material.append(getString(R.string.material3));

            // Creăm obiectul Palton
            Palton palton = new Palton(culoare, impermeabil, marime, pret, material.toString());

            // Trimitem rezultatul înapoi în MainActivity
            Intent returnIntent = new Intent();
            returnIntent.putExtra("mesaj", "Culoare: " + palton.getCuloare() +
                    "\nImpermeabil: " + palton.isImpermeabil() +
                    "\nMărime: " + palton.getMarime() +
                    "\nPreț: " + palton.getPret() +
                    "\nMaterial: " + palton.getMaterial());
            setResult(RESULT_OK, returnIntent);
            finish();
        });
    }
}
