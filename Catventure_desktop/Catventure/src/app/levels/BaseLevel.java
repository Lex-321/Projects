package app.levels;

import app.BGControl;
import app.BGUpdate;
import app.Catventurer;
import app.Music;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Klasa bazowa poziomow gry
 */
public class BaseLevel implements BGUpdate {
    protected boolean isPlaying;
    protected Thread gameThread;
    public Catventurer catventurer;
    protected Image background;
    protected List<Obstacles> obstacles;
    protected float backgroundOffset = 0;
    public float backgroundSpeed = 10.0f;
    public float obstacleSpeed = 10.0f;
    private Random random = new Random();
    public int screenWidth = 800;
    public int screenHeight = 600;
    public float levelLength;
    public int NUM_OBSTACLES = 5;
    public int numDiamonds = (int) (NUM_OBSTACLES * 1.5);
    private int obstaclesCleared = 0;
    public long startTime;
    protected List<Collectibles> collectibles;
    public int score;
    protected int currentLevel;
    protected int selectedSlot;
    private boolean isLevelCompleted = false;

    /**
     * Konstruktor
     * @param avatarIndex index avatara
     * @param level poziom
     * @param selectedSlot wybrany slot zapisu
     */
    public BaseLevel(int avatarIndex, int level, int selectedSlot) {
        this.currentLevel = level;
        this.selectedSlot = selectedSlot;
        this.background = new Image(getClass().getResourceAsStream("/resources/background_day.jpg"));
        Music.getInstance().playGameMusic();

        levelLength = screenWidth * 5;

        obstacles = new ArrayList<>();
        collectibles = new ArrayList<>();

        initializeObstacles();
        initializeCollectibles();
        startTime = System.currentTimeMillis();

        // Wszystkie klatki kotków dla biegu
        Image[][] allRunFrames = {
                {
                        new Image(getClass().getResourceAsStream("/resources/black_cat_run1.png")),
                        new Image(getClass().getResourceAsStream("/resources/black_cat_run2.png"))
                },
                {
                        new Image(getClass().getResourceAsStream("/resources/grey_cat_run1.png")),
                        new Image(getClass().getResourceAsStream("/resources/grey_cat_run2.png"))
                },
                {
                        new Image(getClass().getResourceAsStream("/resources/orange_cat_run1.png")),
                        new Image(getClass().getResourceAsStream("/resources/orange_cat_run2.png"))
                },
                {
                        new Image(getClass().getResourceAsStream("/resources/white_cat_run1.png")),
                        new Image(getClass().getResourceAsStream("/resources/white_cat_run2.png"))
                }
        };

// Wszystkie klatki kotków dla skoku
        Image[][] allJumpFrames = {
                {
                        new Image(getClass().getResourceAsStream("/resources/black_cat_jump1.png")),
                        new Image(getClass().getResourceAsStream("/resources/black_cat_jump2.png")),
                        new Image(getClass().getResourceAsStream("/resources/black_cat_jump3.png"))
                },
                {
                        new Image(getClass().getResourceAsStream("/resources/grey_cat_jump1.png")),
                        new Image(getClass().getResourceAsStream("/resources/grey_cat_jump2.png")),
                        new Image(getClass().getResourceAsStream("/resources/grey_cat_jump3.png"))
                },
                {
                        new Image(getClass().getResourceAsStream("/resources/orange_cat_jump1.png")),
                        new Image(getClass().getResourceAsStream("/resources/orange_cat_jump2.png")),
                        new Image(getClass().getResourceAsStream("/resources/orange_cat_jump3.png"))
                },
                {
                        new Image(getClass().getResourceAsStream("/resources/white_cat_jump1.png")),
                        new Image(getClass().getResourceAsStream("/resources/white_cat_jump2.png")),
                        new Image(getClass().getResourceAsStream("/resources/white_cat_jump3.png"))
                }
        };


        catventurer = new Catventurer(allRunFrames[avatarIndex], allJumpFrames[avatarIndex], 100);
        BGControl.fetchAndUpdateBackground(this, level);
    }

