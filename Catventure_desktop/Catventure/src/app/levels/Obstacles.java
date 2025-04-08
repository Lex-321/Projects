package app.levels;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import java.util.Random;

import static app.Catventurer.GROUND_LEVEL;

/**
 * Klasa odpowiedzialna za mechaniki przeszkod
 */
public class Obstacles {
    private Image obstacleImage;
    private double x, y;
    private Rectangle2D boundingBox;

    /**
     * Konstruktor
     * @param obstacleImage
     * @param startX pozycja starowa przeszkody
     */
    public Obstacles(Image obstacleImage, double startX) {
        this.x = startX;
        this.obstacleImage = obstacleImage; // Przyjmujemy Image bezpośrednio

        // Pozycja Y obliczona tak, aby dolna krawędź przeszkody była na poziomie ziemi
        this.y = GROUND_LEVEL + 70;

        boundingBox = new Rectangle2D(x, y, obstacleImage.getWidth(), obstacleImage.getHeight());
    }

    /**
     * Odswierzanie
     * @param speed prędkosc ruchu na ekranie
     */
    public void update(double speed) {
        x -= speed; //przesunięcie przeszkody z określoną prędkością

        // Aktualizacja bounding boxu
        boundingBox = new Rectangle2D(x, y, obstacleImage.getWidth(), obstacleImage.getHeight());
    }

    public Rectangle2D getBoundingBox() {
        return new Rectangle2D(x, y, obstacleImage.getWidth(), obstacleImage.getHeight());
    }

    /**
     * Rysowanie przszkod
     * @param gc
     */
    public void draw(GraphicsContext gc) {
        gc.drawImage(obstacleImage, x, y);

//         debugowanie bounding boxu dla przeszkod
//        gc.setStroke(Color.BLUE);
//        gc.strokeRect(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getWidth(), boundingBox.getHeight());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        boundingBox = new Rectangle2D(x, y, obstacleImage.getWidth(), obstacleImage.getHeight());
    }
}
