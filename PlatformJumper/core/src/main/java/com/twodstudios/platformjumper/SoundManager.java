package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Class to manage any audio files for the game.
 */
public class SoundManager {

    private Sound gameOverSound; // Sound effect for game over
    private boolean isGameOverSoundPlayed = false; // Flag to check if game over sound has been played
    
    /** Create instance of SoundManager. */
    public SoundManager() {
        setupSoundManager();
    }
    
    /**
     * Method to initialise and populate necessary fields for the SoundManager.
     */
    private void setupSoundManager(){
        // Loading gameover sound
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("losetrumpet.wav"));
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
    
    /** Dispose of SoundManager assets. */
    public void dispose(){
        gameOverSound.dispose();
    }
}
