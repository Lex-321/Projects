package com.example.catventure.levels;

import android.os.Bundle;


public class Level3Activity extends BaseLevelActivity<Level_3> {
    private int selectedSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        selectedSlot = getIntent().getIntExtra("selected_slot", -1);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected Level_3 createLevel(int avatarIndex, int levelNumber) {
        return new Level_3(this, avatarIndex, levelNumber, selectedSlot);
    }
}
