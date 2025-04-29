package com.example.lab4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.example.lab4.Entities.AppDatabase;
import com.example.lab4.Entities.PaltonDao;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_PALTON = 1;
    private static final int REQUEST_CODE_EDIT_PALTON = 2;
    private ArrayList<Palton> listaPaltoane;
    private CustomAdapter adapter;
    private ListView listViewPaltoane;
    private AppDatabase db;
    private PaltonDao paltonDao;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executorService = Executors.newSingleThreadExecutor();

        db = AppDatabase.getInstance(getApplicationContext());
        paltonDao = db.paltonDao();

        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        String dimensiune = prefs.getString("text_size", "16");
        String culoare = prefs.getString("text_color", "black");
        float fontSize = Float.parseFloat(dimensiune);

        listViewPaltoane = findViewById(R.id.listViewPaltoane);
        listaPaltoane = new ArrayList<>();
        adapter = new CustomAdapter(this, listaPaltoane, fontSize, culoare);
        listViewPaltoane.setAdapter(adapter);

        // Observăm datele din LiveData
        LiveData<List<Palton>> paltoaneLiveData = paltonDao.getAll();
        paltoaneLiveData.observe(this, new Observer<List<Palton>>() {
            @Override
            public void onChanged(List<Palton> paltoane) {
                listaPaltoane.clear();
                listaPaltoane.addAll(paltoane != null ? paltoane : new ArrayList<>());
                adapter.notifyDataSetChanged();
            }
        });

        Button buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PALTON);
        });

        listViewPaltoane.setOnItemClickListener((parent, view, position, id) -> {
            Palton palton = listaPaltoane.get(position);
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("palton", palton);
            intent.putExtra("position", position);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PALTON);
        });

        // Modificăm apăsarea lungă pentru a șterge obiectul
        listViewPaltoane.setOnItemLongClickListener((parent, view, position, id) -> {
            Palton paltonToDelete = listaPaltoane.get(position);

            // Ștergem obiectul pe un fir de fundal
            executorService.execute(() -> {
                paltonDao.delete(paltonToDelete);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Palton șters!", Toast.LENGTH_SHORT).show();
                });
            });

            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Palton palton = data.getParcelableExtra("palton");

            executorService.execute(() -> {
                if (requestCode == REQUEST_CODE_ADD_PALTON) {
                    paltonDao.insert(palton);
                } else if (requestCode == REQUEST_CODE_EDIT_PALTON) {
                    paltonDao.update(palton);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, requestCode == REQUEST_CODE_ADD_PALTON ? "Palton adăugat!" : "Paltonul a fost modificat!", Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}