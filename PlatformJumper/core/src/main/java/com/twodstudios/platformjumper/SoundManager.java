package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Class to manage any audio files for the game.
 */
public class SoundManager {

    private Sound gameOverSound; // Sound effect for game over
    private boolean isGameOverSoundPlayed = false; // Flag to check if game over sound has been played
    private Music backgroundMusic; // Background Music
    private Music menuMusic; // Menu Music
    private Sound coinPickupSound; // Coin pickup sound
    /** Create instance of SoundManager. */
    public SoundManager() {
        setupSoundManager();
    }

    /**
     * Method to initialise and populate necessary fields for the SoundManager.
     */
    private void setupSoundManager(){
        // Loading gameover sound && Background/Menu music
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver.wav"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/backgroundMusic.ogg"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/menuMusic.ogg"));
        coinPickupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/coinPickup.wav"));
    }

    /** Plays game over sound when player dies. */
    public void playGameOverSound(){
        // Play game over sound only once
        if(!isGameOverSoundPlayed) {
            gameOverSound.play(); // Play game over sound
            isGameOverSoundPlayed = true; // Mark that sound has been played
        }
    }

    /** Set status of gameOverSoundPlayed.
    * @param gameOverSoundPlayed Set played-status with true or false.
    */
    public void setGameOverSoundPlayed(boolean gameOverSoundPlayed) {
        isGameOverSoundPlayed = gameOverSoundPlayed;
    }

    /** Plays backgroundMusic while playing */
    public void backgroundMusic(){
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }
    // stop background music
    public void stopBackgroundMusic(){
        backgroundMusic.stop();
    }
    /** Plays menuMusic while on start screen */
    public void menuMusic(){
        menuMusic.setLooping(true);
        menuMusic.play();
    }
    // stop menuMusic
    public void stopMenuMusic(){
        menuMusic.stop();
    }
    /** Plays coinPickup sound when coin collected */
    public void coinPickupSound(){
        coinPickupSound.play();
    }
    /** Dispose of SoundManager assets. */
    public void dispose(){
        gameOverSound.dispose();
        backgroundMusic.dispose();
        menuMusic.dispose();
        coinPickupSound.dispose();
    }



}
