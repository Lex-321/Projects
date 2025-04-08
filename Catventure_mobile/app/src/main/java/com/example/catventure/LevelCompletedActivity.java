package com.example.catventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.catventure.levels.Level1Activity;
import com.example.catventure.levels.Level2Activity;
import com.example.catventure.levels.Level3Activity;

public class LevelCompletedActivity extends AppCompatActivity {
    private int avatarIndex;
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ukrycie pasków systemowych
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        setContentView(R.layout.activity_level_completed);
        EdgeToEdge.enable(this);

        // Pobieranie danych z Intentu
        int score = getIntent().getIntExtra("score", 0);
        int time = getIntent().getIntExtra("time", 0);
        int selectedSlot = getIntent().getIntExtra("selected_slot", -1);

        if (selectedSlot < 0) {
            Log.e("LevelCompletedActivity", "Invalid slot selected");
            finish();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("CharacterPrefs", MODE_PRIVATE);

        // Odczytanie aktualnego poziomu z SharedPreferences
        int currentLevel = sharedPreferences.getInt("last_completed_level_slot_" + selectedSlot, 1); // Domyślnie poziom 1

        // Zwiększenie poziomu o 1 i zapisanie
        int nextLevel = currentLevel + 1;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("last_completed_level_slot_" + selectedSlot, nextLevel);
        editor.apply();

//        Log.d("LevelCompletedActivity", "Zaktualizowano poziom: Slot = " + selectedSlot + ", Current Level = " + currentLevel + ", Next Level = " + nextLevel);

        // Odczytanie danych awatara i gracza
        avatarIndex = sharedPreferences.getInt("avatar_slot_" + selectedSlot, -1);
        playerName = sharedPreferences.getString("name_slot_" + selectedSlot, "Unknown");

        // Dostosowanie paddingu do systemowych pasków
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Wyświetlanie wyników
        TextView scoreView = findViewById(R.id.scoreTextView);
        TextView timeView = findViewById(R.id.timeTextView);

        scoreView.setText("Your Score: " + score);
        timeView.setText("Game Time: " + time);

        // Kolejny poziom
        findViewById(R.id.nextLevelButton).setOnClickListener(v -> {
            Music.getInstance().pauseMusic();
            Intent intent;
            switch (nextLevel) {
                case 1:
                    intent = new Intent(LevelCompletedActivity.this, Level1Activity.class);
                    break;
                case 2:
                    intent = new Intent(LevelCompletedActivity.this, Level2Activity.class);
                    break;
                case 3:
                    intent = new Intent(LevelCompletedActivity.this, Level3Activity.class);
                    break;
                default:
                    intent = new Intent(LevelCompletedActivity.this, Level1Activity.class); // Powrót do Level1 w przypadku błędu
                    break;
            }

            // Przekazanie nowego poziomu i wybranego slota
            intent.putExtra("selected_slot", selectedSlot);
            intent.putExtra("level", nextLevel);
            intent.putExtra("avatarIndex", avatarIndex);

            startActivity(intent);
            finish();
        });

        // Powrót do menu głównego
        findViewById(R.id.mainMenuButton).setOnClickListener(v -> {
            Music.getInstance().pauseMusic();
            Intent intent = new Intent(LevelCompletedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
