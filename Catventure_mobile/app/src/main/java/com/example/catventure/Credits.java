package com.example.catventure;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Credits extends AppCompatActivity {

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
        setContentView(R.layout.activity_credits);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Main
        // Znajdź przycisk i ustaw kliknięcie
        ImageButton ReturnButton = findViewById(R.id.Return_button_credits);
        ReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uruchomienie aktywności Main
                Intent intent = new Intent(Credits.this, MainActivity.class);
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

}