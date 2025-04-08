package com.example.catventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.example.catventure.api.RetrofitClient;
import com.example.catventure.api.TimeResponse;
import com.example.catventure.api.WorldTimeApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BGControl {
    private static final String TAG = "WorldTimeAPI";

    public static void fetchAndUpdateBackground(Context context, View layout, int levelNumber) {
        WorldTimeApiService apiService = RetrofitClient.getRetrofitInstance().create(WorldTimeApiService.class);
        Call<TimeResponse> call = apiService.getTime("Europe", "Warsaw");

        call.enqueue(new Callback<TimeResponse>() {
            @Override
            public void onResponse(Call<TimeResponse> call, Response<TimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String datetime = response.body().getDatetime();
                    Log.d(TAG, "Current Time: " + datetime);
                    updateBackgroundBasedOnTime(context, layout, datetime, levelNumber);
                } else {
                    Log.e(TAG, "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<TimeResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching time: ", t);
            }
        });
    }

    private static void updateBackgroundBasedOnTime(Context context, View layout, String datetime, int levelNumber) {
        String hourString = datetime.split("T")[1].split(":")[0];
        int hour = Integer.parseInt(hourString);
        int backgroundResource;
        try {
            if (hour >= 6 && hour < 15) {
                // Dzień
                if (levelNumber == 1) {
                    backgroundResource = R.drawable.background_day;
                } else if (levelNumber == 2) {
                    backgroundResource = R.drawable.background_day;
                } else {
                    backgroundResource = R.drawable.background_day;
                }
            } else {
                // Noc
                if (levelNumber == 1) {
                    backgroundResource = R.drawable.background_night;
                } else if (levelNumber == 2) {
                    backgroundResource = R.drawable.background_night;
                } else {
                    backgroundResource = R.drawable.background_night;
                }
            }

            // Ustawienie bitmapy dla poziomu
            if (layout instanceof BGUpdate) {
                Bitmap background = BitmapFactory.decodeResource(context.getResources(), backgroundResource);
                ((BGUpdate) layout).setBackgroundBitmap(background);
            } else {
                Log.e("BGControl", "Layout does not implement BGUpdate");
            }
            Log.d("BGControl", "Background updated successfully for level: " + levelNumber);
        } catch (Exception e) {
            Log.e("BGControl", "Error updating background: " + e.getMessage());
        }
    }
}
