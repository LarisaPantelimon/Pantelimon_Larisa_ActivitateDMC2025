package com.example.lab12;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<Integer> values = (ArrayList<Integer>) getIntent().getSerializableExtra("values");
        String type = getIntent().getStringExtra("type");

        ChartView chartView = new ChartView(this, values, type);
        setContentView(chartView);
    }
}
