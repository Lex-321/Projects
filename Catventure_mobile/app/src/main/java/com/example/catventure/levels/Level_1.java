package com.example.catventure.levels;


import android.content.Context;

public class Level_1 extends BaseLevel {

    public Level_1(Context context, int avatarIndex, int level, int selectedSlot) {
        super(context, avatarIndex, level, selectedSlot);

        this.backgroundSpeed = 10.0f;  // Prędkość tła
        this.obstacleSpeed = 10.0f;   // Prędkość przeszkód
        this.NUM_OBSTACLES = 5;       // Liczba przeszkód
        //this.levelLength = screenWidth * 10;
    }
}
