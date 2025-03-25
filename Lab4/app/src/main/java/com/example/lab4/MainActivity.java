package com.example.lab4;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_PALTON = 1;
    private static final int REQUEST_CODE_EDIT_PALTON = 2;  // Cod pentru editare
    private ArrayList<Palton> listaPaltoane;
    private CustomAdapter adapter;
    private ListView listViewPaltoane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewPaltoane = findViewById(R.id.listViewPaltoane);
        listaPaltoane = new ArrayList<>();
        adapter = new CustomAdapter(this, listaPaltoane);
        listViewPaltoane.setAdapter(adapter);

        // Adăugarea unui nou obiect Palton
        findViewById(R.id.button).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_PALTON);
        });

        // Click pe elementul din listă pentru a edita
        listViewPaltoane.setOnItemClickListener((parent, view, position, id) -> {
            Palton palton = listaPaltoane.get(position);

            // Se trimite obiectul și poziția la activitatea de editare
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("palton", palton);  // Transmiterea obiectului pentru editare
            intent.putExtra("position", position);  // Transmiterea poziției
            startActivityForResult(intent, REQUEST_CODE_EDIT_PALTON);  // Cod de editare
        });

        // Ștergerea unui element
        listViewPaltoane.setOnItemLongClickListener((parent, view, position, id) -> {
            Palton paltonDeSters = listaPaltoane.get(position);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirmare")
                    .setMessage("Sigur vrei să ștergi paltonul " + paltonDeSters.getCuloare() + "?")
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
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

        if (resultCode == RESULT_OK && data != null) {
            Palton palton = data.getParcelableExtra("palton");

            if (requestCode == REQUEST_CODE_ADD_PALTON) {
                // Adăugăm obiectul nou la listă
                listaPaltoane.add(palton);
                Toast.makeText(this, "Palton adăugat!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_CODE_EDIT_PALTON) {
                // Preluăm poziția elementului modificat
                int position = data.getIntExtra("position", -1);  // Preluăm poziția

                if (position != -1) {
                    // Înlocuim obiectul vechi cu cel nou
                    listaPaltoane.set(position, palton);
                    Toast.makeText(this, "Paltonul a fost modificat!", Toast.LENGTH_SHORT).show();
                }
            }

            // Actualizăm adapterul pentru a reflecta modificările
            adapter.notifyDataSetChanged();
        }
    }
}
