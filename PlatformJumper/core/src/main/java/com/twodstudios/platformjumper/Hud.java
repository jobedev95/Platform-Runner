package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import static com.twodstudios.platformjumper.AnimationManager.*;

/** Class to create and manage the game HUD. */
public class Hud {

    private Stage stage;
    private Skin skin;
    private Label score;
    private Image rotatingCoin;
    private HudListener hudListener;
    private TextureAtlas rotatingCoinAtlas;
    private TextureRegion[] rotatingCoinTextureRegions = new TextureRegion[12];
    private Animation<TextureRegion> rotatingCoinAnimation;
    private float animationTime = 0;

    public Hud(HudListener hudListener) {

        this.stage = new Stage(new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        this.hudListener = hudListener;

        Table table = new Table();
        table.setFillParent(true);
        table.top().pad(25, 50, 0, 50);
        stage.addActor(table);

        table.row();

        this.skin = new Skin(Gdx.files.internal("hud/new/hud_skin.json"));

        // Create rotating coin animation
        rotatingCoinAtlas = new TextureAtlas(Gdx.files.internal("hud/star_coin.atlas"));
        rotatingCoinAnimation = createAnimation(rotatingCoinAtlas, rotatingCoinTextureRegions, "rotating_star_coin", 1/10f);
        rotatingCoinAnimation.setPlayMode(Animation.PlayMode.LOOP);
        rotatingCoin = new Image(rotatingCoinAnimation.getKeyFrame(0));
        table.add(rotatingCoin).size(50, 50).padRight(20);


        // Score label
        this.score = new Label("O", skin);
        table.add(this.score)
            .expandX()
            .left();

        createPauseButton(table);

    }

    public Stage getStage() {
        return stage;
    }

    private void createPauseButton(Table table){

        ImageButton pauseButton = new ImageButton(skin);
        table.add(pauseButton)
            .size(40, 35)// set size of pause button
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

    public void render(float deltaTime){
        animationTime += deltaTime;
        TextureRegion currentFrame = rotatingCoinAnimation.getKeyFrame(animationTime);
        rotatingCoin.setDrawable(new TextureRegionDrawable(currentFrame));
        stage.act();
        stage.draw();
    }

}
