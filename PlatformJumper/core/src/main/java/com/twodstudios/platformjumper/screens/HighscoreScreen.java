package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twodstudios.platformjumper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HighscoreScreen implements Screen {

    private final Main game;
    private final EffectsManager effectsManager;
    private ScoreManager scoreManager;
    private  StartMenuScreen startMenuScreen;

    // Stage, table and viewport
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

    private final List<HighscoreEntry> highscores;

    public static class HighscoreEntry {
        public String name;
        public int score;
    }

    public HighscoreScreen(Main game, StartMenuScreen startMenuScreen) {
        this.game = game;
        this.scoreManager = new ScoreManager();
        this.startMenuScreen = startMenuScreen;

        Array<String> highscoreNames = scoreManager.getNames(10);
        Array<Integer> highscorePoints = scoreManager.getScores(10);

        this.highscores = readHighscores();
        sortHighscores(highscores);

        // Skin för Labels och knappar
        this.skin = new Skin(Gdx.files.internal("high_score_skin.json")); // Ange rätt sökväg till din skin-fil

        // Kamera och vyport
        OrthographicCamera camera = new OrthographicCamera();
        this.viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        camera.setToOrtho(false, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);

        // Bakgrund
        this.backgroundImage = new Texture("menu_background.png");
        this.bg1XPosition = 0;
        this.bg2XPosition = backgroundWidth;

        // Stage och table
        this.stage = new Stage(viewport);
        this.table = new Table();
        table.setFillParent(true);
        table.top().padTop(10); // Flytta ner tabellen

        // Lägg till Highscore-tabell och knappar
        createHighscoreTable();
        createButtons();

        stage.addActor(table);

        // Effects och ljud
        this.effectsManager = new EffectsManager(game.spriteBatch);
    }

    private void createHighscoreTable() {
        // Create labels
        Label titleLabel = new Label("HIGH SCORES", skin, "extra_large");
        Label nameHeader = new Label("Name", skin, "large");
        Label scoreHeader = new Label("Score", skin, "large");

        // Add labels to table
        table.add(titleLabel).colspan(2).padBottom(20).center();
        table.row();
        table.add(nameHeader).padRight(50).left();
        table.add(scoreHeader).padLeft(50).right();
        table.row();

        // Add the highscores
        for (int i = 0; i < 10; i++) {
            String nameText;
            String scoreText;

            if (i < highscores.size()) {
                HighscoreEntry score = highscores.get(i);
                nameText = (i + 1) + ": " + score.name; // Add rank before the name
                scoreText = String.valueOf(score.score);
            } else {
                nameText = (i + 1) + ": -----"; // Rank med plats för tomma rader
                scoreText = "0";
            }

            // Labels för namn och poäng
            Label nameLabel = new Label(nameText, skin, "medium");
            Label scoreLabel = new Label(scoreText, skin, "medium");

            // Lägg till i tabellen
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
                game.setScreen(startMenuScreen); // Växla till spelet
            }
        });

        // Add Back button to table
        table.row().padTop(10);
        table.add(backButton).colspan(2).size(265, 70).center();
    }

    private List<HighscoreEntry> readHighscores() {
        List<HighscoreEntry> entries = new ArrayList<>();
        FileHandle file = Gdx.files.local("high_scores.json");

        if (!file.exists()) return entries;

        String jsonContent = file.readString();
        Pattern pattern = Pattern.compile("\\{name:([a-zA-Z\\s]+),score:(\\d+)\\}");
        Matcher matcher = pattern.matcher(jsonContent);

        while (matcher.find()) {
            HighscoreEntry entry = new HighscoreEntry();
            entry.name = matcher.group(1).trim();
            entry.score = Integer.parseInt(matcher.group(2).trim());
            entries.add(entry);
        }

        return entries;
    }


    private void sortHighscores(List<HighscoreEntry> highscores) {
        highscores.sort((o1, o2) -> Integer.compare(o2.score, o1.score));


    }

    private void drawBackground(float deltaTime) {
        bg1XPosition -= backgroundSpeed * deltaTime;
        bg2XPosition -= backgroundSpeed * deltaTime;

        if (bg1XPosition + backgroundImage.getWidth() <= 0) {
            bg1XPosition = bg2XPosition + backgroundWidth;
        }
        if (bg2XPosition + backgroundImage.getWidth() <= 0) {
            bg2XPosition = bg1XPosition + backgroundWidth;
        }

        game.spriteBatch.begin();
        game.spriteBatch.draw(backgroundImage, bg1XPosition, 0, backgroundWidth, Gdx.graphics.getHeight());
        game.spriteBatch.draw(backgroundImage, bg2XPosition, 0, backgroundWidth, Gdx.graphics.getHeight());
        game.spriteBatch.end();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        drawBackground(delta);

        // Rita knappar om menyn är aktiv
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
