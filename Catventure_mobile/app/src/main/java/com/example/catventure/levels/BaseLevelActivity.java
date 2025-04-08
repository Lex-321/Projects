package com.example.catventure.levels;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.catventure.BGControl;
import com.example.catventure.Music;

public abstract class BaseLevelActivity<T extends BaseLevel> extends AppCompatActivity {
    protected T level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Music.getInstance().playGameMusic(this);
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Pobranie avatarIndex z Intentu
        int avatarIndex = getIntent().getIntExtra("avatarIndex", 0); // Domyślnie 0

        // Pobranie bieżącego poziomu z Intentu
        int levelNumber = getIntent().getIntExtra("level", 1); // Domyślnie poziom 1

        // Utwórz poziom, zależnie od klasy T
        level = createLevel(avatarIndex, levelNumber);

        // Ustaw poziom jako widok
        setContentView(level);

        // Pobierz i zaktualizuj tło dla poziomu
        BGControl.fetchAndUpdateBackground(this, level, levelNumber);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (level != null) {
            level.resume(); // Wznawiamy grę, jeśli była wstrzymana
        }
        Music.getInstance().resumeGameMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (level != null) {
            level.pause(); // Wstrzymujemy grę, jeśli aktywność jest wstrzymana
        }
        Music.getInstance().stopGameMusic();
    }

    // Klasa potomna musi zaimplementować tę metodę
    protected abstract T createLevel(int avatarIndex, int levelNumber);
}
