package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.twodstudios.platformjumper.screens.StartMenu;

public class PauseState {

    private Main game;
    private SharedAssets sharedAssets;

    private ImageButton resumeButton;
    private ImageButton mainMenuButton;

    private Skin skin;
    private Stage stage;
    private Table table;

    private boolean uiCreated;
    private boolean paused;

    // Buttons sizes
    private int buttonWidth = 265;
    private int buttonHeight = 70;

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

    private void createPauseUI(){
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

        // Add buttons to table
        table.add(resumeButton).size(buttonWidth, buttonHeight).pad(10);
        table.row();
        table.add(mainMenuButton).size(buttonWidth, buttonHeight).pad(10);
        stage.addActor(table);
        this.uiCreated = true;
    }

    public void render(){
        // Create the game over UI if it hasn't been created already
        if (!uiCreated){
            createPauseUI();
        }
        Gdx.input.setInputProcessor(stage);
        stage.act();
        stage.draw();
    }
}
