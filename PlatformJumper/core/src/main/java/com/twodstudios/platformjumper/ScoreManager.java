package com.twodstudios.platformjumper;

/** Handles player game scores. */
public class ScoreManager implements ScoreUpdater{

    private int score;

    /** Create instance of ScoreManager. */
    public ScoreManager() {
        setupScoreManager();
    }

    /**
     * Method to initialise and populate necessary fields for the ScoreManager.
     */
    private void setupScoreManager(){
        score = 0;
    }

    /** Increase player score with 1. */
    @Override
    public void increaseScore() {
        this.score++;
    }

    /** Get current player score. */
    public int getScore() {
        return score;
    }

    /** Reset player score. */
    public void reset(){
        score = 0; // Reset score
    }
}