    /**
     * inicjalizacja przeszkod
     */
    private void initializeObstacles() {
        float lastObstacleX = 0;
        float minOffset = 500;
        float offsetRange = 800;

        Image[] obstacleImages = {
                new Image(getClass().getResourceAsStream("/resources/obstacle1.png")),
                new Image(getClass().getResourceAsStream("/resources/obstacle2.png")),
                new Image(getClass().getResourceAsStream("/resources/obstacle3.png")),
                new Image(getClass().getResourceAsStream("/resources/obstacle4.png")),
                new Image(getClass().getResourceAsStream("/resources/obstacle5.png"))
        };

        for (int i = 0; i < NUM_OBSTACLES; i++) {
            float obstacleX = lastObstacleX + minOffset + random.nextInt((int) offsetRange);
            lastObstacleX = obstacleX;

            // Wybierz losowy obraz przeszkody
            Image randomObstacleImage = obstacleImages[random.nextInt(obstacleImages.length)];

            obstacles.add(new Obstacles(randomObstacleImage, obstacleX));
        }
    }
    /**
     * inicjalizacja obiektów Collectibles
     */
    private void initializeCollectibles() {
        float collectibleOffset = 500;
        Image[] collectibleImages = {
                new Image(getClass().getResourceAsStream("/resources/diamond_blue.png")),
                new Image(getClass().getResourceAsStream("/resources/diamond_green.png")),
                new Image(getClass().getResourceAsStream("/resources/diamond_red.png")),
                new Image(getClass().getResourceAsStream("/resources/diamond_purple.png"))
        };

        for (int i = 0; i < numDiamonds; i++) {
            float collectibleX = collectibleOffset * (i + 1);
            float collectibleY = 620 - 170 - random.nextInt(170);

            // Wybierz losowy obraz kolekcjonalu
            Image randomCollectibleImage = collectibleImages[random.nextInt(collectibleImages.length)];

            collectibles.add(new Collectibles(randomCollectibleImage, collectibleX, collectibleY, 10.0f, 50, 50));
        }
    }

    private float totalBackgroundOffset = 0;

    /**
     * odswierzanie tła
     */
    public void update() {
        // Aktualizacja przesunięcia tła
        backgroundOffset -= backgroundSpeed;
        totalBackgroundOffset += backgroundSpeed;

        if (backgroundOffset <= -screenWidth) {
            backgroundOffset += screenWidth; // Resetuj przesunięcie do początkowej wartości
        }

        // Sprawdzenie, czy gra się zakończyła
        if (totalBackgroundOffset >= levelLength) {
            isLevelCompleted = true; // Oznacz poziom jako ukończony
            endLevel(); // Zakończ poziom
            return;
        }

        // Aktualizacja przeszkód
        for (Obstacles obstacle : obstacles) {
            obstacle.update(obstacleSpeed);
            if (obstacle.getX() + obstacle.getBoundingBox().getWidth() < 0) {
                obstacle.setX(obstacle.getX() + levelLength);
            }
            if (catventurer.collidesWith(obstacle)) {
                isLevelCompleted = false; // Oznacz poziom jako nieukończony
                endLevel(); // Zakończ poziom
                return;
            }
        }

        // Aktualizacja collectibles
        for (Collectibles collectible : collectibles) {
            collectible.update(obstacleSpeed, screenHeight / 2, obstacles);
            if (!collectible.isCollected() && catventurer.getBoundingBox().intersects(collectible.getBoundingBox())) {
                collectible.collect();
                score += 10;
            }
        }

        // Aktualizacja bohatera
        catventurer.update();
    }

    /**
     * rysowanie poziomow
     * @param gc
     */
    public void draw(GraphicsContext gc) {
        gc.drawImage(background, backgroundOffset, 0);
        gc.drawImage(background, backgroundOffset + screenWidth, 0);

        for (Obstacles obstacle : obstacles) {
            obstacle.draw(gc);
        }

        for (Collectibles collectible : collectibles) {
            collectible.draw(gc);
        }

        gc.setFill(Color.web("#4F4F4E"));
        gc.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 24));
        int timeInDecyseconds = (int) ((System.currentTimeMillis() - startTime) / 100);

        String timeText = "" + timeInDecyseconds;
        double textWidth = new Text(timeText).getLayoutBounds().getWidth();

        gc.fillText(timeText, (screenWidth - textWidth) / 2, 50);

        catventurer.draw(gc);
    }

    /**
     * konczenie poziomow
     */
    private void endLevel() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        int timeInDeciseconds = (int) (elapsedTime / 100);

        isPlaying = false;

        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("saves.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Nie znaleziono pliku saves.properties. Tworzenie nowego...");
        }

        String prefix = "slot_" + selectedSlot + "_";

        if (isLevelCompleted) {
            currentLevel++;
            if (currentLevel > 3) {
                currentLevel = 1; // Reset poziomu po ukończeniu 3 poziomów
            }
        }

        properties.setProperty(prefix + "current_level", String.valueOf(currentLevel));
        properties.setProperty(prefix + "score", String.valueOf(score));
        properties.setProperty(prefix + "time", String.valueOf(timeInDeciseconds));

        try (FileOutputStream fos = new FileOutputStream("saves.properties")) {
            properties.store(fos, "Game Save");
            System.out.println("Zapisano poziom: " + currentLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stop() {
        isPlaying = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isLevelCompleted() {
        return isLevelCompleted;
    }

    public Catventurer getCatventurer() {
        return catventurer;
    }

    public List<Obstacles> getObstacles() {
        return obstacles;
    }

    @Override
    public void setBackgroundImage(Image background) {
        this.background = background;
    }
}
