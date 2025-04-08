package com.example.catventure.levels;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("ViewConstructor")
public class Level_3 extends BaseLevel{

    public Level_3(Context context, int avatarIndex, int level, int selectedSlot) {
        super(context, avatarIndex, level, selectedSlot);

        this.backgroundSpeed = 14.0f;  // Prędkość tła
        this.obstacleSpeed = 14.0f;   // Prędkość przeszkód
        this.NUM_OBSTACLES = 5;       // Liczba przeszkód
        //this.levelLength = screenWidth * 2;
    }
}
