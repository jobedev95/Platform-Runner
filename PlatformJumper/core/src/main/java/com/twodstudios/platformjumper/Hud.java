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
import com.badlogic.gdx.utils.viewport.ScreenViewport;


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

        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.hudListener = hudListener;

        Table table = new Table();
        table.setFillParent(true);
        table.top().pad(25, 50, 0, 50);
        stage.addActor(table);

        table.row();

        this.skin = new Skin(Gdx.files.internal("hud/hud_skin.json"));

        rotatingCoinAtlas = new TextureAtlas(Gdx.files.internal("atlas/rotating_star.atlas"));
        rotatingCoinAnimation = createAnimation(rotatingCoinAtlas, rotatingCoinTextureRegions, "rotating_star_", 1/10f);
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

    /** Creates an animation from a TextureRegion array.
     * @param atlas TextureAtlas containing the assets needed for the animation.
     * @param textureRegions Empty array of TextureRegions that together will become the Animation object.
     * @param fileBaseName Base name of the TextureRegions.
     * @param frameDuration Adjusts how long each frame of the animation should be shown e.g. 1/30.
     */
    private Animation<TextureRegion> createAnimation(TextureAtlas atlas, TextureRegion[] textureRegions, String fileBaseName, float frameDuration){

        int amountOfTextures = textureRegions.length;

        // Save all TextureRegions in an array
        for (int i = 0; i < amountOfTextures; i++) {
            String frameName = fileBaseName + i;
            textureRegions[i] = atlas.findRegion(frameName);
        }

        // Create and return animation object
        return new Animation<TextureRegion>(frameDuration, textureRegions);
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

    public void render(float deltaTime){
        animationTime += deltaTime;
        TextureRegion currentFrame = rotatingCoinAnimation.getKeyFrame(animationTime);
        rotatingCoin.setDrawable(new TextureRegionDrawable(currentFrame));
        stage.act();
        stage.draw();
    }

}
