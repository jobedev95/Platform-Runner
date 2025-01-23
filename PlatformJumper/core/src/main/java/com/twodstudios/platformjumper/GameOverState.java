package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingLabel;

/** Creates and handles the game over UI. */
public class GameOverState implements Resettable <GameOverState>{

    private final GameOverListener gameOverListener;
    private final ScoreManager scoreManager;

    private final Skin skin;
    private final Stage stage;
    private final Table table;

    // All stage and table elements
    private Texture gameoverBackground;
    private Image uiBackground;
    private Label gameOverLabel;
    private Label highscoreTitle, top1Name, top1Score, top2Name, top2Score, top3Name, top3Score;
    private Table highScoreTable;
    private TypingLabel animatedHighScoreLabel;
    private TextField textField;
    private ImageButton submitButton;
    private ImageButton playButton;

    private int score;
    private boolean uiCreated; // Flag to check if UI has been created

    /**
     * Creates a GameOverState handler.
     * @param gameOverListener A class which can reset the game when score has been submitted.
     * @param scoreManager For handling retrieval of score and validation of name submission.
     */
    public GameOverState(GameOverListener gameOverListener, ScoreManager scoreManager) {
        this.gameOverListener = gameOverListener;
        this.scoreManager = scoreManager;


        // Load the skin
        this.skin = new Skin(Gdx.files.internal("skins/game_over_skin.json"));

        // Create stage and table
        this.stage = new Stage(new ScreenViewport());
        this.table = new Table();
        table.setFillParent(true);
        table.center().pad(10, 300, 10, 300);
        Gdx.input.setInputProcessor(stage);

        this.uiCreated = false; // Flag to check if UI has been created
    }

    /** Create the full Game Over UI. Include background, high score and text input field if high score is reached. */
    private void createGameOverUI(){

        // If player has reached a high score the high core UI will be created
        if (scoreManager.checkIfHighScore(score)){
            gameOverHighScoreUI();
        } else { // If no high score was reached, simply create a "Game over" label
            // Add Game Over Label
            this.gameOverLabel = new Label("GAME OVER", skin, "biggest");
            table.add(this.gameOverLabel).colspan(3).center();
            table.row();
        }

        // Add the completed table to the stage
        stage.addActor(table);

        // Flag that informs the render method that the UI has been created
        uiCreated = true;
    }

    /** Create the game over background.
     * @param width Width of background.
     * @param height Height of background.
     * */
    private void createGameOverBackground(int width, int height) {

        // Load the background image
        this.gameoverBackground = new Texture(Gdx.files.internal("gameover_background.png"));

        // Create an Image object from the texture (so it can be added to a Stage)
        uiBackground = new Image(gameoverBackground);

        // Set size, position and color of background
        uiBackground.setSize(width, height);
        uiBackground.setPosition((Main.WORLD_WIDTH - uiBackground.getWidth()) / 2, (Main.WORLD_HEIGHT - uiBackground.getHeight()) / 2);
        uiBackground.setColor(1, 1, 1, 0.85f);

        // Add Image to the stage
        stage.addActor(uiBackground);
    }

    /** Create a Game Over UI which includes top 3 high scores, text input field and
     * a submit button to allow player to submit their high score. */
    private void gameOverHighScoreUI(){

        // Create background
        createGameOverBackground(700, 500);

        // Create Game Over Label
        this.gameOverLabel = new Label("GAME OVER", skin, "biggest");
        table.add(this.gameOverLabel).colspan(3).center();
        table.row();

        // Create the "HIGHSCORES" title
        this.highscoreTitle = new Label("HIGHSCORES", skin);
        table.add(this.highscoreTitle)
            .colspan(3)
            .center()
            .padBottom(10);
        table.row();

        createHighScoreTable();

        // Create Font family because TypingLabel does not support multiple fonts inside skins
        Font.FontFamily fontFamily = new Font.FontFamily(skin);

        // Add animated "NEW HIGH SCORE" label
        this.animatedHighScoreLabel = new TypingLabel("[%100][@Rationale-Regular4]{RAINBOW=1.0;1.0;0.4;0.5}{SHRINK=4;1.1;false}{WAVE=0.4;0.9;1.0}NEW HIGH SCORE: {ENDWAVE}{ENDSHRINK}[%]" + score, fontFamily.connected[0]);
        table.add(this.animatedHighScoreLabel)
            .colspan(3)
            .center()
            .padBottom(10);
        table.row();

        // Create the text input field
        createTextField(table);
        table.row();

        // Create the submit button
        createSubmitButton(table);
    }

