package app;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Klasa ekranu gry
 */
public class GameScreen extends Application {

    private int avatarIndex;
    private String playerName;

    // Przechowywanie ścieżek do obrazów avatarów (np. "file:resources/images/black_cat.png")
    private String[] avatarImages = {
            "file:resources/black_cat.png",  // Index 0
            "file:resources/grey_cat.png",   // Index 1
            "file:resources/orange_cat.png", // Index 2
            "file:resources/white_cat.png"   // Index 3
    };

    @Override
    public void start(Stage primaryStage) {
        // Pobierz numer wybranego slota z Intent
        int selectedSlot = 1;  // Domyślnie ustawiamy slot na 1, możesz to dostosować.

        // Wczytanie danych z pliku "saves.properties"
        Properties properties = new Properties();
        try (FileReader reader = new FileReader("saves.properties")) {
            properties.load(reader);

            // Pobieranie danych na podstawie wybranego slota
            String avatarKey = "slot_" + selectedSlot + "_avatar";
            String nameKey = "slot_" + selectedSlot + "_name";
            avatarIndex = Integer.parseInt(properties.getProperty(avatarKey, "-1")); // Pobierz avatarIndex
            playerName = properties.getProperty(nameKey, "Player"); // Pobierz playerName

            // Znajdź ImageView i ustaw obrazek wybranego avatara
            Image avatarImage = new Image("file:" + avatarImages[avatarIndex]);
            ImageView avatarView = new ImageView(avatarImage);
            avatarView.setFitWidth(100);  // Przykładowa szerokość avatara
            avatarView.setFitHeight(100); // Przykładowa wysokość avatara

            // Dalsza logika gry (dodanie do widoku, wczytanie poziomu, itd.)
            System.out.println("Avatar: " + playerName + " - Avatar index: " + avatarIndex);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Uruchomienie aplikacji JavaFX
    }
}
