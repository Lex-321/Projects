package app;

import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Klasa tworzaca niestandardowy kursor
 */
public class Custom_Cursor {
    public static ImageCursor createCustomCursor(String imagePath) {
        Image cursorImage = new Image(Objects.requireNonNull(Custom_Cursor.class.getResourceAsStream(imagePath)));
        return new ImageCursor(cursorImage);
    }
}