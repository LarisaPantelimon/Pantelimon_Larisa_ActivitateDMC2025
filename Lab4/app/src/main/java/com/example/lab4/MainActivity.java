package com.example.lab4;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import com.example.lab4.Palton;
import com.example.lab4.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_PALTON = 1;
    private ArrayList<Palton> listaPaltoane;
    private ArrayAdapter<Palton> adapter;
    private ListView listViewPaltoane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewPaltoane = findViewById(R.id.listViewPaltoane);
        listaPaltoane = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaPaltoane);
        listViewPaltoane.setAdapter(adapter);

        // Deschidem activitatea de adăugare
        findViewById(R.id.button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PALTON);
        });

        // Afișăm un Toast când se selectează un element
        listViewPaltoane.setOnItemClickListener((parent, view, position, id) -> {
            Palton palton = listaPaltoane.get(position);
            Toast.makeText(this, palton.toString(), Toast.LENGTH_SHORT).show();
        });

        listViewPaltoane.setOnItemLongClickListener((parent, view, position, id) -> {
            Palton paltonDeSters = listaPaltoane.get(position);

            // Creăm un dialog de confirmare
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirmare")
                    .setMessage("Sigur vrei să ștergi paltonul " + paltonDeSters.getCuloare() + "?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Ștergem obiectul din listă și notificăm adapterul
                            listaPaltoane.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this, "Paltonul a fost șters!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Nu", null)
                    .show();

            return true;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PALTON && resultCode == RESULT_OK && data != null) {
            // Preluăm obiectul Palton trimis din MainActivity2
            Palton palton = data.getParcelableExtra("palton");
            listaPaltoane.add(palton);
            adapter.notifyDataSetChanged();
        }
    }
}
