package com.example.catventure;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoadGame extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ImageView avatar1,avatar2,avatar3;
    private TextView characterName1,characterName2,characterName3;
    private ImageButton delete1,delete2,delete3,Save1,Save2,Save3;
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
        setContentView(R.layout.activity_load_game);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Przyciski powrotu i wyświetlania zapisów
        ImageButton returnButton = findViewById(R.id.Return_button_load);
        avatar1 = findViewById(R.id.Save1);
        avatar2 = findViewById(R.id.Save2);
        avatar3 = findViewById(R.id.Save3);
        characterName1 = findViewById(R.id.SavedName1);
        characterName2 = findViewById(R.id.SavedName2);
        characterName3 = findViewById(R.id.SavedName3);
        delete1 = findViewById(R.id.Delete1);
        delete2 = findViewById(R.id.Delete2);
        delete3 = findViewById(R.id.Delete3);
        Save1 = findViewById(R.id.Empty_save_1);
        Save2 = findViewById(R.id.Empty_save_2);
        Save3 = findViewById(R.id.Empty_save_3);


        // Obsługa kliknięcia przycisku powrotu
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoadGame.this, MainActivity.class);
            startActivity(intent);
        });

        // Inicjalizacja SharedPreferences
        sharedPreferences = getSharedPreferences("CharacterPrefs", MODE_PRIVATE);

        // Wczytaj zapisane dane dla każdego slota
        loadSave(1, characterName1, avatar1);
        loadSave(2, characterName2, avatar2);
        loadSave(3, characterName3, avatar3);

        delete1.setOnClickListener(v -> {
            if(deletebtncontol(characterName1))
                {showDeleteConfirmationDialog(1,characterName1,avatar1);}
            else
                {delete1.setEnabled(false);}
        });
        delete2.setOnClickListener(v -> {
            if(deletebtncontol(characterName2))
                {showDeleteConfirmationDialog(2,characterName2,avatar2);}
            else
                {delete2.setEnabled(false);}
        });
        delete3.setOnClickListener(v -> {
            if(deletebtncontol(characterName3))
                {showDeleteConfirmationDialog(3,characterName3,avatar3);}
            else
                {delete3.setEnabled(false);}
        });

        // Obsługa przycisków ładowania gry
        Save1.setOnClickListener(v -> {
            if (isSlotFilled(1)) {
                Intent intent = new Intent(LoadGame.this, Game_Screen.class);
                intent.putExtra("selected_slot", 1);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Slot 1 is empty. Please create a player first.", Toast.LENGTH_SHORT).show();
            }
        });

        avatar1.setOnClickListener(v -> {
            if (isSlotFilled(1)) {
                Intent intent = new Intent(LoadGame.this, Game_Screen.class);
                intent.putExtra("selected_slot", 1);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Slot 1 is empty. Please create a player first.", Toast.LENGTH_SHORT).show();
            }
        });

        Save2.setOnClickListener(v -> {
            if (isSlotFilled(2)) {
                Intent intent = new Intent(LoadGame.this, Game_Screen.class);
                intent.putExtra("selected_slot", 2);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Slot 2 is empty. Please create a player first.", Toast.LENGTH_SHORT).show();
            }
        });

        avatar2.setOnClickListener(v -> {
            if (isSlotFilled(2)) {
                Intent intent = new Intent(LoadGame.this, Game_Screen.class);
                intent.putExtra("selected_slot", 2);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Slot 2 is empty. Please create a player first.", Toast.LENGTH_SHORT).show();
            }
        });

        Save3.setOnClickListener(v -> {
            if (isSlotFilled(3)) {
                Intent intent = new Intent(LoadGame.this, Game_Screen.class);
                intent.putExtra("selected_slot", 3);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Slot 3 is empty. Please create a player first.", Toast.LENGTH_SHORT).show();
            }
        });

        avatar3.setOnClickListener(v -> {
            if (isSlotFilled(3)) {
                Intent intent = new Intent(LoadGame.this, Game_Screen.class);
                intent.putExtra("selected_slot", 3);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Slot 3 is empty. Please create a player first.", Toast.LENGTH_SHORT).show();
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

        // Wczytanie daych z SharedPreferences
        String name = sharedPreferences.getString(nameKey, "Empty Save");
        int savedAvatarIndex = sharedPreferences.getInt(avatarKey, 0); // Domyślnie pierwszy avatar

        // Ustawienie danych w widokach
        characterName.setText(name);
        avatarView.setImageResource(avatarImages[savedAvatarIndex]);
    }
    private boolean deletebtncontol(TextView characterName){
        if ("Empty save".equals(characterName.getText().toString()))
            {return false;}
        else
            {return true;}
    }
    //usuwanie save'a
    private void clearSlot(int slot) {
        // Klucze dla danego slota
        String nameKey = "name_slot_" + slot;
        String avatarKey = "avatar_slot_" + slot;
        String levelKey = "last_completed_level_slot_" + slot;
        String scoreKey = "score_slot_" + slot;
        String timeKey = "time_slot_" + slot;

        // Edytor SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Usunięcie danych z danego slota
        editor.remove(nameKey);
        editor.remove(avatarKey);
        editor.remove(levelKey);
        editor.remove(scoreKey);
        editor.remove(timeKey);

        // Zapisz zmiany
        editor.apply();
    }
    //pop up potwierdzający usuwanie save'a
    private void showDeleteConfirmationDialog(int slot, TextView characterName, ImageView avatar) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this save?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearSlot(slot);
                    loadSave(slot, characterName, avatar);
                    Toast.makeText(this, "Save deleted!", Toast.LENGTH_SHORT).show();})
                .setNegativeButton("No", (dialog, which) -> {dialog.dismiss();}).show();//Zamkięcie pop up
    }
    // Muzyka w tle
    @Override
    protected void onPause() {
        super.onPause();
        Music.getInstance().pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.getInstance().playBackgroundMusic(this);
    }
}