    /** Create the high score table which includes the top 3 high scores.*/
    private void createHighScoreTable(){
        Array<String> highScoreNames = scoreManager.getNames();
        Array<Integer> currentHighScores = scoreManager.getScores();
        highScoreTable = new Table();
        for (int i = 0; i < 3; i++) {
            Label nameLabel = new Label((i + 1) + ". " + highScoreNames.get(i), skin, "medium");
            Label dashLabel = new Label("  ---------------  ", skin, "medium");
            Label scoreLabel = new Label("" + currentHighScores.get(i), skin, "medium");

            highScoreTable.add(nameLabel).left().expandX().fillX();
            highScoreTable.add(dashLabel).expandX().fillX();
            highScoreTable.add(scoreLabel).right().expandX().fillX();
            highScoreTable.row();
        }
        table.add(highScoreTable).colspan(3);
        table.row();
    }

    /** Create Submit button.
     * @param table The Table object that the button will be added to.
     * */
    private void createSubmitButton(Table table){

        submitButton = new ImageButton(skin);
        table.add(submitButton)
            .size(150, 50);

        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                submitHighScore();
            }
        });

        playButton = new ImageButton(skin, "play_button");
        table.add(playButton)
            .size(150, 50);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameOverListener.resetGame();
            }
        });


    }

    /** Sends name for validation and submits the high score after validation. */
    private void submitHighScore(){
        String name = textField.getText(); // Get text field input (name)

        // Validate name input using regex (must be A-Ö and cannot include any special characters)
        boolean isNameValid = scoreManager.validateName(name);

        // If name is valid save the score
        if (isNameValid){
            scoreManager.submitHighScore(name, score); // Submit highscore
            gameOverListener.resetGame(); // Call main game to reset after the score has been submitted
        } else {
            setNameInvalidMessage(); // Inform user of faulty input if that's the case
        }
    }

    /** Creates the text input field for the player to be able to enter their name.
     * @param table The Table object that the TextField will be added to.
     * */
    private void createTextField(Table table){
        textField = new TextField("", skin);
        textField.setMessageText("ENTER NAME");
        textField.getStyle().cursor.setTopHeight(3f);
        table.add(textField)
            .colspan(3)
            .fillX()
            .size(250, 70);

        textField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    textField.setColor(1, 1, 1, 1);
                    textField.setText(""); // Clear the text when focused
                }
            }
        });

        // Listener for Enter key
        textField.addListener(new InputListener() {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.ENTER) {
                submitHighScore();
                return true; // Event handled
            }
            return false; // Event not handled
        }
    });
    }

    public Stage getStage() {
        return stage;
    }

    /** Sets the text input field to show "Invalid" with a red background. */
    public void setNameInvalidMessage(){
        stage.unfocus(textField); // Removes the focus from the text field
        textField.setText("INVALID!");

        // Set text field background color to red with 25% lower opacity
        textField.setColor(1, 0, 0, 0.75f);
    }

    /** Update the score for this class in preparation of being shown in the game over screen. */
    private void updateScore(){
        score = scoreManager.getScore();
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }

    public void render(float deltaTime){
        updateScore();

        // Create the game over UI if it hasn't been created already
        if (!uiCreated){
            createGameOverUI();
        }

        Gdx.input.setInputProcessor(stage);
        stage.act(deltaTime);
        stage.draw();
    }

    @Override
    public void reset(){
        if (gameoverBackground != null){
            gameoverBackground.dispose();
        }
        stage.clear();
        table.clear();
        this.uiCreated = false;
        score = 0;
    }
}
