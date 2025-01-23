package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

/** Handles player game scores. */
public class ScoreManager implements ScoreUpdater, Resettable <ScoreManager>{

    private int score;
    private FileHandle file;
    private Json json;
    private HighScores highScoreObject;

    /** Create instance of ScoreManager. */
    public ScoreManager() {
        setupScoreManager();
    }

    /**
     * Method to initialise and populate necessary fields for the ScoreManager.
     */
    private void setupScoreManager(){
        score = 0;
        loadHighScores();
    }


    private void loadHighScores(){
        json = new Json();
        file = Gdx.files.local("high_scores.json");

        if (file.exists()){
            highScoreObject = json.fromJson(HighScores.class, file.readString()); // Return parsed file as HighScores object
        } else {
            System.out.println("File not found.");
            highScoreObject = new HighScores();
            highScoreObject.highScores = new Array<>(); // Initialize the array to avoid null references
        }
    }

    public Array<Integer> getScores(){

        Array<Integer> scores = new Array<>();

        for (int i = 0; i < highScoreObject.highScores.size; i++){
            scores.add(highScoreObject.highScores.get(i).score);
            System.out.println(highScoreObject.highScores.get(i).score); // Get score from one specific user // 950
        }

        return scores;
    }

    /**
     * Get names for highscores
     */
    public Array<String> getNames(){
        Array<String> names = new Array<>();

        for (int i = 0; i < highScoreObject.highScores.size; i++){
            names.add(highScoreObject.highScores.get(i).name.toUpperCase());
        }
        return names;
    }

    /** Saves the given high score to a JSON file. */
    public void submitHighScore(String name, int score){
        // Create new Score Object with new player name and score and add to the HighScores object
        highScoreObject.highScores.add(new Score(name.toUpperCase(), score));
        System.out.println("Adding new score: " + name + ", " + score);

        // Sorts the highscores after highest score
        highScoreObject.highScores.sort((playerScore1, playerScore2) -> playerScore2.getScore() - playerScore1.getScore());

        // Limit amount of scores to 10
        highScoreObject.highScores.truncate(10);

        // Replace old content of json file with content of the highScoreObject
        file.writeString(json.toJson(highScoreObject), false); // Overwrite the JSON file
    }

    public boolean checkIfHighScore(int score){
        if (!highScoreObject.highScores.isEmpty()){
            return score > highScoreObject.highScores.peek().score;
        } else {
            return true;
        }

    }

    public boolean validateName(String name){
        name = name.trim(); // Trim any leading or trailing white space
        String regex = "^[A-Za-zÅÄÖåäö]+$"; // Create the regex string

        // Return true if it matches regex requirements and is less than 10 characters long
        return name.matches(regex) && name.length() <= 10;
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
    @Override
    public void reset(){
        score = 0; // Reset score
    }
}
