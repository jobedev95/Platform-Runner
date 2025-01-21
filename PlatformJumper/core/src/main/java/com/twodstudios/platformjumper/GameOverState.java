package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameOverState {

    private ResetListener resetListener;
    private ScoreManager scoreManager;
    private Skin skin;
    private Stage stage;
    private Table table;
    private Label gameOverLabel;
    private Label highscoreLabel;
    private float animationTime = 0;
    private int score;
    private TextField textField;
    private ImageButton okButton;
    private boolean uiCreated;

    public GameOverState(ResetListener resetListener, ScoreManager scoreManager) {
        this.resetListener = resetListener;
        this.scoreManager = scoreManager;
        this.table = new Table();
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal("skins/game_over_skin.json"));
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        this.uiCreated = false;
    }

    private void updateScore(){
        score = scoreManager.getScore();
    }


    /** Create Game Over UI. Include high score and text input field if high score is reached. */
    private void createGameOverUI(){
        // Add Game Over Label
        this.gameOverLabel = new Label("Game Over", skin);
        table.add(this.gameOverLabel);

        table.row(); // Add a row

        if (scoreManager.checkIfHighScore(score)){
            newHighScoreUI();
            System.out.println("HIGH SCORE UI CREATED");
        }

        uiCreated = true;
    }

    private void newHighScoreUI(){
        // Add High Score label
        this.highscoreLabel = new Label("New High Score: " + score, skin);
        table.add(this.highscoreLabel);
        table.row(); // Add a row
        createTextField(table);
        createOkButton(table);
    }


  
    
    private void clearHighScoreUI(){
        highscoreLabel.remove();
        textField.remove();
        okButton.remove();
    }


    private void createOkButton(Table table){

        okButton = new ImageButton(skin);
        table.add(okButton)
            .size(50, 50); // set size of pause button

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                confirmHighScore();
            }
        });

        
    }

    private void confirmHighScore(){
        String name = textField.getText(); // Get textfield input (name)

        // Validate name input using regex (must be A-Ö and cannot include any special characters)
        boolean isNameValid = scoreManager.validateName(name.trim());

        // If name is valid save the score
        if (isNameValid){
            scoreManager.saveScore(name, score);
            resetListener.resetGame();
        } else {
            setNameInvalidMessage(); // Inform user of faulty input if that's the case
        }
    }

    private void createTextField(Table table){
        textField = new TextField("", skin);
        textField.setMessageText("ENTER NAME");
        table.add(textField).size(300, 80);

        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    textField.setText(""); // Clear the text when focused
                }
            }
        });

        // Listener for Enter key
        textField.addListener(new InputListener() {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.ENTER) {
                confirmHighScore();
                return true; // Event handled
            }
            return false; // Event not handled
        }
    });
    }

    public void setNameInvalidMessage(){
        stage.unfocus(textField);
        textField.setText("INVALID!");
    }


    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }

    public void render(){
        updateScore();
        
        if (!uiCreated){
            createGameOverUI();
        }
        
        stage.act();
        stage.draw();
    }

    public void reset(){
        table.clear();
        this.uiCreated = false;
        score = 0;
    }


}
