package app;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Klasa odpowiedzialna za obsluge muzyki
 */
public class Music {
    private static Music instance;
    private MediaPlayer backgroundMusic;
    private MediaPlayer gameMusic; //bedzie osobna muzyka do rozgrywki
    private boolean isPaused = false;

    private Music() {
        // prywatny konstruktor w celu wymuszenia uzycia getInstance()
    }

    public static Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    private MediaPlayer createMediaPlayer(String resourcePath) {
        String musicFile = getClass().getResource(resourcePath).toExternalForm();
        Media media = new Media(musicFile);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        return mediaPlayer;
    }

    //ustawienie muzyki tla
    public void playBackgroundMusic() {
        if (backgroundMusic == null) {
            backgroundMusic = createMediaPlayer("/resources/music/background_music.mp3");
        }
        if (!backgroundMusic.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            backgroundMusic.play();
            isPaused = false;
        }
    }

    // Zatrzymanie muzyki tła
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.getStatus() == MediaPlayer.Status.PLAYING) {
            backgroundMusic.pause();
            isPaused = true;
        }
    }

    // Wznowienie muzyki tła
    public void resumeBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.getStatus() == MediaPlayer.Status.PAUSED) {
            backgroundMusic.play();
        }
    }

    // Ustawienie muzyki gry
    public void playGameMusic() {
        if (gameMusic == null) {
            gameMusic = createMediaPlayer("/resources/music/game_music.mp3");
        }
        if (!gameMusic.getStatus().equals(MediaPlayer.Status.PLAYING)) {
            gameMusic.play();
            isPaused = false;
        }
    }

    // Zatrzymanie muzyki gry
    public void pauseGameMusic() {
        if (gameMusic != null && gameMusic.getStatus() == MediaPlayer.Status.PLAYING) {
            gameMusic.pause();
            isPaused = true;
        }
    }

    // Wznowienie muzyki gry
    public void resumeGameMusic() {
        if (gameMusic != null && gameMusic.getStatus() == MediaPlayer.Status.PAUSED) {
            gameMusic.play();
        }
    }

    // Zwalnianie zasobów
    public void release() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
        if (gameMusic != null) {
            gameMusic.stop();
            gameMusic.dispose();
            gameMusic = null;
        }
        instance = null;
    }

}
