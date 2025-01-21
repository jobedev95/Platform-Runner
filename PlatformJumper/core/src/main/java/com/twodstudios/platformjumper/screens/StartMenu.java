package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twodstudios.platformjumper.Main;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class StartMenu implements Screen {
    private Main game;
    private Stage stage;
    private OrthographicCamera camera;
    private Viewport viewport;

    // Bakgrund
    private final Texture backgroundImage;
    private float bg1XPosition;
    private float bg2XPosition;
    private float backgroundSpeed = 5f;

    // Flagga för att kontrollera om knapparna ska vara aktiva
    private boolean isMenuActive = true;

    public StartMenu(Main game) {
        this.game = game;

        // Kamera och vyport
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        this.camera.setToOrtho(false, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // Bakgrund
        this.backgroundImage = new Texture("MenuBG.png");
        this.bg1XPosition = 0;
        this.bg2XPosition = backgroundImage.getWidth();

        // Stage för knappar
        this.stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // Skapa knappar
        createButtons();
    }

    private void createButtons() {
        Skin skin = new Skin(Gdx.files.internal("atlas/buttons.json"));


        // Start-knapp
        ImageButton startButton = new ImageButton(skin, "Playbutton");
        startButton.setPosition(400, 300);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isMenuActive) {
                    game.setScreen(new PlayScreen(game)); // Växla till spelet
                    isMenuActive = false;
                }
            }
        });
        stage.addActor(startButton);

        // Highscore-knapp
        ImageButton highscoreButton = new ImageButton(skin, "Highscorebutton");
        highscoreButton.setPosition(400, 200);
        highscoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isMenuActive) {
                    //game.setScreen(new HighscoreScreen()); // avkommentera när highscore är med
                    isMenuActive = false;
                }

            }
        });
        stage.addActor(highscoreButton);

        // Exit-knapp
        ImageButton quitButton = new ImageButton(skin, "Quitbutton");
        quitButton.setPosition(400, 100);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Stäng spelet
            }
        });
        stage.addActor(quitButton);
    }

    @Override
    public void render(float delta) {
        // Uppdatera bakgrund
        drawBackground(delta);

        // Rita knappar om menyn är aktiv
        if (isMenuActive) {
            stage.act(delta);
            stage.draw();
        }
    }

    private void drawBackground(float deltaTime) {
        bg1XPosition -= backgroundSpeed * deltaTime;
        bg2XPosition -= backgroundSpeed * deltaTime;

        if (bg1XPosition + backgroundImage.getWidth() <= 0) {
            bg1XPosition = bg2XPosition + backgroundImage.getWidth();
        }
        if (bg2XPosition + backgroundImage.getWidth() <= 0) {
            bg2XPosition = bg1XPosition + backgroundImage.getWidth();
        }

        game.spriteBatch.begin();
        game.spriteBatch.draw(backgroundImage, bg1XPosition, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.spriteBatch.draw(backgroundImage, bg2XPosition, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        backgroundImage.dispose();
    }
}
