package com.example.catventure.levels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.List;

public class Collectibles {
    private Bitmap image;
    private float x, y;
    private Rect boundingBox;
    private boolean isCollected; //czy diament zostal zebrany
    private float speed; //Prędkość ruchu collectibles

    public Collectibles(Context context, int resourceId, float startX, float startY, float speed, int obstacleWidth, int obstacleHeight) {
        Bitmap originalImage = BitmapFactory.decodeResource(context.getResources(), resourceId);

        //skalowanie obrazu do rozmiaru przeszkody
        image = Bitmap.createScaledBitmap(originalImage, obstacleWidth, obstacleHeight, false);

        x = startX;
        y = startY;
        isCollected = false;
        this.speed = speed;

        // Inicjalizuj bounding box
        updateBoundingBox();
    }

    public void update(int maxHeight, List<com.example.catventure.levels.Obstacles> obstacles) {
        if (!isCollected) {
            x -= speed;

            if (x + image.getWidth() < 0) {
                // Generowanie nowej pozycji aż nie będzie nachodzić na przeszkodę
                do {
                    x = (float) (Math.random() * 1000 + 2000); // Losowa nowa pozycja X
                    y = (float) (Math.random() * maxHeight); // Nowa pozycja Y do maxHeight
                } while (isOverlappingObstacle(x, y, image.getWidth(), image.getHeight(), obstacles));

                isCollected = false; // Reset flagi zebranych collectibles
            }
            updateBoundingBox();
        }
    }

    //Metoda sprawdzającą kolizję z przeszkodami
    private boolean isOverlappingObstacle(float x, float y, int width, int height, List<com.example.catventure.levels.Obstacles> obstacles) {
        Rect collectibleRect = new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));

        for (com.example.catventure.levels.Obstacles obstacle : obstacles) {
            if (Rect.intersects(collectibleRect, obstacle.getBoundingBox())) {
                return true; // Nachodzi na przeszkodę
            }
        }
        return false; // Brak kolizji
    }


    public void draw(Canvas canvas) {
        if (!isCollected) {
            canvas.drawBitmap(image, x, y, null);
        }
    }

    private void updateBoundingBox() {
        boundingBox = new Rect(
                (int) x,
                (int) y,
                (int) (x + image.getWidth()),
                (int) (y + image.getHeight())
        );
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    //naprawic score - tzn. wyswietla o 10pkt wiecej niz sie zbiera ;/
    public void collect() {
        isCollected = true;
    }

    public boolean isCollected() {
        return isCollected;
    }
}
