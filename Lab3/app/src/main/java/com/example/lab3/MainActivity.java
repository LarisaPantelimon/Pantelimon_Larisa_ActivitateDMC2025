package com.example.lab3;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLifecycle";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate: ERROR - Activitatea a fost creată.");
        Log.w(TAG, "onCreate: WARNING - Inițializarea resurselor.");
        Log.d(TAG, "onCreate: DEBUG - Setarea interfeței UI.");
        Log.i(TAG, "onCreate: INFO - Pregătirea activității.");
        Log.v(TAG, "onCreate: VERBOSE - Detalii complete despre creare.");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ERROR - Posibile probleme la start.");
        Log.w(TAG, "onStart: WARNING - Activitatea devine vizibilă.");
        Log.d(TAG, "onStart: DEBUG - Restaurarea setărilor UI.");
        Log.i(TAG, "onStart: INFO - Pregătire pentru interacțiune.");
        Log.v(TAG, "onStart: VERBOSE - Toate detaliile despre start.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ERROR - Posibile probleme la reactivare.");
        Log.w(TAG, "onResume: WARNING - Activitatea este în prim-plan.");
        Log.d(TAG, "onResume: DEBUG - Reluarea animațiilor.");
        Log.i(TAG, "onResume: INFO - Utilizatorul poate interacționa.");
        Log.v(TAG, "onResume: VERBOSE - Detalii complete despre resume.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ERROR - Posibile probleme la pauză.");
        Log.w(TAG, "onPause: WARNING - Activitatea pierde focusul.");
        Log.d(TAG, "onPause: DEBUG - Salvarea datelor temporare.");
        Log.i(TAG, "onPause: INFO - Oprirea acțiunilor non-critice.");
        Log.v(TAG, "onPause: VERBOSE - Detalii complete despre pauză.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ERROR - Activitatea s-a oprit brusc?");
        Log.w(TAG, "onStop: WARNING - Nu mai este vizibilă.");
        Log.d(TAG, "onStop: DEBUG - Salvarea setărilor utilizatorului.");
        Log.i(TAG, "onStop: INFO - Închidere conexiuni neesențiale.");
        Log.v(TAG, "onStop: VERBOSE - Toate detaliile despre stop.");
    }
}