package com.example.catventure;

import android.content.Context;
import android.media.MediaPlayer;

/* klasa typu singleton - ma tylko jedną instancje obiektu w całej aplikacji
   dzięki temu muzyka będzie się odtwarzać płynnie między aktywnościami - nie jest inicjalizowana
   ponownie podczas przechodzenia między aktywnościami */

public class Music {
    private static Music instance;
    private MediaPlayer backgroundMusic;
    private MediaPlayer gameMusic; // osobna muzyka do rozgrywki
    private boolean isPaused = false;

    private Music() {
        // prywatny konstruktor w celu wymuszenia użycia getInstance()
    }

    public static Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    // Ustawienie muzyki tła
    public void playBackgroundMusic(Context context) {
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(context, R.raw.background_music);
            backgroundMusic.setLooping(true); // powtarzanie muzyki
            backgroundMusic.start();
        } else if (isPaused) {
            backgroundMusic.start();
            isPaused = false;
        }
    }

    // Odtwarzanie muzyki gry
    public void playGameMusic(Context context) {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause(); // Wstrzymaj muzykę tła
        }

        if (gameMusic == null) {
            gameMusic = MediaPlayer.create(context, R.raw.game_music);
            gameMusic.setLooping(true);
            gameMusic.start();
        } else if (isPaused) {
            gameMusic.start();
            isPaused = false;
        }
    }

    // Pauzowanie aktualnej muzyki
    public void pauseMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
            isPaused = true;
        }
        if (gameMusic != null && gameMusic.isPlaying()) {
            gameMusic.pause();
            isPaused = true;
        }
    }

    // Wznowienie muzyki tła po pauzie
    public void resumeBackgroundMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    // Wznowienie muzyki gry po pauzie
    public void resumeGameMusic() {
        if (gameMusic != null && !gameMusic.isPlaying()) {
            gameMusic.start();
        }
    }

    // Zatrzymanie muzyki gry
    public void stopGameMusic() {
        if (gameMusic != null) {
            gameMusic.stop();
            gameMusic.release();
            gameMusic = null;
        }
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start(); // Wznowienie muzyki tła
        }
    }

    // Zwolnienie zasobów przy zamknięciu aplikacji
    public void release() {
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
        if (gameMusic != null) {
            gameMusic.release();
            gameMusic = null;
        }
        instance = null;
    }
}
