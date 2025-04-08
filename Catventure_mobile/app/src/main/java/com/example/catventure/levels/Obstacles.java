package com.example.catventure.levels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Random;

public class Obstacles {
    private Bitmap obstacleImage;
    private float x, y;
    private Rect boundingBox;

    private static final int GROUND_LEVEL = 620;

    public Obstacles(Context context, int[] resourceIds, float startX) {
        this.x = startX;

        // Załaduj losowy obraz przeszkody z tablicy resourceIds
        Random random = new Random();
        int randomIndex = random.nextInt(resourceIds.length);
        obstacleImage = BitmapFactory.decodeResource(context.getResources(), resourceIds[randomIndex]);

        // Pozycja Y obliczona tak, aby dolna krawędź przeszkody była na poziomie ziemi
        this.y = GROUND_LEVEL;

        boundingBox = new Rect((int) x, (int) y, (int) (x + obstacleImage.getWidth()), GROUND_LEVEL);
    }

    public void update(float speed) {
        x -= speed; //przesuniecie przeszkody z określoną prędkością

        // Aktualizacja bounding boxu
        boundingBox.set((int) x, (int) y, (int) (x + obstacleImage.getWidth()), GROUND_LEVEL);
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(obstacleImage, x, y, null);

//        //rysowanie bounding boxu (debug)
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(android.graphics.Color.BLUE);
//        canvas.drawRect(boundingBox, paint);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        boundingBox.set((int) x, (int) y, (int) (x + obstacleImage.getWidth()), GROUND_LEVEL);
    }
}
