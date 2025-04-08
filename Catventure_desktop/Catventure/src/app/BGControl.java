package app;

import app.api.RetrofitClient;
import app.api.TimeResponse;
import app.api.WorldTimeApiService;
import javafx.scene.image.Image;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Klasa aktualizujaca tlo poziomu w zaleznosci od czasu
 */
public class BGControl {

    private static final String TAG = "WorldTimeAPI";

    /**
     * Pobieranie aktualnego czasu z API
     * @param bgUpdate interfejs BGUptade
     * @param levelNumber numer poziomu
     */
    public static void fetchAndUpdateBackground(BGUpdate bgUpdate, int levelNumber) {
        WorldTimeApiService apiService = RetrofitClient.getRetrofitInstance().create(WorldTimeApiService.class);
        Call<TimeResponse> call = apiService.getTime("Europe", "Warsaw");
        call.enqueue(new Callback<TimeResponse>() {
            @Override
            public void onResponse(Call<TimeResponse> call, Response<TimeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String datetime = response.body().getDatetime();
                    System.out.println(TAG + ": Current Time: " + datetime);
                    updateBackgroundBasedOnTime(bgUpdate, datetime, levelNumber);
                } else {
                    System.err.println(TAG + ": Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<TimeResponse> call, Throwable t) {
                System.err.println(TAG + ": Error fetching time: " + t.getMessage());
            }
        });
    }

    /**
     * Aktualizacja tla poziomu
     * @param bgUpdate interfejs BGUpdate
     * @param datetime aktualny czas
     * @param levelNumber numer poziomu
     */
    private static void updateBackgroundBasedOnTime(BGUpdate bgUpdate, String datetime, int levelNumber) {
        // Wyciągamy godzinę z odpowiedzi datetime
        String hourString = datetime.split("T")[1].split(":")[0];
        int hour = Integer.parseInt(hourString);
        String backgroundUrl;

        try {
            if (hour >= 6 && hour < 18) {
                // Dzień
                backgroundUrl = getBackgroundForDay(levelNumber);
            } else {
                // Noc
                backgroundUrl = getBackgroundForNight(levelNumber);
            }
            // Załaduj obraz jako tło
            Image backgroundImage = new Image(backgroundUrl);
            bgUpdate.setBackgroundImage(backgroundImage);
            System.out.println("Background updated successfully for level: " + levelNumber);
        } catch (Exception e) {
            System.err.println("Error updating background: " + e.getMessage());
        }
    }

    private static String getBackgroundForDay(int levelNumber) {
        switch (levelNumber) {
            case 1: return "file:background_day_level1.png";
            case 2: return "file:background_day_level2.png";
            default: return "file:background_day_default.png";
        }
    }

    private static String getBackgroundForNight(int levelNumber) {
        switch (levelNumber) {
            case 1: return "file:background_night_level1.png";
            case 2: return "file:background_night_level2.png";
            default: return "file:background_night_default.png";
        }
    }
}

