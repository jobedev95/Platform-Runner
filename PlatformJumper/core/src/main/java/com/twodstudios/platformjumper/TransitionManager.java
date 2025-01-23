package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.twodstudios.platformjumper.screens.PlayScreen;

public class TransitionManager {

    private Texture transitionImage;
    private Texture clearLevelImage;
    private float transitionImageXPosition;
    private float clearLevelImageXPosition;
    private boolean isVisibleTransitionFinished;
    private boolean isTransitionInProgress;
    Array<Float> winterBGParallaxMultipliers;

    PlayScreen game;
    ScoreManager scoreManager;
    EffectsManager effectsManager;
    float backgroundSpeed;

    Background currentLevelBackground;
    Background nextLevelBackground;

    Background lava_level_background;
    Background winter_level_background;


    public TransitionManager(PlayScreen game, ScoreManager scoreManager, EffectsManager effectsManager, float backgroundSpeed) {
        this.game = game;
        this.scoreManager = scoreManager;
        this.effectsManager = effectsManager;
        this.backgroundSpeed = backgroundSpeed;

        // Background speeds for all lava background textures
        Array<Float> lavaBGParallaxMultipliers = new Array<>();
        lavaBGParallaxMultipliers.addAll(0.1f, 0.15f, 0.20f, 0.45f, 0.45f, 0.8f);
        this.lava_level_background = new Background("atlas/lava_theme.atlas", backgroundSpeed, lavaBGParallaxMultipliers, 6, 0, game.spriteBatch);


        // Background speeds for all lava background textures
        winterBGParallaxMultipliers = new Array<>();
        winterBGParallaxMultipliers.addAll(0.1f, 0.15f, 0.25f, 0.40f, 0.60f, 0.65f, 0.80f, 0.85f, 0.50f, 0.7f);
        this.winter_level_background = new Background("atlas/winter_level.atlas", backgroundSpeed, winterBGParallaxMultipliers, 10, Main.WORLD_WIDTH + 300, game.spriteBatch);

        this.transitionImage = new Texture(Gdx.files.internal("transition_image.png"));
        this.transitionImageXPosition = Main.WORLD_WIDTH + 150;
        this.isVisibleTransitionFinished = false;
        this.isTransitionInProgress = false;




        this.clearLevelImage = new Texture(Gdx.files.internal("clear_level.png"));
        this.clearLevelImageXPosition = Main.WORLD_WIDTH;

        currentLevelBackground = lava_level_background;
        nextLevelBackground = winter_level_background;

    }

    public boolean isTransitionInProgress() {
        return isTransitionInProgress;
    }

    public void drawCurrentLevel(float deltaTime) {
        currentLevelBackground.drawBackgroundSet(true, deltaTime);
        currentLevelBackground.drawGround(true, deltaTime);
    }

    private void drawNewLevelBackground(float deltaTime) {
        nextLevelBackground.drawBackgroundSet(true, deltaTime);
        nextLevelBackground.drawGround(true, deltaTime);
        drawMovingTransitionImage(deltaTime);
    }

    private void clearCurrentLevel(float deltaTime){
        clearLevelImageXPosition -= backgroundSpeed * deltaTime;
        game.spriteBatch.draw(clearLevelImage, clearLevelImageXPosition, 0, Main.WORLD_WIDTH * 7, Main.WORLD_HEIGHT);
    }

    private void drawMovingTransitionImage(float deltaTime){
        transitionImageXPosition = nextLevelBackground.getFirstBackgroundX() - transitionImage.getWidth() / 2f;

        game.spriteBatch.draw(transitionImage, transitionImageXPosition, 0, transitionImage.getWidth(), Main.WORLD_HEIGHT);
        effectsManager.drawTransitionEffect(deltaTime, transitionImageXPosition, (Main.WORLD_HEIGHT / 2) * 1.2f);
    }


    /** Updates the speed of the next level. */
    private void updateNewLevelBackgroundSpeed() {
        if (nextLevelBackground.getFirstBackgroundX() <= 0){
            // Reset speeds to parallax multipliers
            winterBGParallaxMultipliers.clear();
            winterBGParallaxMultipliers.addAll(0.1f, 0.15f, 0.25f, 0.40f, 0.60f, 0.65f, 0.80f, 0.85f, 0.50f, 0.7f);
            isVisibleTransitionFinished = true;

        } else {
            winterBGParallaxMultipliers.clear();
            winterBGParallaxMultipliers.addAll(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f);
        }
        nextLevelBackground.changeSpeedMultipliers(winterBGParallaxMultipliers);
    }

    private void checkTransitionFinished() {
        if (nextLevelBackground.getFirstBackgroundX() == 0) {
            isVisibleTransitionFinished = true;
            isTransitionInProgress = false;
            System.out.println("VISIBLE TRANSITION FINISHED!");
            currentLevelBackground = nextLevelBackground;
        }
    }

    public void progressTheTransition(float deltaTime) {
        if (!isTransitionInProgress) return;

        // If the visible part of the transition is not finished...
        if (!isVisibleTransitionFinished) {

            // Draw current level background
            drawCurrentLevel(deltaTime);
            clearCurrentLevel(deltaTime);

            // Update speeds of new level
            updateNewLevelBackgroundSpeed();

            System.out.println("CHECKING");
            checkTransitionFinished();

        }
        drawNewLevelBackground(deltaTime);
        drawMovingTransitionImage(deltaTime);
    }

    public Background getCurrentLevelBackground() {
        return currentLevelBackground;
    }

    public Background getNextLevelBackground() {
        return nextLevelBackground;
    }

    public void startTransition() {
        isTransitionInProgress = true;
        resetTransition();
        //Background temp = currentLevelBackground;
        //currentLevelBackground = nextLevelBackground;
        //nextLevelBackground = temp;
    }

    private void resetTransition() {
        transitionImageXPosition = Main.WORLD_WIDTH + 150;
        clearLevelImageXPosition = Main.WORLD_WIDTH;
        isVisibleTransitionFinished = false;
    }
}
