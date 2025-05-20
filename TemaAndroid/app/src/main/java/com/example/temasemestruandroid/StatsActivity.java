package com.example.temasemestruandroid;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.temasemestruandroid.entities.DBHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {
    private PieChart pieChart;
    private RatingChartView ratingChartView;
    private DBHelper dbHelper;
    private FirebaseFirestore firestore;
    private String username;
    private ArrayList<PieEntry> entries;
    private int importantCount = 0;
    private int normalCount = 0;
    private int spamCount = 0;
    private ArrayList<Integer> ratingCounts;
    private ArrayList<String> ratingLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        pieChart = findViewById(R.id.pieChart);
        ratingChartView = findViewById(R.id.ratingChartView);
        dbHelper = new DBHelper(this);
        firestore = FirebaseFirestore.getInstance();
        entries = new ArrayList<>();
        ratingCounts = new ArrayList<>();
        ratingLabels = new ArrayList<>();
        MaterialButton btnBack = findViewById(R.id.btnBack);

        // Retrieve username from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("EmailAppPrefs", MODE_PRIVATE);
        username = prefs.getString("loggedInUser", null);

        if (username != null) {
            // Load email statistics
            loadEmailStats();
        } else {
            Toast.makeText(this, "Niciun utilizator autentificat!", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadEmailStats() {
        // Reset counts
        importantCount = 0;
        normalCount = 0;
        spamCount = 0;

        // Initialize rating counts for exact ratings: 0, 1, 2, 3, 4, 5
        int[] ratings = {0, 1, 2, 3, 4, 5};
        for (int rating : ratings) {
            ratingLabels.add(String.valueOf(rating));
            ratingCounts.add(0);
        }

        // Load sent emails from SQLite
        Cursor cursor = dbHelper.getEmailsBySender(username);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                boolean important = cursor.getInt(cursor.getColumnIndexOrThrow("important")) == 1;
                float rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
                // Update pie chart counts
                if (important) {
                    importantCount++;
                } else if (rating < 2.0) {
                    spamCount++;
                } else {
                    normalCount++;
                }
                // Update rating counts
                int intRating = Math.round(rating);
                if (intRating >= 0 && intRating <= 5) {
                    ratingCounts.set(intRating, ratingCounts.get(intRating) + 1);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Load received emails from Firebase
        firestore.collection("emails")
                .whereEqualTo("to", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        boolean important = Boolean.TRUE.equals(doc.getBoolean("important"));
                        double rating = doc.getDouble("rating") != null ? doc.getDouble("rating") : 0;
                        // Update pie chart counts
                        if (important) {
                            importantCount++;
                        } else if (rating < 2.0) {
                            spamCount++;
                        } else {
                            normalCount++;
                        }
                        // Update rating counts
                        int intRating = (int) Math.round(rating);
                        if (intRating >= 0 && intRating <= 5) {
                            ratingCounts.set(intRating, ratingCounts.get(intRating) + 1);
                        }
                    }

                    // Filter out zero-count ratings
                    ArrayList<Integer> filteredCounts = new ArrayList<>();
                    ArrayList<String> filteredLabels = new ArrayList<>();
                    for (int i = 0; i < ratingCounts.size(); i++) {
                        if (ratingCounts.get(i) > 0) {
                            filteredCounts.add(ratingCounts.get(i));
                            filteredLabels.add(ratingLabels.get(i));
                        }
                    }

                    // Update both charts
                    updateChartData();
                    ratingChartView.setRatingData(filteredCounts, filteredLabels);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Eroare la Firebase", Toast.LENGTH_SHORT).show());
    }

    private void updateChartData() {
        entries.clear();
        if (importantCount > 0) entries.add(new PieEntry(importantCount, "Importante"));
        if (normalCount > 0) entries.add(new PieEntry(normalCount, "Normale"));
        if (spamCount > 0) entries.add(new PieEntry(spamCount, "Spam"));

        setupPieChart();
        updatePieChart();
    }

    private void setupPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.graphics.Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleRadius(58f);
        pieChart.setCenterText("Email Statistics");
        pieChart.setCenterTextSize(18f);
        pieChart.setEntryLabelColor(android.graphics.Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.animateY(1000);
    }

    private void updatePieChart() {
        PieDataSet dataSet = new PieDataSet(entries, "Tipuri de emailuri");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(android.graphics.Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.invalidate();
    }
}