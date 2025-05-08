package com.example.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://192.168.216.15:6000/"; // Change to your Flask server IP
    private static Retrofit retrofit = null;

    // Now `getClient()` will automatically handle `create()` too
    public static APIInterface getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(APIInterface.class);  // Automatically creates the APIInterface
    }
}
