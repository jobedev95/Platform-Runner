package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twodstudios.platformjumper.EffectsManager;
import com.twodstudios.platformjumper.Main;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.twodstudios.platformjumper.SharedAssets;
import com.twodstudios.platformjumper.SoundManager;

public class StartMenuScreen implements Screen {
    private final Main game;
    private final SharedAssets sharedAssets;
    private final EffectsManager effectsManager;
    private final Stage stage;
    private final Table table;
    private final OrthographicCamera camera;
    private Viewport viewport;
    private final SoundManager soundManager;

    // Background variables
    private final Texture backgroundImage;
    private float bg1XPosition;
    private float bg2XPosition;
    private final float backgroundSpeed = 15f;
    private final float backgroundWidth = Main.WORLD_WIDTH;

    // Button sizes
    private final int buttonWidth = 265;
    private final int buttonHeigth = 70;


    public StartMenuScreen(Main game, SharedAssets sharedAssets, SoundManager soundManager) {
        this.game = game;
        this.sharedAssets = sharedAssets;
        this.soundManager = soundManager;
        this.effectsManager = new EffectsManager(game.spriteBatch);

        // Camera and ViewPort
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        this.camera.setToOrtho(false, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // Background
        this.backgroundImage = new Texture("menu_background.png");
        this.bg1XPosition = 0;
        this.bg2XPosition = backgroundWidth;

        // Stage and Table for the main menu
        this.stage = new Stage(viewport);
        this.table = new Table();
        table.setFillParent(true);
        table.center().padTop(100);
        table.setSize(200, 400);

        // Set up the main menu
        createMainMenu();
    }

    @Override
    public void render(float delta) {
        sharedAssets.updateMainLogoAnimationTime(delta);

        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined); // Link spriteBatch to camera
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 0f); // Clear screen with black color
        game.spriteBatch.begin();

        // Draw moving background
        drawBackground(delta);

        // Draw main logo animation
        sharedAssets.drawLogoAnimation(687, 150, 240, false);

        // Draw menu particle effects sparkles
        effectsManager.drawMainMenuParticles(delta);

        game.spriteBatch.end();

        // Draw the stage
        stage.act(delta);
        stage.draw();
    }

    //** Draw a moving background. */
    private void drawBackground(float deltaTime) {

        // Adjust x-position of both background instances
        bg1XPosition -= backgroundSpeed * deltaTime;
        bg2XPosition -= backgroundSpeed * deltaTime;

        // When background 1 is out of view, move it to the right edge of background 2
        if (bg1XPosition + backgroundImage.getWidth() <= 0) {
            bg1XPosition = bg2XPosition + backgroundWidth;
        }

        // When background 2 is out of view, move it to the right edge of background 1
        if (bg2XPosition + backgroundImage.getWidth() <= 0) {
            bg2XPosition = bg1XPosition + backgroundWidth;
        }

        // Draw both background instances
        game.spriteBatch.draw(backgroundImage, bg1XPosition, 0, backgroundWidth, Main.WORLD_HEIGHT);
        game.spriteBatch.draw(backgroundImage, bg2XPosition, 0, backgroundWidth, Main.WORLD_HEIGHT);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // If menu music is not already playing, start the playback
        if (!soundManager.isMenuMusicPlaying()){
            soundManager.menuMusic();
        }
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
        stage.dispose();
        backgroundImage.dispose();
        soundManager.dispose();
        effectsManager.dispose();
        sharedAssets.dispose();
    }


    //** Setup all the buttons for the main menu. */
    private void createMainMenu() {

        // Load the skin
        Skin skin = new Skin(Gdx.files.internal("atlas/main_menu.json"));

        // Create Start button
        ImageButton startButton = new ImageButton(skin, "play_button");
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game, StartMenuScreen.this)); // Switch to game screen
                soundManager.stopMenuMusic();
            }
        });

        // Add Start Button to table
        table.add(startButton).size(buttonWidth, buttonHeigth).pad(10);
        table.row();

        // Create High Score button
        ImageButton highscoreButton = new ImageButton(skin, "high_score_button");
        highscoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HighscoreScreen(game, StartMenuScreen.this, effectsManager)); // Switch to high score screen
            }
        });

        // Add High Score button to table
        table.add(highscoreButton).size(buttonWidth, buttonHeigth).pad(10);
        table.row();

        // Create Exit button
        ImageButton quitButton = new ImageButton(skin, "quit_button");
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Exit game
            }
        });

        // Add Exit button to table
        table.add(quitButton).size(buttonWidth, buttonHeigth).pad(10);

        // Add table to the stage
        stage.addActor(table);
    }
}
