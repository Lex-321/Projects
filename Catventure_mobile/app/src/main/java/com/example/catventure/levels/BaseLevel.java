package com.example.catventure.levels;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.catventure.BGControl;
import com.example.catventure.BGUpdate;
import com.example.catventure.Catventurer;
import com.example.catventure.GameOverActivity;
import com.example.catventure.LevelCompletedActivity;
import com.example.catventure.Music;
import com.example.catventure.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BaseLevel extends SurfaceView implements Runnable, SurfaceHolder.Callback,BGUpdate {
    private Thread gameThread; // Wątek gry odpowiedzialny za pętlę gry
    private boolean isPlaying;
    protected Catventurer catventurer;
    protected Bitmap background;
    protected List<com.example.catventure.levels.Obstacles> obstacles;
    private float backgroundOffset = 0;
    public float backgroundSpeed = 10.0f;
    public float obstacleSpeed = 10.0f;
    private Random random = new Random();
    public int screenWidth;
    private int screenHeight;
    private GestureDetector gestureDetector;
    public float levelLength; //"dlugosc" poziomu, ile razy tlo wystepuje w jednym poziomie
    public int NUM_OBSTACLES = 5; //liczba przeszkod
    public int numDiamonds = (int) (NUM_OBSTACLES * 1.5); //liczba diamentow do zdobycia np. 1.5x wieksza niz l.przeszkod
    private int obstaclesCleared = 0; // Liczba przeszkód przeskoczonych przez gracza
    private long startTime; // Czas rozpoczęcia gry
    private Paint timerPaint; // Używane do rysowania licznika na ekranie
    protected List<com.example.catventure.levels.Collectibles> collectibles;
    private int score; // Wynik gracza
    protected int currentLevel; // Przechowywanie bieżącego poziomu
    protected int selectedSlot;
    private Bitmap backgroundBitmap;
    public static boolean isLevelCompleted = false;

    public BaseLevel(Context context, int avatarIndex, int level, int selectedSlot) {
        super(context);
        this.currentLevel = level;
        this.selectedSlot = selectedSlot;
        Music.getInstance().playGameMusic(context);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getY() > e2.getY() && Math.abs(velocityY) > Math.abs(velocityX)) {
                    catventurer.jump(); // Obsługuje pierwszy, jak i drugi skok
                    return true;
                }
                return false;
            }
        });

        //inicjalizacja licznika czasu
        startTime = System.currentTimeMillis(); //pobranie biezacego czasu w milisekundach

        // Ustawienia Paint dla tekstu licznika
        timerPaint = new Paint();
        timerPaint.setColor(ContextCompat.getColor(context, R.color.text_color));
        timerPaint.setTextSize(75);
        timerPaint.setAntiAlias(true); // Wygładzanie tekstu
        timerPaint.setTypeface(ResourcesCompat.getFont(context, R.font.bradley_font));

        BGControl.fetchAndUpdateBackground(context, this, currentLevel);

        // Inicjalizacja domyślnego tła
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_day);

        //wszystkie klatki kotkow dla biegu
        int[][] allRunFrames = {
                {R.drawable.black_cat_run1, R.drawable.black_cat_run2},
                {R.drawable.grey_cat_run1, R.drawable.grey_cat_run2},
                {R.drawable.orange_cat_run1, R.drawable.orange_cat_run2},
                {R.drawable.white_cat_run1, R.drawable.white_cat_run2}
        };

        //wszystkie klatki kotkow dla skoku
        int[][] allJumpFrames = {
                {R.drawable.black_cat_jump1, R.drawable.black_cat_jump2, R.drawable.black_cat_jump3},
                {R.drawable.grey_cat_jump1, R.drawable.grey_cat_jump2, R.drawable.grey_cat_jump3},
                {R.drawable.orange_cat_jump1, R.drawable.orange_cat_jump2, R.drawable.orange_cat_jump3},
                {R.drawable.white_cat_jump1, R.drawable.white_cat_jump2, R.drawable.white_cat_jump3}
        };

        //utworzenie postaci w rozgrywce
        catventurer = new Catventurer(
                context,
                allRunFrames[avatarIndex],
                allJumpFrames[avatarIndex],
                100
        );

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        levelLength = screenWidth * 5; // 5 długości ekranu

        obstacles = new ArrayList<>();

        //przeszkody
        int[] obstacleImages = {
                R.drawable.obstacle1,
                R.drawable.obstacle2,
                R.drawable.obstacle3,
                R.drawable.obstacle4,
                R.drawable.obstacle5
        };

        float lastObstacleX = 0;
        float minOffset = 900;
        float offsetRange = 300;

        for (int i = 0; i < NUM_OBSTACLES; i++) {
            float obstacleX;
            do {
                obstacleX = lastObstacleX + minOffset + random.nextInt((int) offsetRange);
            } while (isOverlappingObstacle(obstacleX, 620, 100, 100));
            lastObstacleX = obstacleX;

        obstacles.add(new com.example.catventure.levels.Obstacles(
                    context,
                    obstacleImages,
                    obstacleX
            ));
        }

        collectibles = new ArrayList<>();
        float collectibleOffset = 800; // Początkowe odstępy między collectibles

        //collectibles - diamenty - punkty do zbierania
        int[] collectibleImages = {
                R.drawable.diamond_blue,
                R.drawable.diamond_purple,
                R.drawable.diamond_green,
                R.drawable.diamond_red
        };

        //rozmiar przeszkody-collectible
        int obstacleWidth = 100;
        int obstacleHeight = 100;
        int maxHeight = screenHeight; // Maksymalna wysokość pojawiania się diamentów

        for (int i = 0; i < numDiamonds; i++) {
            float collectibleX, collectibleY;

            // Generuj pozycję do skutku, aż nie będzie nachodzić na przeszkodę
            do {
                collectibleX = collectibleOffset * (i + 1); // Odsunięcie w poziomie
                collectibleY = 620 - 170 - random.nextInt(170);
            } while (isOverlappingObstacle(collectibleX, collectibleY, obstacleWidth, obstacleHeight));

            float collectibleSpeed = 10.0f; //predkosc

            collectibles.add(new com.example.catventure.levels.Collectibles(
                    context,
                    collectibleImages[i % collectibleImages.length],
                    collectibleX,
                    collectibleY,
                    collectibleSpeed,
                    obstacleWidth,
                    obstacleHeight));
        }
    }
    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        backgroundOffset -= backgroundSpeed;
        if (backgroundOffset <= -screenWidth) {
            backgroundOffset = 0;
        }

        //przeszkody
        for (com.example.catventure.levels.Obstacles obstacle : obstacles) {
            obstacle.update(obstacleSpeed);

            if (obstacle.getX() + obstacle.getBoundingBox().width() < 0) {
                // Przeszkoda została przeskoczona
                obstacle.setX(obstacle.getX() + levelLength);
                obstaclesCleared++; // Zwiększ licznik przeszkód

                if (obstaclesCleared >= NUM_OBSTACLES) {
                    // Wszystkie przeszkody pokonane, zakończ poziom
                    isLevelCompleted = true;
                    endLevel();
                    return;
                }
            }
        }

        for (com.example.catventure.levels.Obstacles obstacle : obstacles) {
            if (catventurer.collidesWith(obstacle)) {
                Intent intent = new Intent(getContext(), GameOverActivity.class);
                getContext().startActivity(intent);
                isPlaying = false;
                break;
            }
        }

        //punkty
        for (com.example.catventure.levels.Collectibles collectible : collectibles) {
            collectible.update(screenHeight / 2, obstacles);
            if (!collectible.isCollected() && Rect.intersects(catventurer.getBoundingBox(), collectible.getBoundingBox())) {
                collectible.collect();
                score += 10;
            }
        }

        catventurer.update();
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.WHITE);

            drawBackground(canvas);

            catventurer.draw(canvas);

            for (com.example.catventure.levels.Obstacles obstacle : obstacles) {
                obstacle.draw(canvas);
            }

            // Rysuj licznik czasu
            drawTimer(canvas);

            for (com.example.catventure.levels.Collectibles collectible : collectibles) {
                collectible.draw(canvas);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public synchronized void setBackgroundBitmap(Bitmap background) {
        this.backgroundBitmap = background;
    }

    private synchronized Bitmap getBackgroundBitmap() {
        return (backgroundBitmap != null) ? backgroundBitmap : background;
    }

    private void drawBackground(Canvas canvas) {
        Bitmap currentBackground = getBackgroundBitmap();
        Bitmap scaledBackground = Bitmap.createScaledBitmap(currentBackground, screenWidth, screenHeight, false);
        canvas.drawBitmap(scaledBackground, backgroundOffset, 0, null);
        canvas.drawBitmap(scaledBackground, backgroundOffset + screenWidth, 0, null);
    }

    private void sleep() {
        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        if (!isPlaying) {
            BGControl.fetchAndUpdateBackground(getContext(), this, currentLevel);
            isPlaying = true;
            if (gameThread == null || !gameThread.isAlive()) {
                gameThread = new Thread(this);
                gameThread.start();
            }
        }
    }

    public void pause() {
        try {
            isPlaying = false;
            if (gameThread != null) {
                gameThread.join(); // Czekaj na zakończenie wątku gry
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                isPlaying = false; // Ustaw isPlaying na false, aby zatrzymać pętlę wątku
                if (gameThread != null) {
                    gameThread.join(); // Czekaj na zakończenie wątku
                }
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawTimer(Canvas canvas) {
        // Oblicz, ile czasu minęło od startu
        long elapsedTime = System.currentTimeMillis() - startTime;

        // Zamień czas na decysekundy
        int decyseconds = (int) (elapsedTime / 100);

        String timeText = String.valueOf(decyseconds);

        // Oblicz pozycję X i Y, aby wyśrodkować tekst
        float textWidth = timerPaint.measureText(timeText);
        Paint.FontMetrics fontMetrics = timerPaint.getFontMetrics();
        //float textHeight = fontMetrics.bottom - fontMetrics.top;

        float centerX = (screenWidth - textWidth) / 2;  // Środek ekranu w poziomie
        float topY = 120;

        // Rysuj tekst na ekranie
        canvas.drawText(timeText, centerX, topY, timerPaint);

    }

    private boolean isOverlappingObstacle(float x, float y, int width, int height) {
        Rect obstacleRect = new Rect((int) x, (int) y, (int) (x + width), (int) (y + height));

        for (com.example.catventure.levels.Obstacles obstacle : obstacles) {
            if (Rect.intersects(obstacleRect, obstacle.getBoundingBox())) {
                return true; // Nachodzi na inną przeszkodę
            }
        }
        return false; // Brak kolizji
    }

    private void endLevel() {
        Intent intent = new Intent(getContext(), LevelCompletedActivity.class);
        BGControl.fetchAndUpdateBackground(getContext(), this, currentLevel + 1);
        // Przekazanie wyniku
        intent.putExtra("score", score);
        // Przekazanie czasu gry
        long elapsedTime = System.currentTimeMillis() - startTime;
        int timeInDeciseconds = (int) (elapsedTime / 100);
        intent.putExtra("time", timeInDeciseconds);

        // Sprawdzenie, czy poziom został zakończony
        if (isLevelCompleted) {
            intent.putExtra("level", currentLevel + 1);

            if (selectedSlot >= 0) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("CharacterPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Zapisz numer ostatnio ukończonego poziomu dla wybranego slota
                editor.putInt("last_completed_level_slot_" + selectedSlot, currentLevel);
                editor.apply();
            }
            intent.putExtra("selected_slot", selectedSlot);
        }

        getContext().startActivity(intent);
        isPlaying = false; // Zatrzymanie gry
    }

}
