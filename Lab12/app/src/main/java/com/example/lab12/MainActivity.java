package com.example.lab12;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText editTextNumber;
    private Button buttonAdd, buttonShow, buttonDelete;
    private Spinner spinner;
    private ArrayList<Integer> values = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private TextView valuesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNumber = findViewById(R.id.editTextNumber);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonShow = findViewById(R.id.buttonShow);
        buttonDelete = findViewById(R.id.buttonDelete);
        spinner = findViewById(R.id.spinner);
        valuesText = findViewById(R.id.valuesText);

        String[] options = {"PieChart", "ColumnChart", "BarChart"};
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        buttonAdd.setOnClickListener(v -> {
            String numStr = editTextNumber.getText().toString().trim();
            if (!numStr.isEmpty()) {
                try {
                    int num = Integer.parseInt(numStr);
                    values.add(num);
                    valuesText.setText("Values: " + values);
                    editTextNumber.setText("");
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid number!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDelete.setOnClickListener(v -> {
            values.clear();
            valuesText.setText("Values: ");
            Toast.makeText(this, "Values cleared!", Toast.LENGTH_SHORT).show();
        });

        buttonShow.setOnClickListener(v -> {
            if (values.isEmpty()) {
                Toast.makeText(this, "Add some values first!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(MainActivity.this, ChartActivity.class);
            intent.putExtra("values", values);
            intent.putExtra("type", spinner.getSelectedItem().toString());
            startActivity(intent);
        });
    }
}