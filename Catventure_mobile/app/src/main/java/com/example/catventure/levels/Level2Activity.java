package com.example.catventure.levels;

import android.os.Bundle;


public class Level2Activity extends BaseLevelActivity<Level_2> {
    private int selectedSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        selectedSlot = getIntent().getIntExtra("selected_slot", -1);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected Level_2 createLevel(int avatarIndex, int levelNumber) {
        return new Level_2(this, avatarIndex, levelNumber, selectedSlot);
    }
}
