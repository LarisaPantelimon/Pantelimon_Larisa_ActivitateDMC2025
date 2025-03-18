package com.example.lab4;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab4.Palton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity2 extends AppCompatActivity {

    private TextView textViewSelectedDate;
    private EditText editTextCuloare;

    private TextView editTextPret,editTextMaterial;
    private Switch switchImpermeabil;
    private RadioGroup radioGroupMarime;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button buttonSelectDate = findViewById(R.id.buttonSelectDate);
        Button buttonSave = findViewById(R.id.button2);
        textViewSelectedDate = findViewById(R.id.textView7);
        editTextCuloare = findViewById(R.id.editTextText);
        editTextPret = findViewById(R.id.textView4);
        editTextMaterial = findViewById(R.id.textView5);
        switchImpermeabil = findViewById(R.id.switch1);
        radioGroupMarime = findViewById(R.id.radioGroup);

        // Setăm data implicită
        Calendar calendar = Calendar.getInstance();
        selectedDate = calendar.getTime(); // Data curentă
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        textViewSelectedDate.setText("Data selectată: " + dateFormat.format(selectedDate));

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
                        textViewSelectedDate.setText("Data selectată: " + dateFormat.format(selectedDate));
                    },
                    year, month, day
            );

            // Setăm limita maximă a datei să nu poată selecta o dată din viitor
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        buttonSave.setOnClickListener(v -> {
            String culoare = editTextCuloare.getText().toString();
            String pret = editTextPret.getText().toString();
            String material = editTextMaterial.getText().toString();
            boolean impermeabil = switchImpermeabil.isChecked();

            // Obținem mărimea selectată din RadioGroup
            int selectedId = radioGroupMarime.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(selectedId);
            String marime = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "";

            // Creăm obiectul Palton
            Palton palton = new Palton(culoare, impermeabil, marime, pret, material, selectedDate);

            // Trimitem obiectul înapoi către MainActivity
            Intent intent = new Intent();
            intent.putExtra("palton", palton);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
