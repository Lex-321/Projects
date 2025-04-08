package app;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import app.levels.BaseLevel;
import app.levels.Level1;
import app.levels.Level2;
import app.levels.Level3;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * Glowny watek aplikacji
 */
public class MainApp extends Application {

    // Rozmiary okna
    double SCRX = 800;
    double SCRY = 600;

    // Niestandardowy kursor
    //ImageCursor customCursor = Custom_Cursor.createCustomCursor("/resources/cursor.png");

    // Lista catvatarów (ścieżki w zasobach)
    String[] imagePaths = {
            "/catvatars/black_cat.png",
            "/catvatars/grey_cat.png",
            "/catvatars/orange_cat.png",
            "/catvatars/white_cat.png",
    };

    /**
    * Start aplikacji
    */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setFullScreen(false);
        Music.getInstance().playBackgroundMusic(); // wywołanie muzyki

        // Przycisk Start Game
        Button StartGame = new Button();
        Image startGameImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/start_game.png")));
        ImageView startGameImageView = new ImageView(startGameImage);
        startGameImageView.setFitWidth(150.0);
        startGameImageView.setFitHeight(50.0);
        StartGame.setGraphic(startGameImageView);
        StartGame.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        StartGame.setOnAction((e) -> {
            Scene createSaveScene = CreateSaveScene(primaryStage);
            primaryStage.setScene(createSaveScene);
            GlobalSettings(createSaveScene);
        });

        // Przycisk Load Game
        Button LoadGame = new Button();
        Image loadGameImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/load_game.png")));
        ImageView loadGameImageView = new ImageView(loadGameImage);
        loadGameImageView.setFitWidth(150.0);
        loadGameImageView.setFitHeight(50.0);
        LoadGame.setGraphic(loadGameImageView);
        LoadGame.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        LoadGame.setOnAction((e) -> {
            Scene loadSaveScene = LoadSaveScene(primaryStage);
            primaryStage.setScene(loadSaveScene);
            //GlobalSettings(loadSaveScene);
        });

        // Przycisk Exit
        Button ExitGame = new Button();
        Image exitGameImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/exit_game.png")));
        ImageView exitGameImageView = new ImageView(exitGameImage);
        exitGameImageView.setFitWidth(150.0);
        exitGameImageView.setFitHeight(50.0);
        ExitGame.setGraphic(exitGameImageView);
        ExitGame.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        ExitGame.setOnAction((e) -> {
            Platform.exit();
        });

        // Tło ekranu tytułowego
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/background_blue_logo.png")));
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        // Układ przycisków na ekranie tytułowym
        VBox root = new VBox(10.0);
        root.getChildren().addAll(StartGame, LoadGame, ExitGame);
        root.setStyle("-fx-alignment: center; -fx-padding: 100 0 0 0;");
        root.setBackground(new Background(background));

