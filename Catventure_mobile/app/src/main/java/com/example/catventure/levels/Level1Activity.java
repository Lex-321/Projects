package com.example.catventure.levels;

import android.os.Bundle;

public class Level1Activity extends BaseLevelActivity<Level_1> {
    private int selectedSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Pobierz wybrany slot z Intentu
        selectedSlot = getIntent().getIntExtra("selected_slot", -1);

//        Logowanie dla debugowania
//        Log.d("Level1Activity", "Selected Slot: " + selectedSlot);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected Level_1 createLevel(int avatarIndex, int levelNumber) {
//        // Logowanie danych przekazywanych do Level_1
//        Log.d("Level1Activity", "Avatar Index: " + avatarIndex);
//        Log.d("Level1Activity", "Level Number: " + levelNumber);

        // Tworzenie instancji Level_1 z odpowiednimi parametrami
        return new Level_1(this, avatarIndex, levelNumber, selectedSlot);
    }
}
