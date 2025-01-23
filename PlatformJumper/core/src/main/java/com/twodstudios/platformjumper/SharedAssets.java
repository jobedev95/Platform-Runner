package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


import static com.twodstudios.platformjumper.AnimationManager.*;

/** Class to handle any assets that will be shared among multiple classes. */
public class SharedAssets implements Resettable <SharedAssets>{

    private final SpriteBatch spriteBatch;

    // Main logo variables
    private TextureAtlas logoAtlas;
    private final TextureRegion[] logoTextureRegions = new TextureRegion[66];
    private Animation<TextureRegion> logoAnimation;
    private float logoAnimationTime; // Time since start of main logo animation
    private boolean isLogoAnimationFinished;

    /**
     * Create instance of SharedAssets.
     * @param spriteBatch Spritebatch that handles drawing in the game.
     */
    public SharedAssets(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;

        setupSharedAssets();
    }

    /**
     * Method to initialise and populate necessary fields for the SharedAssets.
     */
    private void setupSharedAssets(){
        // Load main logo atlas
        logoAtlas = new TextureAtlas(Gdx.files.internal("atlas/main_logo.atlas"));

        // Create main logo animation
        logoAnimation = createAnimation(logoAtlas, logoTextureRegions, "main_logo", 1/30f);
        logoAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        // Initialise animation time for logo
        logoAnimationTime = 0f;
        isLogoAnimationFinished = false;
    }

    /** Update the main logo animation time with given deltaTime. */
    public void updateMainLogoAnimationTime(float deltaTime){
        logoAnimationTime += deltaTime;
    }

    /**
     * Draws the main logo animation.
     * @param width Width of animation logo.
     * @param height Height of animation logo.
     * @param heightOffset Amount to offset the logo with from the top.
     * @param endAnimation Flag to control if logo animation should end.
     */
    public void drawLogoAnimation(int width, int height, int heightOffset, boolean endAnimation) {
        TextureRegion atlasFrame; // Will store the frame to be drawn

        // When set to end, the animation is marked as finished when it has returned to the starting frame
        if (endAnimation && logoAnimation.getKeyFrameIndex(logoAnimationTime) == 0) {
            isLogoAnimationFinished = true;
        }

        // Get the current frame for the animation
        atlasFrame = logoAnimation.getKeyFrame(logoAnimationTime, true);

        // Draw the current frame
        spriteBatch.draw(atlasFrame, Main.WORLD_WIDTH / 2f - width / 2f, Main.WORLD_HEIGHT - heightOffset, width, height);
    }

    /** Set status of main logo animation. */
    public boolean isLogoAnimationFinished() {
        return isLogoAnimationFinished;
    }

    /** Reset shared assets. */
    @Override
    public void reset(){
        isLogoAnimationFinished = false;
        this.logoAnimationTime = 0;
    }

    /** Dispose of shared assets. */
    public void dispose(){
        logoAtlas.dispose();
    }
}
