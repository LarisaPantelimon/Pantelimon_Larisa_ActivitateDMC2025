package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public void buttonClick(View v) {
        EditText tv1 = (EditText) findViewById(R.id.editTextText2);
        EditText tv2 = (EditText) findViewById(R.id.editTextText3);
        TextView tv = (TextView) findViewById(R.id.textView2);

        try {
            String sTextFromET = tv1.getText().toString();
            int nIntFromET = Integer.parseInt(sTextFromET);

            String sTextFromET2 = tv2.getText().toString();
            int nIntFromET2 = Integer.parseInt(sTextFromET2);

            int suma = nIntFromET + nIntFromET2;

            tv.setText(String.valueOf(suma));
        } catch (NumberFormatException e) {
            tv.setText();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}