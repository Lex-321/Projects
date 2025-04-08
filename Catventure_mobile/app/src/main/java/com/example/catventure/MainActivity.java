package com.example.catventure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Music music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.setProperty("javax.net.debug", "ssl,handshake,trustmanager,failure");
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Start game
        // Znajdź przycisk i ustaw kliknięcie
        ImageButton startGameButton = findViewById(R.id.Start_game);
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uruchomienie aktywności StartGame
                Intent intent = new Intent(MainActivity.this, StartGame.class);
                startActivity(intent);
            }
        });
        // Load game
        // Znajdź przycisk i ustaw kliknięcie
        ImageButton loadGameButton = findViewById(R.id.Load_game);
        loadGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uruchomienie aktywności LoadGame
                Intent intent = new Intent(MainActivity.this, LoadGame.class);
                startActivity(intent);
            }
        });
        // Credits
        // Znajdź przycisk i ustaw kliknięcie
        ImageButton CreditsButton = findViewById(R.id.Credits);
        CreditsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uruchomienie aktywności Credits
                Intent intent = new Intent(MainActivity.this, Credits.class);
                startActivity(intent);
            }
        });
    }

    //muzyka w tle
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}