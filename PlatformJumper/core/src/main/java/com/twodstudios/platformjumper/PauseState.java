package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twodstudios.platformjumper.screens.StartMenu;

public class PauseState {

    private final Main game;
    private final SharedAssets sharedAssets;

    private ImageButton resumeButton;
    private ImageButton mainMenuButton;

    private final Skin skin;
    private final Stage stage;
    private final Table table;

    private boolean uiCreated;
    private boolean paused;

    // Buttons sizes
    private final int buttonWidth = 265;
    private final int buttonHeight = 70;

    private FreeTypeFontGenerator generator;
    private BitmapFont font;
    private Label pauseLabel;
    private Label.LabelStyle labelStyle;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;


    public PauseState(Main game, SharedAssets sharedAssets) {
        this.game = game;
        this.sharedAssets = sharedAssets;

        this.uiCreated = false; // Flag to check if UI has been created
        this.paused = false;

        // Load the skin
        this.skin = new Skin(Gdx.files.internal("atlas/main_menu.json"));

        // Create stage and table
        this.stage = new Stage(new ScreenViewport());
        this.table = new Table();
        table.setFillParent(true);
        table.center().padTop(100);
        table.setSize(200, 400);

        this.uiCreated = false; // Flag to check if UI has been created
    }

    /** Create pause menu UI*/
    private void createPauseUI() {

        // Create freetype font generator to convert ttf font to bitmap font in run time
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Jersey10-Regular.ttf"));
        // Change fonts settings
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 150;
        // Convert font
        font = generator.generateFont(parameter);
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        pauseLabel = new Label("Paused", labelStyle);

        // Resume Button
        resumeButton = new ImageButton(skin, "play_button");
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = false; // Resumes the game
            }
        });
        // Main menu button
        mainMenuButton = new ImageButton(skin, "quit_button");
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StartMenu(game, sharedAssets));
            }
        });

        table.add(pauseLabel).padTop(25).center();
        table.row();

        // Add buttons to table
        table.add(resumeButton).size(buttonWidth, buttonHeight).pad(10);
        table.row();
        table.add(mainMenuButton).size(buttonWidth, buttonHeight).pad(10);
        table.padBottom(100);
        stage.addActor(table);
        this.uiCreated = true;
    }

    public void render() {
        // Create the pause UI if it hasn't been created already
        if (!uiCreated) {
            createPauseUI();
        }
        Gdx.input.setInputProcessor(stage);
        stage.act();
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    // Getter för pausstatus
    public boolean isPaused() {
        return paused;
    }

    // Toggle pausläget
    public void togglePause() {
        paused = !paused;
    }
}

