package com.example.meteo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private Spinner daysSpinner;
    private Button searchButton;
    private TextView resultTextView;
    private static final String API_KEY = "wOx56suMErSVDC3hExumdyLc9I3GK24H"; // Your AccuWeather API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        cityEditText = findViewById(R.id.cityEditText);
        daysSpinner = findViewById(R.id.daysSpinner);
        searchButton = findViewById(R.id.searchButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.forecast_days,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(adapter);

        // Set button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString().trim();
                String selectedDays = daysSpinner.getSelectedItem().toString();
                int days = 1; // Default
                if (selectedDays.equals("5 days")) days = 5;
                else if (selectedDays.equals("10 days")) days = 10;

                if (!city.isEmpty()) {
                    new CitySearchTask(days).execute(city);
                } else {
                    resultTextView.setText("Please enter a city name");
                }
            }
        });
    }

    // AsyncTask to perform city search and fetch forecast
    private class CitySearchTask extends AsyncTask<String, Void, String> {
        private int forecastDays;

        public CitySearchTask(int days) {
            this.forecastDays = days;
        }

        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String cityKeyUrl = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" + API_KEY + "&q=" + city;

            OkHttpClient client = new OkHttpClient();

            // Step 1: Get city key
            try {
                Log.d("WeatherApp", "Fetching city key for: " + city);
                Log.d("WeatherApp", "City Search URL: " + cityKeyUrl);
                Request cityRequest = new Request.Builder().url(cityKeyUrl).build();
                Response cityResponse = client.newCall(cityRequest).execute();
                if (!cityResponse.isSuccessful()) {
                    Log.e("WeatherApp", "City API error: " + cityResponse.code() + " " + cityResponse.message());
                    String errorBody = cityResponse.body() != null ? cityResponse.body().string() : "No response body";
                    Log.e("WeatherApp", "Error response: " + errorBody);
                    return "Error fetching city: " + cityResponse.message();
                }

                String cityJson = cityResponse.body().string();
                Log.d("WeatherApp", "City JSON: " + cityJson);
                JSONArray cityArray = new JSONArray(cityJson);
                if (cityArray.length() == 0) {
                    return "City not found";
                }

                JSONObject cityObject = cityArray.getJSONObject(0);
                String cityKey = cityObject.getString("Key");
                Log.d("WeatherApp", "City Key: " + cityKey);

                // Step 2: Get forecast
                String forecastUrl = String.format(
                        "http://dataservice.accuweather.com/forecasts/v1/daily/%dday/%s?apikey=%s&metric=true",
                        forecastDays, cityKey, API_KEY
                );
                Log.d("WeatherApp", "Fetching forecast from: " + forecastUrl);
                Request forecastRequest = new Request.Builder().url(forecastUrl).build();
                Response forecastResponse = client.newCall(forecastRequest).execute();
                if (!forecastResponse.isSuccessful()) {
                    Log.e("WeatherApp", "Forecast API error: " + forecastResponse.code() + " " + forecastResponse.message());
                    String forecastErrorBody = forecastResponse.body() != null ? forecastResponse.body().string() : "No response body";
                    Log.e("WeatherApp", "Forecast error response: " + forecastErrorBody);
                    return "Error fetching forecast: " + forecastResponse.message();
                }

                String forecastJson = forecastResponse.body().string();
                Log.d("WeatherApp", "Forecast JSON: " + forecastJson);
                JSONObject forecastObject = new JSONObject(forecastJson);
                JSONArray dailyForecasts = forecastObject.getJSONArray("DailyForecasts");

                StringBuilder result = new StringBuilder();
                for (int i = 0; i < dailyForecasts.length(); i++) {
                    JSONObject forecast = dailyForecasts.getJSONObject(i);
                    String date = forecast.getString("Date").substring(0, 10); // Get date part
                    JSONObject temperature = forecast.getJSONObject("Temperature");
                    double minTemp = temperature.getJSONObject("Minimum").getDouble("Value");
                    double maxTemp = temperature.getJSONObject("Maximum").getDouble("Value");

                    result.append(String.format("Date: %s\nMin Temp: %.1f°C\nMax Temp: %.1f°C\n\n", date, minTemp, maxTemp));
                }

                return result.toString();

            } catch (Exception e) {
                Log.e("WeatherApp", "Exception: " + e.getMessage(), e);
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            resultTextView.setText(result);
        }
    }
}