package com.example.lab4;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab4.Entities.AppDatabase;
import com.example.lab4.Entities.PaltonDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    private TextView textViewSelectedDate;
    private EditText editTextCuloare;
    private EditText editTextPret;
    private Switch switchImpermeabil;
    private Date selectedDate;
    private Palton paltonToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Legătura între view-uri și componente
        Button buttonSelectDate = findViewById(R.id.buttonSelectDate);
        Button buttonSave = findViewById(R.id.button2);
        textViewSelectedDate = findViewById(R.id.textView7);
        editTextCuloare = findViewById(R.id.editTextText);
        //editTextPret = findViewById(R.id.textView4);
        switchImpermeabil = findViewById(R.id.switch1);

        // Extragem obiectul Palton din Intent dacă există (pentru editare)
        paltonToEdit = getIntent().getParcelableExtra("palton");

        // Inițializăm data curentă dacă nu există un palton de editat
        if (paltonToEdit != null) {
            // Prepopulăm câmpurile cu valorile obiectului existent
            editTextCuloare.setText(paltonToEdit.getCuloare());
            //editTextPret.setText(paltonToEdit.getPret());
            switchImpermeabil.setChecked(paltonToEdit.isImpermeabil());

            // Prepopulăm CheckBox-urile pentru material
            CheckBox checkBoxLana = findViewById(R.id.checkBox);
            CheckBox checkBoxBumbac = findViewById(R.id.checkBox2);
            CheckBox checkBoxLanaBumbac = findViewById(R.id.checkBox3);

            checkBoxLana.setChecked(false);
            checkBoxBumbac.setChecked(false);
            checkBoxLanaBumbac.setChecked(false);

            String material = paltonToEdit.getMaterial();
            Log.d("Materialul meu ramas este:", "Material " + material);
            checkBoxLana.setChecked(material.contains("Lana"));
            checkBoxBumbac.setChecked(material.contains("Bumbac"));
            checkBoxLanaBumbac.setChecked(material.contains("Poliester"));

            // Prepopulăm mărimea
            String marime = paltonToEdit.getMarime();
            RadioButton radioButton1 = findViewById(R.id.radiobutton1);
            RadioButton radioButton2 = findViewById(R.id.radiobutton2);
            RadioButton radioButton3 = findViewById(R.id.radiobutton3);
            radioButton1.setChecked(false);
            radioButton2.setChecked(false);
            radioButton3.setChecked(false);

            if (marime.equals("S")) {
                radioButton1.setChecked(true);
            } else if (marime.equals("M")) {
                radioButton2.setChecked(true);
            } else if (marime.equals("L")) {
                radioButton3.setChecked(true);
            }

            // Preluăm data existentă
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                selectedDate = dateFormat.parse(paltonToEdit.getDataAdaugare());
            } catch (ParseException e) {
                e.printStackTrace();
                selectedDate = Calendar.getInstance().getTime();
            }
            textViewSelectedDate.setText("Data selectată: " + dateFormat.format(selectedDate));
            setTitle("Editare Palton");
        } else {
            setTitle("Adăugare Palton");
            selectedDate = Calendar.getInstance().getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            textViewSelectedDate.setText("Data selectată: " + dateFormat.format(selectedDate));
        }

        // Configurăm comportamentul butonului de selectare a datei
        buttonSelectDate.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            int year = today.get(Calendar.YEAR);
            int month = today.get(Calendar.MONTH);
            int day = today.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity2.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar selectedCal = Calendar.getInstance();
                        selectedCal.set(selectedYear, selectedMonth, selectedDay);
                        selectedDate = selectedCal.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        textViewSelectedDate.setText("Data selectată: " + dateFormat.format(selectedDate));
                    },
                    year, month, day
            );

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // Salvăm obiectul Palton (fie adăugăm, fie modificăm)
        buttonSave.setOnClickListener(v -> {
            String culoare = editTextCuloare.getText().toString();
            //String pret = editTextPret.getText().toString();
            boolean impermeabil = switchImpermeabil.isChecked();

            // Construim materialul din CheckBox-uri
            CheckBox checkBoxLana = findViewById(R.id.checkBox);
            CheckBox checkBoxBumbac = findViewById(R.id.checkBox2);
            CheckBox checkBoxLanaBumbac = findViewById(R.id.checkBox3);

            StringBuilder materialBuilder = new StringBuilder();
            if (checkBoxLana.isChecked()) {
                materialBuilder.append("Lana, ");
            }
            if (checkBoxBumbac.isChecked()) {
                materialBuilder.append("Bumbac, ");
            }
            if (checkBoxLanaBumbac.isChecked()) {
                materialBuilder.append("Poliester, ");
            }
            if (materialBuilder.length() > 0) {
                materialBuilder.setLength(materialBuilder.length() - 2);
            }
            String material = materialBuilder.toString();

            // Validăm câmpurile
            if (culoare.isEmpty() ) {
                Toast.makeText(this, "Completați culoarea și prețul!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (material.isEmpty()) {
                Toast.makeText(this, "Selectați cel puțin un material!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obținem mărimea selectată
            RadioButton radioButton1 = findViewById(R.id.radiobutton1);
            RadioButton radioButton2 = findViewById(R.id.radiobutton2);
            RadioButton radioButton3 = findViewById(R.id.radiobutton3);

            String marime = "";
            if (radioButton1.isChecked()) {
                marime = "S";
            } else if (radioButton2.isChecked()) {
                marime = "M";
            } else if (radioButton3.isChecked()) {
                marime = "L";
            }

            if (marime.isEmpty()) {
                Toast.makeText(this, "Selectați o mărime!", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = formatter.format(selectedDate);

            // Dacă edităm un obiect existent, actualizăm valorile acestuia
            if (paltonToEdit != null) {
                paltonToEdit.setCuloare(culoare);
                paltonToEdit.setPret("300");
                paltonToEdit.setMaterial(material);
                paltonToEdit.setImpermeabil(impermeabil);
                paltonToEdit.setMarime(marime);
                paltonToEdit.setDataAdaugare(formattedDate);
            } else {
                // Creăm un obiect nou pentru adăugare
                paltonToEdit = new Palton(culoare, impermeabil, marime, "300", material, selectedDate);
            }

            // Salvăm în baza de date
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            PaltonDao dao = db.paltonDao();

            AppDatabase.databaseWriteExecutor.execute(() -> {
                if (getIntent().hasExtra("palton")) {
                    dao.update(paltonToEdit);
                } else {
                    dao.insert(paltonToEdit);
                }
                runOnUiThread(() -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("palton", paltonToEdit);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });
            });
        });
    }
}