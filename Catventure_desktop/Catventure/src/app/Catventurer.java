package app;

import app.levels.Obstacles;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.geometry.Rectangle2D;

import java.util.List;

/**
 * Klasa gracza. Odpowiedzialna za mechaniki gracza i sterowanie
 */
public class Catventurer {
    private Image[] runFrames;
    private Image[] jumpFrames;
    private Image currentFrame;
    private Rectangle2D boundingBox;
    private float x, y;
    public static final int GROUND_LEVEL = 340; // Stała poziomu gruntu
    private float velocityY = 0;
    private boolean isJumping = false;
    private boolean doubleJumpAvailable = true;
    private int jumpState = 0;

    private int currentRunFrame = 0;
    private long lastFrameChangeTime = 0;
    private static final long FRAME_DURATION = 100; // czas trwania jednej klatki w ms
    /**
     * Konstruktor gracza
     * @param runFrames klatki animacji biegu
     * @param jumpFrames klatki animacji skoku
     * @param startX pozycja poczatkowa gracza
     */
    public Catventurer(Image[] runFrames, Image[] jumpFrames, float startX) {
        this.runFrames = runFrames;
        this.jumpFrames = jumpFrames;
        this.x = startX;
        this.y = GROUND_LEVEL;
        this.currentFrame = runFrames[0];
        this.boundingBox = new Rectangle2D(x, y, currentFrame.getWidth(), currentFrame.getHeight());
    }

    /**
     * Skok
     */
    public void jump() {
        if (!isJumping) {
            // Pierwszy skok
            isJumping = true;
            jumpState = 1;
            velocityY = -16f; //sila skoku
        } else if (doubleJumpAvailable) {
            // Drugi skok
            doubleJumpAvailable = false;
            jumpState = 1;
            velocityY = -10f; //sila skoku (mniejsza przy drugim)
        }
    }

    /**
     * odswierzanie gracza
     */
    public void update() {
        if (isJumping) {
            velocityY += 0.6f; //grawitacja
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
                doubleJumpAvailable = true;
                jumpState = 3;
                currentFrame = jumpFrames[2];
            }
        } else {
            //animacja biegu
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameChangeTime > FRAME_DURATION) {
                currentRunFrame = (currentRunFrame + 1) % runFrames.length;
                currentFrame = runFrames[currentRunFrame];
                lastFrameChangeTime = currentTime;
            }
            jumpState = 0;
        }

        //zmniejszenie bounding boxu
        double paddingX = currentFrame.getWidth() * 0.55;
        double paddingY = currentFrame.getHeight() * 0.55;

        boundingBox = new Rectangle2D(
                x + paddingX / 2,
                y + paddingY / 2,
                currentFrame.getWidth() - paddingX,
                currentFrame.getHeight() - paddingY
        );
    }

    /**
     * Rysowanie gracza
     * @param gc
     */
    public void draw(GraphicsContext gc) {
        gc.drawImage(currentFrame, x, y);

        //debugowanie - bounding box
//        gc.setStroke(javafx.scene.paint.Color.GREEN);
//        gc.setLineWidth(2);
//        gc.strokeRect(boundingBox.getMinX(), boundingBox.getMinY(),
//                boundingBox.getWidth(), boundingBox.getHeight());
    }

    /**
     * mechanika kolizji
     * @param obstacle przeszkoda
     * @return
     */
    public boolean collidesWith(Obstacles obstacle) {
        return this.boundingBox.intersects(obstacle.getBoundingBox());
    }

    public Rectangle2D getBoundingBox() {
        return boundingBox;
    }

    //sprawdza czy jest kolizja z jakakolwiek przeszkoda
    public boolean collidesWithAny(List<Obstacles> obstacles) {
        for (Obstacles obstacle : obstacles) {
            if (this.boundingBox.intersects(obstacle.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }


}
