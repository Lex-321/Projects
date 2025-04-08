package app.levels;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;
import java.util.List;

/**
 * Klasa obiektow Collectibles
 */
public class Collectibles {
    private Image image;
    private double x, y;
    private Rectangle2D boundingBox;
    private boolean isCollected; //czy diament zostal zebrany
    private double speed; //Prędkość ruchu collectibles
    private int width, height;

    /**
     * Konstuktor
     * @param image
     * @param x pozycja x
     * @param y pozcja
     * @param speed prędkosc poruszania po ekranie
     * @param width szeroksc
     * @param height wysoksc
     */
    public Collectibles(Image image, float x, float y, float speed, int width, int height) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.width = width;
        this.height = height;
    }

    /**
     * Odswierznie obiektow
     * @param speed predkosc poruszania po ekranie
     * @param maxHeight maksymalna wykosc ekrany do ktorej mogą się pojawiac
     * @param obstacles lista zasobow
     */
    public void update(double speed, double maxHeight, List<Obstacles> obstacles) {
        if (!isCollected) {
            x -= speed; // Użycie podanej prędkości

            if (x + image.getWidth() < 0) {
                // Generowanie nowej pozycji aż nie będzie nachodzić na przeszkodę
                do {
                    x = (Math.random() * 1000 + 2000); // Losowa nowa pozycja X
                    y = (Math.random() * maxHeight); // Nowa pozycja Y do maxHeight
                } while (isOverlappingObstacle(x, y, image.getWidth(), image.getHeight(), obstacles));

                isCollected = false; // Reset flagi zebranych collectibles
            }
            updateBoundingBox();
        }
    }

    /**
     * Metoda sprawdzająca kolizję z obiektami Collectibles
     * @param x
     * @param y
     * @param width
     * @param height
     * @param obstacles
     * @return
     */

    private boolean isOverlappingObstacle(double x, double y, double width, double height, List<Obstacles> obstacles) {
        Rectangle2D collectibleRect = new Rectangle2D(x, y, width, height);

        for (Obstacles obstacle : obstacles) {
            if (collectibleRect.intersects(obstacle.getBoundingBox())) {
                return true; // Nachodzi na przeszkodę
            }
        }
        return false; // Brak kolizji
    }

    public void draw(GraphicsContext gc) {
        if (!isCollected) {
            gc.drawImage(image, x, y, image.getWidth() * 0.5, image.getHeight() * 0.5); // 50% oryginalnego rozmiaru
        }
    }

    private void updateBoundingBox() {
        boundingBox = new Rectangle2D(x, y, image.getWidth(), image.getHeight());
    }

    public Rectangle2D getBoundingBox() {
        return boundingBox;
    }

    // Naprawienie zbierania punktów, aby wyświetlało prawidłową wartość
    public void collect() {
        isCollected = true;
    }

    public boolean isCollected() {
        return isCollected;
    }
}
