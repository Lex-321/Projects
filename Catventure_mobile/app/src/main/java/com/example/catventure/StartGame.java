package com.example.catventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StartGame extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ImageView avatar1, avatar2, avatar3;
    private TextView characterName1, characterName2, characterName3;
    private ImageButton emptySave1, emptySave2, emptySave3;
    private int[] avatarImages = {
            R.drawable.black_cat,
            R.drawable.grey_cat,
            R.drawable.orange_cat,
            R.drawable.white_cat
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //usuniecie widocznosci paskow systemowych
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        setContentView(R.layout.activity_start_game);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Inicjalizacja SharedPreferences
        sharedPreferences = getSharedPreferences("CharacterPrefs", MODE_PRIVATE);

        // Inicjalizacja widoków
        avatar1 = findViewById(R.id.Save1);
        avatar2 = findViewById(R.id.Save2);
        avatar3 = findViewById(R.id.Save3);
        characterName1 = findViewById(R.id.SavedName1);
        characterName2 = findViewById(R.id.SavedName2);
        characterName3 = findViewById(R.id.SavedName3);
        emptySave1 = findViewById(R.id.Empty_save_1);
        emptySave2 = findViewById(R.id.Empty_save_2);
        emptySave3 = findViewById(R.id.Empty_save_3);

        // Przycisk powrotu
        ImageButton returnButton = findViewById(R.id.Return_button_Start);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(StartGame.this, MainActivity.class);
            startActivity(intent);
        });

        // Obsługa przycisków i avatarów
        setupSlotListeners(1, emptySave1, avatar1);
        setupSlotListeners(2, emptySave2, avatar2);
        setupSlotListeners(3, emptySave3, avatar3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.getInstance().playBackgroundMusic(this);
        loadSave(1, characterName1, avatar1);
        loadSave(2, characterName2, avatar2);
        loadSave(3, characterName3, avatar3);
    }

    private void setupSlotListeners(int slot, ImageButton emptySave, ImageView avatar) {
        emptySave.setOnClickListener(v -> {
            Intent intent = new Intent(StartGame.this, CreateSave.class);
            intent.putExtra("saveSlot", slot); // Wskaż slot zapisu
            startActivity(intent);
        });

        avatar.setOnClickListener(v -> {
            if (isSlotFilled(slot)) {
            } else {
                // Jeśli slot jest pusty, przekieruj do tworzenia zapisu
                Intent intent = new Intent(StartGame.this, CreateSave.class);
                intent.putExtra("saveSlot", slot);
                startActivity(intent);
            }
        });
    }

    private boolean isSlotFilled(int slot) {
        String nameKey = "name_slot_" + slot;
        String avatarKey = "avatar_slot_" + slot;

        return sharedPreferences.contains(nameKey) && sharedPreferences.contains(avatarKey);
    }

    private void loadSave(int slot, TextView characterName, ImageView avatarView) {
        // Klucze dla danego slota
        String nameKey = "name_slot_" + slot;
        String avatarKey = "avatar_slot_" + slot;

        // Wczytanie danych z SharedPreferences
        String name = sharedPreferences.getString(nameKey, "Empty Save");
        int savedAvatarIndex = sharedPreferences.getInt(avatarKey, -1); // -1 oznacza pusty slot

        // Jeśli slot jest pusty, wyświetl domyślne dane
        if (savedAvatarIndex == -1) {
            characterName.setText("Empty Save");
        } else {
            // Slot wypełniony - wyświetl zapisane dane
            characterName.setText(name);
            avatarView.setImageResource(avatarImages[savedAvatarIndex]);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.getInstance().pauseMusic();
    }
}
