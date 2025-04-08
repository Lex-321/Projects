package com.example.catventure;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.SystemClock;

import com.example.catventure.levels.Obstacles;

public class Catventurer {
    public static final int GROUND_LEVEL = 440; //poziom(wysokosc) na ktorym jest gracz
    private boolean isJumping = false;
    private boolean doubleJumpAvailable = true; //czy mozna wykonac drugi skok
    private int jumpState = 0; //0 - postac biegnie, 1 - poczatkowy stan skoku, 2 - skok "w locie", 3 - skok - ladowanie

    private Bitmap[] runFrames;
    private Bitmap[] jumpFrames;
    private Bitmap currentFrame;

    private float x, y;
    private float velocityY = 0;
    private float gravity = 1.0f;

    private Rect boundingBox; //prostokatny obszar otaczajacy obiekt, sluzy jako narzedzie do wykrywania kolizji

    private int currentRunFrame = 0; //wyswietlana klatka biegu
    private long lastFrameChangeTime = 0; //kiedy zmienino klatke
    private long frameDuration = 85; //czas trwania jednej klatki w milisekundach

    public Catventurer(Context context, int[] runResources, int[] jumpResources, float startX) {
        this.x = startX;
        this.y = GROUND_LEVEL;

        runFrames = new Bitmap[runResources.length];
        for (int i = 0; i < runResources.length; i++) {
            runFrames[i] = BitmapFactory.decodeResource(context.getResources(), runResources[i]);
        }

        jumpFrames = new Bitmap[jumpResources.length];
        for (int i = 0; i < jumpResources.length; i++) {
            jumpFrames[i] = BitmapFactory.decodeResource(context.getResources(), jumpResources[i]);
        }

        currentFrame = runFrames[0];
        boundingBox = new Rect(
                (int) x,
                (int) y,
                (int) (x + currentFrame.getWidth()),
                (int) (y + currentFrame.getHeight())
        );
    }

    public void jump() {
        if (!isJumping) {
            //pierwszy skok
            isJumping = true;
            jumpState = 1;
            velocityY = -22f; //sila skoku
        } else if (doubleJumpAvailable) {
            //drugi skok
            doubleJumpAvailable = false;
            jumpState = 1;
            velocityY = -12f; //sila skoku - mniejsza przy drugim
        }
    }

    public void update() {
        if (isJumping) {
            velocityY += gravity * 0.6f;
            y += velocityY;

            //animacja skoku
            if (jumpState == 1) {
                currentFrame = jumpFrames[0]; //klatka startowa
                jumpState = 2;
            } else if (jumpState == 2) {
                currentFrame = jumpFrames[1]; //klatka "w locie"
            }

            if (y >= GROUND_LEVEL) {
                y = GROUND_LEVEL;
                velocityY = 0;
                isJumping = false;
                doubleJumpAvailable = true; //reset mozliwosci podwojnego skoku po wyladowaniu
                jumpState = 3;
                currentFrame = jumpFrames[2]; //klatka ladowania
            }
        } else {
            //animacja biegu
            if (SystemClock.uptimeMillis() - lastFrameChangeTime > frameDuration) {
                currentRunFrame = (currentRunFrame + 1) % runFrames.length;
                currentFrame = runFrames[currentRunFrame];
                lastFrameChangeTime = SystemClock.uptimeMillis();
            }
            jumpState = 0;
        }

        // Obliczenie marginesów dla bounding boxu
        int boxWidth = currentFrame.getWidth();
        int boxHeight = currentFrame.getHeight();
        int marginX = (int) (boxWidth * 0.30);
        int marginY = (int) (boxHeight * 0.30);

        // Aktualizacja bounding boxu z marginesami
        boundingBox.set(
                (int) x + marginX,
                (int) y + marginY,
                (int) (x + boxWidth - marginX),
                (int) (y + boxHeight - marginY)
        );
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(currentFrame, x, y, null);
    }

    public boolean collidesWith(Obstacles obstacle) {
        return Rect.intersects(boundingBox, obstacle.getBoundingBox());
    }

    public boolean isJumping() {
       return isJumping;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

}
