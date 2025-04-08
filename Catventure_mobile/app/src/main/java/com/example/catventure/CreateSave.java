package com.example.catventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class CreateSave extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ImageView avatar;
    private EditText editName;
    private ImageButton leftArrow, rightArrow, btnSave, ReturnButton;
    private int[] avatarImages = {
            R.drawable.black_cat,
            R.drawable.grey_cat,
            R.drawable.orange_cat,
            R.drawable.white_cat
    };

    int currentAvatarIndex = 0;
    private int saveSlot; // Numer slota

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_save);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        avatar = findViewById(R.id.avatar);
        editName = findViewById(R.id.editName);
        leftArrow = findViewById(R.id.leftArrow);
        rightArrow = findViewById(R.id.rightArrow);
        btnSave = findViewById(R.id.savebutton);
        ReturnButton = findViewById(R.id.Return_button_create_save);

        sharedPreferences = getSharedPreferences("CharacterPrefs", MODE_PRIVATE);

        // Pobierz numer slota zapisu z Intentu
        saveSlot = getIntent().getIntExtra("saveSlot", 1); // Domyślnie Save 1

        // Obsługa przycisków zmiany avatara
        leftArrow.setOnClickListener(v -> showPreviousAvatar());
        rightArrow.setOnClickListener(v -> showNextAvatar());

        // Obsługa przycisku zapisu
        btnSave.setOnClickListener(v -> {
            if (!editName.getText().toString().isEmpty()) {
                saveCharacter();
                Toast.makeText(this,"Save created",Toast.LENGTH_SHORT).show();}
            else {
                Toast.makeText(this,"Name your cat",Toast.LENGTH_SHORT).show();}
            });

        ReturnButton.setOnClickListener(v -> {
            Intent intent = new Intent(CreateSave.this, StartGame.class);
            startActivity(intent);
        });
    }

    void showPreviousAvatar() {
        currentAvatarIndex = (currentAvatarIndex - 1 + avatarImages.length) % avatarImages.length;
        avatar.setImageResource(avatarImages[currentAvatarIndex]);
    }

    void showNextAvatar() {
        currentAvatarIndex = (currentAvatarIndex + 1) % avatarImages.length;
        avatar.setImageResource(avatarImages[currentAvatarIndex]);
    }


    private void saveCharacter() {
        String name = editName.getText().toString();

        // Zapis danych w SharedPreferences dla wybranego slota
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name_slot_" + saveSlot, name); // Klucz dla imienia
        editor.putInt("avatar_slot_" + saveSlot, currentAvatarIndex); // Klucz dla avatara
        editor.apply();
    }

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


