package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {
    private static final int REQUEST_CODE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Button btnOpenThird = findViewById(R.id.button2);
        btnOpenThird.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
            String message = getString(R.string.salut);
            Bundle bundle = new Bundle();
            bundle.putString("message", message);
            bundle.putInt("num1", 10);
            bundle.putInt("num2", 20);

            intent.putExtras(bundle);

            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            int sum = data.getIntExtra("sum", 0);

            String toastMessage = "Suma: " + sum;
            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
        }
    }
}
