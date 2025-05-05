package com.example.meteo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Button searchButton;
    private TextView resultTextView;
    private static final String API_KEY = "wOx56suMErSVDC3hExumdyLc9I3GK24H"; // Replace with your AccuWeather API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        cityEditText = findViewById(R.id.cityEditText);
        searchButton = findViewById(R.id.searchButton);
        resultTextView = findViewById(R.id.resultTextView);

        // Set button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString().trim();
                if (!city.isEmpty()) {
                    new CitySearchTask().execute(city);
                } else {
                    resultTextView.setText("Please enter a city name");
                }
            }
        });
    }

    // AsyncTask to perform city search
    private class CitySearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String url = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" + API_KEY + "&q=" + city;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonData);
                    if (jsonArray.length() > 0) {
                        JSONObject cityObject = jsonArray.getJSONObject(0);
                        return cityObject.getString("Key"); // Return city key
                    } else {
                        return "City not found";
                    }
                } else {
                    return "Error: " + response.message();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            resultTextView.setText("City Key: " + result);
        }
    }
}