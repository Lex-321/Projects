package com.example.catventure.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://worldtimeapi.org/api/"; // Wymuszone HTTPS

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient client = OkHttpHelper.createHttpsClient();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}


