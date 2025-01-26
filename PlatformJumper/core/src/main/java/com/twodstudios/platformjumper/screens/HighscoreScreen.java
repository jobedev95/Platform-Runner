package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twodstudios.platformjumper.*;

public class HighscoreScreen implements Screen {

    private final Main game;
    private final StartMenuScreen startMenuScreen;
    private final EffectsManager effectsManager;
    private final ScoreManager scoreManager;


    // Stage, table and ViewPort
    private final Stage stage;
    private final Table table;
    private final Skin skin;
    private final Viewport viewport;

    // Background variables
    private final Texture backgroundImage;
    private float bg1XPosition;
    private float bg2XPosition;
    private final float backgroundSpeed = 15f;
    private final float backgroundWidth = Main.WORLD_WIDTH;
    private Image uiBackground;

    private Array<String> highscoreNames;
    private Array<Integer> highscorePoints;

    public HighscoreScreen(Main game, StartMenuScreen startMenuScreen, EffectsManager effectsManager) {
        this.game = game;
        this.startMenuScreen = startMenuScreen;
        this.effectsManager = effectsManager;

        this.scoreManager = new ScoreManager();

        // Get current high scores
        highscoreNames = scoreManager.getNames(10);
        highscorePoints = scoreManager.getScores(10);

        // Skin för Labels och knappar
        this.skin = new Skin(Gdx.files.internal("high_score_skin.json")); // Ange rätt sökväg till din skin-fil

        // Camera and ViewPort
        OrthographicCamera camera = new OrthographicCamera();
        this.viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        camera.setToOrtho(false, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // Background
        this.backgroundImage = new Texture("menu_background.png");
        this.bg1XPosition = 0;
        this.bg2XPosition = backgroundWidth;

        // Stage and table
        this.stage = new Stage(viewport);
        this.table = new Table();
        table.setFillParent(true);
        table.top().padTop(10);

        // Create high score table and menu buttons
        createUIBackground(600, 510);
        createHighscoreTable();
        createButtons();

        // Add table to stage
        stage.addActor(table);
    }

    private void createHighscoreTable() {

        // Create labels
        Label titleLabel = new Label("HIGH SCORES", skin, "extra_large");
        Label nameHeader = new Label("Name", skin, "medium");
        Label scoreHeader = new Label("Score", skin, "medium");

        // Add labels to table
        table.add(titleLabel).colspan(2).padBottom(5).center();
        table.row();
        table.add(nameHeader).padRight(50).left();
        table.add(scoreHeader).padLeft(50).right();
        table.row();

        // Add the high scores
        for (int i = 0; i < 10; i++) {
            String nameText;
            String scoreText;

            // Get name and highscore
            nameText = (i + 1) + ": " + highscoreNames.get(i); // Add rank before the name
            scoreText = highscorePoints.get(i).toString();

            // Labels for name and score
            Label nameLabel = new Label(nameText, skin, "medium");
            Label scoreLabel = new Label(scoreText, skin, "medium");

            // Add labels to the table
            table.add(nameLabel).padRight(50).left();
            table.add(scoreLabel).padLeft(50).right();
            table.row();
        }
    }


    //** Create the high score menu buttons. */
    private void createButtons() {
        Skin buttonSkin = new Skin(Gdx.files.internal("atlas/main_menu.json")); // Ange rätt sökväg

        // Create Back button
        ImageButton backButton = new ImageButton(buttonSkin, "back_button");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(startMenuScreen); // Go back to main menu
            }
        });

        // Add Back button to table
        table.row().padTop(10);
        table.add(backButton).colspan(2).size(265, 70).center();
    }

    //** Draw moving background. */
    private void drawBackground(float deltaTime) {
        bg1XPosition -= backgroundSpeed * deltaTime;
        bg2XPosition -= backgroundSpeed * deltaTime;

        if (bg1XPosition + backgroundImage.getWidth() <= 0) {
            bg1XPosition = bg2XPosition + backgroundWidth;
        }
        if (bg2XPosition + backgroundImage.getWidth() <= 0) {
            bg2XPosition = bg1XPosition + backgroundWidth;
        }

        game.spriteBatch.draw(backgroundImage, bg1XPosition, 0, backgroundWidth, Gdx.graphics.getHeight());
        game.spriteBatch.draw(backgroundImage, bg2XPosition, 0, backgroundWidth, Gdx.graphics.getHeight());
    }

    /** Create the game over background.
     * @param width Width of background.
     * @param height Height of background.
     * */
    private void createUIBackground(int width, int height) {

        // Load the UI background image
        Texture uiBackgroundTexture = new Texture(Gdx.files.internal("ui_background.png"));

        // Create an Image object from the texture (so it can be added to a Stage)
        uiBackground = new Image(uiBackgroundTexture);

        // Set size, position and color of UI background
        uiBackground.setSize(width, height);
        uiBackground.setPosition((Main.WORLD_WIDTH - uiBackground.getWidth()) / 2, ((Main.WORLD_HEIGHT - uiBackground.getHeight()) / 2) + 15);
        uiBackground.setColor(1, 1, 1, 0.85f);

        // Add Image to the stage
        stage.addActor(uiBackground);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        game.spriteBatch.begin();

        // Draw moving background
        drawBackground(delta);

        // Draw menu particle effects sparkles
        effectsManager.drawMainMenuParticles(delta);

        game.spriteBatch.end();

        // Draw high score table and menu buttons
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        backgroundImage.dispose();
        stage.dispose();
        effectsManager.dispose();
    }
}
