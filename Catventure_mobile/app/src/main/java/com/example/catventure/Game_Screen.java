package com.example.catventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.catventure.levels.Level1Activity;
import com.example.catventure.levels.Level2Activity;
import com.example.catventure.levels.Level3Activity;

public class Game_Screen extends AppCompatActivity {

    private int avatarIndex;
    private String playerName;
    private int[] avatarImages = {
            R.drawable.black_cat,  // Index 0
            R.drawable.grey_cat,   // Index 1
            R.drawable.orange_cat, // Index 2
            R.drawable.white_cat   // Index 3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Music.getInstance().playBackgroundMusic(this);
        //usuniecie widocznosci paskow systemowych
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        setContentView(R.layout.activity_game_screen);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Pobierz numer wybranego slota z Intent
        int selectedSlot = getIntent().getIntExtra("selected_slot", -1);
        // Odczytujemy ostatni ukończony poziom z SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("CharacterPrefs", MODE_PRIVATE);
        int currentLevel = sharedPreferences.getInt("last_completed_level_slot_" + selectedSlot, 1); // Jeśli brak poziomu, ustawiamy domyślnie 1

        avatarIndex = -1; // Brak domyślnego avatara
        playerName = null; // Brak domyślnego imienia

        if (selectedSlot >= 0) {
            // Pobierz SharedPreferences
            sharedPreferences = getSharedPreferences("CharacterPrefs", MODE_PRIVATE);

            // Pobierz dane awatara na podstawie wybranego slota
            String avatarKey = "avatar_slot_" + selectedSlot;
            String nameKey = "name_slot_" + selectedSlot;

            // Pobierz dane z SharedPreferences
            if (sharedPreferences.contains(avatarKey) && sharedPreferences.contains(nameKey)) {
                avatarIndex = sharedPreferences.getInt(avatarKey, -1); // Pobierz avatarIndex
                playerName = sharedPreferences.getString(nameKey, null); // Pobierz playerName
            }

            // Znajdź ImageView i ustaw obrazek wybranego kotka
            ImageView avatarView = findViewById(R.id.selectedAvatar);
            avatarView.setImageResource(avatarImages[avatarIndex]);

            // Znajdź TextView i ustaw imię gracza
            TextView playerNameText = findViewById(R.id.savedName);
            playerNameText.setText(playerName);

            // Znajdź TextView do wyświetlania poziomu i ustaw jego tekst
            TextView levelText = findViewById(R.id.level_text);
            levelText.setText("Level " + currentLevel); // Dynamicznie ustawienie tekstu poziomu

            // Obsługa kliknięcia przycisku powrotu
            ImageButton returnButton = findViewById(R.id.BacktoMenu);
            returnButton.setOnClickListener(v -> {
                Intent intent = new Intent(Game_Screen.this, MainActivity.class);
                startActivity(intent);
                finish();
            });

            // Obsługa przycisku "Play"
            //zle pobiera selected slot - naprawic
            ImageButton startLevelButton = findViewById(R.id.startLevelButton);
            startLevelButton.setOnClickListener(v -> {
                Intent intent;
                switch (currentLevel) {
                    case 1:
                        intent = new Intent(Game_Screen.this, Level1Activity.class);
                        break;
                    case 2:
                        intent = new Intent(Game_Screen.this, Level2Activity.class);
                        break;
                    case 3:
                        intent = new Intent(Game_Screen.this, Level3Activity.class);
                        break;
                    default:
                        intent = new Intent(Game_Screen.this, Level1Activity.class); // Domyślnie uruchamiamy poziom 1, jeśli stan jest nieoczekiwany
                        break;
                }
                intent.putExtra("selected_slot", selectedSlot);
                intent.putExtra("avatarIndex", avatarIndex);
                intent.putExtra("Level", currentLevel);

                startActivity(intent);
                finish();
            });


        }
        Log.d("Game_Screen", "Odczytano: Slot = " + selectedSlot + ", Level = " + currentLevel);


    }
}