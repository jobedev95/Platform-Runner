package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class Hud {

    private Stage stage;
    private Skin skin;
    private Label score;
    private HudListener hudListener;

    public Hud(HudListener hudListener) {

        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.hudListener = hudListener;

        Table table = new Table();
        table.setFillParent(true);
        table.top().pad(25, 50, 0, 50);
        stage.addActor(table);

        table.row();

        this.skin = new Skin(Gdx.files.internal("hud/hud_skin.json"));

        // Score label
        this.score = new Label("O", skin);
        table.add(this.score)
            .expandX()
            .left();

        createPauseButton(table);

    }

    private void createPauseButton(Table table){

        ImageButton pauseButton = new ImageButton(skin);
        table.add(pauseButton)
            .size(50, 50) // set size of pause button
            .expandX()
            .right();

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hudListener.pauseGame();
            }
        });
    }

    public void setScore(int newScore){
        this.score.setText(newScore);
    }

    public void resize(int width, int height){
        stage.getViewport().update(width, height, true);
    }

    public void render(){
        stage.act();
        stage.draw();
    }

}