        Scene Titlescreen = new Scene(root, SCRX, SCRY);
        GlobalSettings(Titlescreen); // globalne ustawienia (m.in. kursor)
        primaryStage.setTitle("Catventure");
        primaryStage.setScene(Titlescreen);
        primaryStage.show();
    }

    /**
    * Kursor i style css
    */
    private void GlobalSettings(Scene scene) {
       // scene.setCursor(customCursor);
        String globalCss = Objects.requireNonNull(getClass().getResource("/resources/style.css")).toExternalForm();
        scene.getStylesheets().add(globalCss);
    }

    /**
    * Tworzenie postaci
    */
    private Scene CreateSaveScene(Stage primaryStage) {
        LoadGame loadGame = new LoadGame(); // wczytanie istniejących zapisanych postaci

        // Przycisk powrotu
        Button backButton = new Button();
        Image backButtonImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/back_button.png")));
        ImageView backButtonImageView = new ImageView(backButtonImage);
        backButtonImageView.setFitWidth(50.0);
        backButtonImageView.setFitHeight(50.0);
        backButton.setGraphic(backButtonImageView);
        backButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        backButton.setOnAction(e -> start(primaryStage));

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/background_yellow.png")));
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        Text saveInstructions = new Text(
                "In order to create a player, choose one of the empty boxes below.\n"
                        + "If all boxes are occupied, proceed to Load Game in order to delete a player."
        );
        saveInstructions.setFill(Color.web("#4F4F4E"));
        saveInstructions.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 20));

        //layout dla zapisów
        HBox savesLayout = new HBox(50.0);
        savesLayout.setStyle("-fx-alignment: center;");

        //sloty 1-3
        for (int i = 1; i <= 3; i++) {
            StackPane slotPane;
            if (!"Empty Save".equals(loadGame.getSlotName(i))) {
                // Slot istniejący (zapisany) – brak możliwości edycji
                slotPane = CreateStaticSlot(loadGame, i);
            } else {
                // Slot pusty – możliwość edycji
                slotPane = CreateEditableSlot(primaryStage, i);
            }
            savesLayout.getChildren().add(slotPane);
        }

        StackPane root = new StackPane();
        root.setBackground(new Background(background));

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40, 20, 20, 20));
        mainLayout.getChildren().addAll(saveInstructions, savesLayout);
        root.getChildren().addAll(mainLayout, backButton);

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(10, 0, 0, 10));

        return new Scene(root, SCRX, SCRY);
    }

    /**
    * zapisany slot(statyczny) - brak mozliwosci klikniecia
    */
    private StackPane CreateStaticSlot(LoadGame loadGame, int slotNumber) {
        String slotName = loadGame.getSlotName(slotNumber);
        int avatarIndex = loadGame.getSlotAvatarIndex(slotNumber);

        // Tło slota
        ImageView slotBackground = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/empty_save.png")))
        );

        ImageView avatarImageView = new ImageView(loadGame.getAvatarImage(avatarIndex));
        avatarImageView.setFitWidth(105);
        avatarImageView.setFitHeight(125);

        Text slotNameText = new Text(slotName);
        slotNameText.setFill(Color.web("#4F4F4E"));
        slotNameText.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 14));

        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center;");
        layout.getChildren().addAll(slotNameText, avatarImageView);

        StackPane slotPane = new StackPane();
        slotPane.getChildren().addAll(slotBackground, layout);

        return slotPane;
    }

    /**
    *slot pusty - mozliwosc klikniecia
    */
    private StackPane CreateEditableSlot(Stage primaryStage, int slotNumber) {
        Button createButton = new Button();
        Image slotImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/empty_save.png")));
        ImageView slotImageView = new ImageView(slotImage);
        createButton.setGraphic(slotImageView);
        createButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

        createButton.setOnAction(e -> {
            Scene saveSlotScene = createSaveSlotScene(primaryStage, slotNumber);
            primaryStage.setScene(saveSlotScene);
            GlobalSettings(saveSlotScene);
        });

        return new StackPane(createButton);
    }

    /**
    * mozliwosc wyboru zapisanej postaci do rozgrywki
    */
    private Scene LoadSaveScene(Stage primaryStage) {
        LoadGame loadGame = new LoadGame();

        StackPane LoadSlot1 = CreateSlot(loadGame, 1, primaryStage);
        StackPane LoadSlot2 = CreateSlot(loadGame, 2, primaryStage);
        StackPane LoadSlot3 = CreateSlot(loadGame, 3, primaryStage);

        // Layout slotów
        HBox EmptySavesLayout = new HBox(50.0);
        EmptySavesLayout.getChildren().addAll(LoadSlot1, LoadSlot2, LoadSlot3);
        EmptySavesLayout.setStyle("-fx-alignment: center;");

        // Layout główny
        VBox centerLayout = new VBox(10.0);
        centerLayout.getChildren().addAll(EmptySavesLayout);
        centerLayout.setStyle("-fx-alignment: center;");

        // Przycisk Back
        Button backButton = new Button();
        Image backButtonImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/back_button.png")));
        ImageView backButtonView = new ImageView(backButtonImg);
        backButtonView.setFitWidth(50.0);
        backButtonView.setFitHeight(50.0);
        backButton.setGraphic(backButtonView);
        backButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        backButton.setOnAction(e -> start(primaryStage));

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/background_yellow.png")));
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        StackPane root = new StackPane();
        root.setBackground(new Background(background));
        root.getChildren().addAll(centerLayout, backButton);

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(10, 0, 0, 10));

        return new Scene(root, SCRX, SCRY);
    }

    /**
    * slot wczytania gry
    */
    private StackPane CreateSlot(LoadGame loadGame, int slotNumber, Stage primaryStage) {
        String slotName = loadGame.getSlotName(slotNumber);
        int avatarIndex = loadGame.getSlotAvatarIndex(slotNumber);

        ImageView slotBackground = new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/empty_save.png")))
        );

        ImageView avatarImageView = new ImageView();
        if (!"Empty Save".equals(slotName)) {
            avatarImageView.setImage(loadGame.getAvatarImage(avatarIndex));
        }
        avatarImageView.setFitWidth(105);
        avatarImageView.setFitHeight(125);

        Text loadInstructions = new Text(
                "In order to start the game, choose one of the created players below.\n"
                        + "If you would like to delete a player, click on the corresponding delete button"
        );
        loadInstructions.setFill(Color.web("#4F4F4E"));
        loadInstructions.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 20));

        Text slotNameText = new Text(slotName);
        slotNameText.setFill(Color.web("#4F4F4E"));
        slotNameText.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 14));

        //usuniecie postaci
        Button deleteButton = new Button("Delete");
        slotNameText.setFill(Color.web("#4F4F4E"));
        slotNameText.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 14));
        deleteButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        deleteButton.setOnAction(e -> {
            //okno potiwerdzenia czy chcemy usunac postac
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.getDialogPane().setStyle(
                    "-fx-background-color: #fff6e3; " +
                            "-fx-font-family: 'Bradley Hand'; " +
                            "-fx-font-size: 16px; " +
                            "-fx-text-fill: #4F4F4E;"
            );
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to delete this character?");

            //wyswietlenie okna
            Optional<ButtonType> result = confirmAlert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                loadGame.clearSlot(slotNumber);
                loadGame.saveData();
                Scene loadScene = LoadSaveScene(primaryStage);
                primaryStage.setScene(loadScene);
                GlobalSettings(loadScene);
            }
        });

        //przycisk startu gry (klikalny obszar calego slota)
        Button gameScreenButton = new Button();
        gameScreenButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        gameScreenButton.setMaxWidth(Double.MAX_VALUE);
        gameScreenButton.setMaxHeight(125);
        //gameScreenButton.setMaxHeight(Double.MAX_VALUE);

        gameScreenButton.setOnAction(e -> {
            if (!"Empty Save".equals(slotName)) {
                Scene gameScene = GameScreenScene(primaryStage, slotNumber);
                primaryStage.setScene(gameScene);
                GlobalSettings(gameScene);
            }
        });

        //ukrycie przycisku gameScreenButton, jeśli slot jest pusty
        if ("Empty Save".equals(slotName)) {
            gameScreenButton.setVisible(false);
        }

        VBox avatarAndTextLayout = new VBox(10);
        avatarAndTextLayout.setStyle("-fx-alignment: center;");

        if (!"Empty Save".equals(slotName)) {
            //slot zapisany – przycisk "Delete"
            avatarAndTextLayout.getChildren().addAll(slotNameText, avatarImageView, deleteButton);

        } else {
            //slot pusty – bez przycisku Delete
            avatarAndTextLayout.getChildren().addAll(slotNameText, avatarImageView);
        }

        StackPane slotLayout = new StackPane();
        slotLayout.getChildren().addAll(slotBackground, avatarAndTextLayout, gameScreenButton);

        return slotLayout;
    }

    /**
    * scena ekranu gry - poziom, postac
    */
    private Scene GameScreenScene(Stage primaryStage, int slotNumber) {
        LoadGame loadGame = new LoadGame();
        String slotName = loadGame.getSlotName(slotNumber);
        int avatarIndex = loadGame.getSlotAvatarIndex(slotNumber);
        int currentLevel = loadCurrentLevel(slotNumber); // wczytanie poziomu

        StackPane root = new StackPane();

        //tlo
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/background_yellow.png")));
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        root.setBackground(new Background(background));

        Image levelBarImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/level_bar.png")));
        ImageView levelBarImageView = new ImageView(levelBarImage);
        levelBarImageView.setFitWidth(458.75);
        levelBarImageView.setFitHeight(257.5);

        Image avatarImage = new Image(getClass().getResourceAsStream("/catvatars/" + getAvatarImagePath(avatarIndex)));
        ImageView avatarImageView = new ImageView(avatarImage);
        avatarImageView.setFitWidth(105);
        avatarImageView.setFitHeight(125);

        Text playerNameText = new Text(slotName);
        playerNameText.setFill(Color.web("#4F4F4E"));
        playerNameText.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 14));

        //ulozenie: levelBar + avatar + imię
        StackPane levelBarWithAvatarAndName = new StackPane(levelBarImageView);
        VBox avatarAndNameLayout = new VBox(10);
        avatarAndNameLayout.setStyle("-fx-alignment: center;");
        avatarAndNameLayout.getChildren().addAll(playerNameText, avatarImageView);

        levelBarWithAvatarAndName.getChildren().add(avatarAndNameLayout);
        StackPane.setAlignment(levelBarImageView, Pos.CENTER);
        StackPane.setAlignment(avatarAndNameLayout, Pos.BOTTOM_CENTER);

        Text levelText = new Text("Level: " + currentLevel);
        levelText.setFill(Color.web("#4F4F4E"));
        levelText.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 20));

        Button playButton = new Button();
        Image playButtonImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/play_button.png")));
        ImageView playButtonView = new ImageView(playButtonImg);
        playButtonView.setFitWidth(150.0);
        playButtonView.setFitHeight(50.0);
        playButton.setGraphic(playButtonView);
        playButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        playButton.setOnAction(e -> {
            // Przejście do konkretnej sceny z poziomem
            Scene levelScene = createLevelScene(primaryStage, avatarIndex, currentLevel, slotNumber);
            primaryStage.setScene(levelScene);
            GlobalSettings(levelScene);
        });

        VBox buttonLayout = new VBox(10);
        buttonLayout.setStyle("-fx-alignment: center;");
        buttonLayout.getChildren().add(playButton);

        //glowny layout ekranu gry
        VBox gameLayout = new VBox(20);
        gameLayout.setStyle("-fx-alignment: center; -fx-padding: 100;");
        gameLayout.getChildren().addAll(buttonLayout, levelText, levelBarWithAvatarAndName);

        Button backButton = new Button();
        Image backButtonImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/back_button.png")));
        ImageView backButtonImageView = new ImageView(backButtonImage);
        backButtonImageView.setFitWidth(50.0);
        backButtonImageView.setFitHeight(50.0);
        backButton.setGraphic(backButtonImageView);
        backButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        backButton.setOnAction(e -> {
            Scene loadSaveScene = LoadSaveScene(primaryStage);
            primaryStage.setScene(loadSaveScene);
        });

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(10, 0, 0, 10));

        root.getChildren().addAll(gameLayout, backButton);

        Scene gameScene = new Scene(root, SCRX, SCRY);
        return gameScene;
    }

    /**
    * zwraca nazwe pliku avatara
    */
    private String getAvatarImagePath(int avatarIndex) {
        String[] avatarPaths = {
                "black_cat.png",
                "grey_cat.png",
                "orange_cat.png",
                "white_cat.png"
        };
        return avatarPaths[avatarIndex];
    }

    /**
    * tworzenie zapisu(imie, avatar)
    */
    private Scene createSaveSlotScene(Stage primaryStage, int slotNumber) {
        // Przycisk Back
        Button backButton = new Button();
        Image backButtonImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/back_button.png")));
        ImageView backButtonView = new ImageView(backButtonImg);
        backButtonView.setFitWidth(50.0);
        backButtonView.setFitHeight(50.0);
        backButton.setGraphic(backButtonView);
        backButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        backButton.setOnAction(e -> start(primaryStage));

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/background_yellow.png")));
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        //pole do wpisania nazwy
        TextField nameField = new TextField();
        nameField.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 14));

        //podglad wybranego catvatara
        ImageView catvatar = new ImageView();
        catvatar.setFitWidth(175);
        catvatar.setFitHeight(205);

        int[] currentIndex = {0}; // index aktualnie wybranego avatara

        //aktualizacja obrazka w zalezności od indexu
        Runnable updateImage = () -> {
            String currentPath = imagePaths[currentIndex[0]];
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(currentPath)));
            catvatar.setImage(image);
        };
        updateImage.run();

        // Obsługa klawiszy LEFT/RIGHT
        EventHandler<KeyEvent> keyHandler = event -> {
            switch (event.getCode()) {
                case RIGHT:
                    currentIndex[0] = (currentIndex[0] + 1) % imagePaths.length;
                    updateImage.run();
                    break;
                case LEFT:
                    currentIndex[0] = (currentIndex[0] - 1 + imagePaths.length) % imagePaths.length;
                    updateImage.run();
                    break;
            }
        };

        // Przycisk SAVE
        Button saveButton = new Button();
        Image saveButtonImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/save_button.png")));
        ImageView saveButtonImageView = new ImageView(saveButtonImage);
        saveButtonImageView.setFitWidth(150.0);
        saveButtonImageView.setFitHeight(50.0);
        saveButton.setGraphic(saveButtonImageView);
        saveButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) {
                // Jeśli pole puste
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Name your cat");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a name.");
                alert.showAndWait();

                // Odświeżenie sceny
                Scene retryScene = createSaveSlotScene(primaryStage, slotNumber);
                primaryStage.setScene(retryScene);
                GlobalSettings(retryScene);
            } else {
                // Zapis do pliku
                LoadGame loadGame = new LoadGame();
                loadGame.setSlotName(slotNumber, name);
                loadGame.setSlotAvatarIndex(slotNumber, currentIndex[0]);
                loadGame.saveData();

                Scene createSaveScene = CreateSaveScene(primaryStage);
                primaryStage.setScene(createSaveScene);
                GlobalSettings(createSaveScene);
            }
        });

        // Layout w kreatorze zapisu
        VBox creatorWidgets = new VBox(50.0);
        creatorWidgets.getChildren().addAll(nameField, saveButton);
        creatorWidgets.setStyle("-fx-alignment: center;");

        HBox creatorLayout = new HBox(150.0);
        creatorLayout.getChildren().addAll(catvatar, creatorWidgets);
        creatorLayout.setStyle("-fx-alignment: center; -fx-padding: 100;");

        Text instructions = new Text("How to choose your character?\n" +
                "Choose you cat by using RIGHT and LEFT arrows on your keyboard\n" +
                "Name him in the box on your right\n" +
                "Save your character and start the game!");
        instructions.setFill(Color.web("#4F4F4E"));
        instructions.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 20));
        instructions.setTextAlignment(TextAlignment.CENTER);

        VBox mainLayout = new VBox(10.0);
        mainLayout.getChildren().addAll(instructions, creatorLayout);
        mainLayout.setStyle("-fx-alignment: center; -fx-padding: 10;");

        StackPane root = new StackPane();
        root.setBackground(new Background(background));
        root.getChildren().addAll(mainLayout, backButton);

        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(10, 0, 0, 10));

        Scene scene = new Scene(root, SCRX, SCRY);
        GlobalSettings(scene);

        // Obsługa klawiatury
        scene.setOnKeyPressed(keyHandler);

        //ustawienie focusu na layout zeby klawisze dzialaly
        Platform.runLater(mainLayout::requestFocus);
        root.setOnMouseClicked(ev -> mainLayout.requestFocus());

        return scene;
    }

    /**
    * scena poziomu
    * @param avatarIndex wybrany avatar
     * @param currentLevel poziom
     * @param selectedSlot slot zapisu
    */
    private Scene createLevelScene(Stage primaryStage, int avatarIndex, int currentLevel, int selectedSlot) {
        //zatrzymanie muzyki tla
        Music.getInstance().pauseBackgroundMusic();
        StackPane root = new StackPane();
        Canvas canvas = new Canvas(SCRX, SCRY);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        BaseLevel level;
        try {
            switch (currentLevel) {
                case 1:
                    level = new Level1(avatarIndex, currentLevel, selectedSlot);
                    break;
                case 2:
                    level = new Level2(avatarIndex, currentLevel, selectedSlot);
                    break;
                case 3:
                    level = new Level3(avatarIndex, currentLevel, selectedSlot);
                    break;
                default:
                    level = new Level1(avatarIndex, currentLevel, selectedSlot);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, SCRX, SCRY);

                level.update();
                level.draw(gc);

                // Sprawdzenie stanu gry
                if (!level.isLevelCompleted() && level.getCatventurer().collidesWithAny(level.getObstacles())) {
                    stop();
                    Platform.runLater(() -> primaryStage.setScene(createGameOverScene(primaryStage)));
                } else if (level.isLevelCompleted()) {
                    stop();
                    int gameTime = (int) ((System.currentTimeMillis() - level.startTime) / 100); // decysekundy
                    int finalScore = level.score;

                    primaryStage.setScene(
                            createGameCompletedScene(primaryStage, gameTime, finalScore, currentLevel, selectedSlot)
                    );
                }
            }
        };
        gameLoop.start();

        Scene scene = new Scene(root, SCRX, SCRY);
        GlobalSettings(scene);
        // Obsługa skoku (spacja)
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                level.getCatventurer().jump();
            }
        });

        return scene;
    }

    /**
    * scena game over
    */
    private Scene createGameOverScene(Stage primaryStage) {
        StackPane root = new StackPane();

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/background_blue.png")));
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 44));
        gameOverLabel.setTextFill(Color.web("#4F4F4E"));

        // Przycisk Restart
        Button restartButton = new Button();
        Image restartBtnImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/restart_button.png")));
        ImageView restartBtnView = new ImageView(restartBtnImg);
        restartBtnView.setFitWidth(150.0);
        restartBtnView.setFitHeight(50.0);
        restartButton.setGraphic(restartBtnView);
        restartButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        restartButton.setOnAction(e -> {
            Music.getInstance().pauseGameMusic();
            Music.getInstance().resumeBackgroundMusic();
            primaryStage.setScene(LoadSaveScene(primaryStage));
        });

        // Przycisk Menu
        Button backToMenuButton = new Button();
        Image backToMenuImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/return_to_menu_button.png")));
        ImageView backToMenuView = new ImageView(backToMenuImg);
        backToMenuView.setFitWidth(150.0);
        backToMenuView.setFitHeight(50.0);
        backToMenuButton.setGraphic(backToMenuView);
        backToMenuButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        backToMenuButton.setOnAction(e -> {
            Music.getInstance().pauseGameMusic();
            start(primaryStage);
        });

        VBox layout = new VBox(20, gameOverLabel, restartButton, backToMenuButton);
        layout.setAlignment(Pos.CENTER);

        root.setBackground(new Background(background));
        root.getChildren().add(layout);

        return new Scene(root, SCRX, SCRY);
    }

    /**
    * scena game completed
     * @param gameTime czas gry
     * @param score wynik
     * @param currentLevel poziom
     * @param selectedSlot slot zapisu
    */
    private Scene createGameCompletedScene(Stage primaryStage,
                                           int gameTime,
                                           int score,
                                           int currentLevel,
                                           int selectedSlot) {
        StackPane root = new StackPane();

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/background_blue.png")));
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        Label completedLabel = new Label("Level Completed!");
        completedLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 44));
        completedLabel.setTextFill(Color.web("#4F4F4E"));

        Label timeLabel = new Label("Game time: " + gameTime);
        timeLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 20));
        timeLabel.setTextFill(Color.web("#4F4F4E"));

        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/font/bradley_font.ttf"), 20));
        scoreLabel.setTextFill(Color.web("#4F4F4E"));

        // Przycisk Next Level
        Button nextLevelButton = new Button();
        Image nextLevelButtonImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/next_level_button.png")));
        ImageView nextLevelButtonView = new ImageView(nextLevelButtonImg);
        nextLevelButtonView.setFitWidth(150.0);
        nextLevelButtonView.setFitHeight(50.0);
        nextLevelButton.setGraphic(nextLevelButtonView);
        nextLevelButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

        nextLevelButton.setOnAction(e -> {
            int nextLevel = currentLevel + 1;
            if (nextLevel > 3) {
                nextLevel = 1; // reset do 1 po wyjściu poza 3
            }

            // Zapis nowego poziomu
            updateSaveFile(selectedSlot, nextLevel);

            // Załaduj scenę
            Scene levelScene = createLevelScene(primaryStage,
                    loadAvatarIndex(selectedSlot),
                    nextLevel,
                    selectedSlot);
            primaryStage.setScene(levelScene);
            GlobalSettings(levelScene);
        });

        // Przycisk powrotu do menu
        Button backToMenuButton = new Button();
        Image backToMenuImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/resources/return_to_menu_button.png")));
        ImageView backToMenuView = new ImageView(backToMenuImg);
        backToMenuView.setFitWidth(150.0);
        backToMenuView.setFitHeight(50.0);
        backToMenuButton.setGraphic(backToMenuView);
        backToMenuButton.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        backToMenuButton.setOnAction(e -> {
            Music.getInstance().pauseGameMusic();
            start(primaryStage);
        });

        VBox layout = new VBox(20, completedLabel, timeLabel, scoreLabel, nextLevelButton, backToMenuButton);
        layout.setAlignment(Pos.CENTER);

        root.setBackground(new Background(background));
        root.getChildren().add(layout);

        return new Scene(root, SCRX, SCRY);
    }

    /**
    * wczytanie indeksu avatara
    */
    private int loadAvatarIndex(int selectedSlot) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("saves.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String prefix = "slot_" + selectedSlot + "_";
        return Integer.parseInt(properties.getProperty(prefix + "avatar", "0"));
    }

    /**
    * wczytanie biezacego poziomu
     * @param selectedSlot slot zapisu
    */
    private int loadCurrentLevel(int selectedSlot) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("saves.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String prefix = "slot_" + selectedSlot + "_";
        return Integer.parseInt(properties.getProperty(prefix + "current_level", "1"));
    }

    /**
    *zapis nowego poziomu do pliku
     * @param selectedSlot  slot zapisu
     * @param currentLevel aktualny poziom
    */
    private void updateSaveFile(int selectedSlot, int currentLevel) {
        Properties properties = new Properties();

        // Wczytanie istniejącego pliku (o ile jest)
        try (FileInputStream fis = new FileInputStream("saves.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Nie znaleziono pliku saves.properties. Tworzenie nowego...");
        }

        String prefix = "slot_" + selectedSlot + "_";
        properties.setProperty(prefix + "current_level", String.valueOf(currentLevel));

        // Zapis do pliku
        try (FileOutputStream fos = new FileOutputStream("saves.properties")) {
            properties.store(fos, "Game Save");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------
    //metoda main
    // ------------------------------------------------------
    public static void main(String[] args) {
        launch(args);
    }
}
