package com.twodstudios.platformjumper;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import static com.twodstudios.platformjumper.AnimationManager.*;

public class Player implements Resettable <Player> {

    private SpriteBatch spriteBatch;

    // Player variables
    private int width;
    private int height;
    private float xPosition;
    private float yPosition;
    private float animationTime; // Time since start of animation
    private float verticalVelocity = 0f; // Speed of which player moves up or down
    private boolean isJumping; // Flag to check if the player is jumping
    private boolean isDead; // Flag to check if the player is dead

    // Player Texture Atlas, Texture Regions & Animation Objects
    private TextureAtlas atlas; // Atlas with all player animation frames
    private TextureRegion[] idleTextureRegions = new TextureRegion[10];
    private TextureRegion[] runningTextureRegions = new TextureRegion[10];
    private TextureRegion[] jumpingTextureRegions = new TextureRegion[10];
    private TextureRegion[] deadTextureRegions = new TextureRegion[10];
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runningAnimation;
    private Animation<TextureRegion> jumpingAnimation;
    private Animation<TextureRegion> deathAnimation;

    // Collision variable
    private Rectangle rectangle;


    /**
     * Create a new instance of Player.
     * @param playerWidth Width of player.
     * @param playerHeight Heigth of player.
     * @param playerXPosition Initial X-position of player.
     * @param playerYPosition Initial Y-position of player.
     */
    public Player(SpriteBatch spriteBatch, int playerWidth, int playerHeight, float playerXPosition, float playerYPosition) {

        this.spriteBatch = spriteBatch;
        this.width = playerWidth;
        this.height = playerHeight;
        this.xPosition = playerXPosition;
        this.yPosition = playerYPosition;
        setupPlayer();

    }

     /**
     * Method to initialise and populate necessary fields for the Player.
     */
    private void setupPlayer(){

        atlas = new TextureAtlas(Gdx.files.internal("atlas/character.atlas")); // Load all player assets

        // Create idle animation
        idleAnimation = createAnimation(atlas, idleTextureRegions, "Idle", 1/10f);

        // Create running animation
        runningAnimation = createAnimation(atlas, runningTextureRegions, "Run", 1/10f);

        // Create jumping animation
        jumpingAnimation = createAnimation(atlas, jumpingTextureRegions, "Jump", 1/10f);

        // Create death animation
        deathAnimation = createAnimation(atlas, deadTextureRegions, "Dead", 1/25f);

        isDead = false;
        isJumping = false;

        animationTime = 0f;

        // Create collision rectangle for collision checks
        rectangle = new Rectangle(xPosition - width / 2f, yPosition, width * 0.8f, height);
    }


    /** Get the X-position of the player. */
    public float getXPosition(){
        return xPosition;
    }

    /** Get the Y-position of the player. */
    public float getYPosition(){
        return yPosition;
    }

     /** Set Y-position of player.
     * @param yPosition Value to set the Y-position to.
     */
    public void setYPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    /** Get the width of the player. */
    public int getWidth() {
        return width;
    }

    /** Check if the player is dead. */
    public boolean isDead(){
        return isDead;
    }

    /** Set if player is dead or alive.
    * @param dead Set dead state to true or false.
    */
    public void setDead(boolean dead) {
        isDead = dead;
    }

    /** Set if player is jumping. */
    public boolean isJumping(){
        return isJumping;
    }

    /** Set jumping state of player.
    * @param jumping Set jumping state to true or false.*/
    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    /** Get velocity of player. */
    public float getVerticalVelocity(){
        return verticalVelocity;
    }

    /** Get rectangle of player. Can be used for collision logic. */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /**
    * Set new animation time to control animation of player.
    * @param animationTime Value to set animationTime to.
    */
    public void setAnimationTime(float animationTime){
        this.animationTime = animationTime;
    }

    /** Set vertical velocity of the player.
    * @param verticalVelocity Value to set vertical velocity of player.
    */
    public void setVerticalVelocity(float verticalVelocity){
        this.verticalVelocity = verticalVelocity;
    }

     /** Set variables to start player jumping animation. */
    public void startJump(){
        // Reset animation time so that each jump starts at first animation frame
        animationTime = 0;
        isJumping = true;
        verticalVelocity = 600;
    }

    /** Draw run or jump animation depending on player state. */
    public void drawRunOrJump(){
        Animation<TextureRegion> runOrJumpAnimation = isJumping ? jumpingAnimation : runningAnimation;
        TextureRegion runOrJumpFrame;

        // Prepare jumping animation by entering normal animation mode and reset animationTime to 0
        if (isJumping && runOrJumpAnimation.getPlayMode() != Animation.PlayMode.NORMAL) {
            runOrJumpAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            animationTime = 0;

        // Prepare run animation by entering loop animation mode
        } else if (!isJumping && runOrJumpAnimation.getPlayMode() != Animation.PlayMode.LOOP) {
            runOrJumpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }

        // Draw run or jump animation depending on player state
        if (isJumping) {
            runOrJumpFrame = runOrJumpAnimation.getKeyFrame(animationTime, false); // Looping off
        }else {
            runOrJumpFrame = runOrJumpAnimation.getKeyFrame(animationTime, true); // Looping on
        }

        // Draw the current frame of the running or jumping animation depending on player state
        spriteBatch.draw(runOrJumpFrame, xPosition - width / 2f, yPosition, width, height);
    }

    /** Draw death animation of player. */
    public void drawDeathAnimation(){
        TextureRegion atlasFrame;
        atlasFrame = deathAnimation.getKeyFrame(animationTime, false);

        // Draw the current frame
        spriteBatch.draw(atlasFrame, xPosition - width / 2f, yPosition, width, height);
    }

    /** Draw idle animation for the start screen. */
    public void drawIdleAnimation() {
        TextureRegion atlasFrame;
        atlasFrame = idleAnimation.getKeyFrame(animationTime, true); // Looping set to true

        // Draw the current frame
        spriteBatch.draw(atlasFrame, xPosition - width / 2f, yPosition, width, height);
    }

    /** Update position of rectangle to position of the player. */
    public void updateRectanglePosition(){
        // Set position of rectangle representing the player
        rectangle.setPosition(xPosition - width / 2f, yPosition);
    }

    /** Increase animation time with given value. */
    public void updateAnimationTime(float deltaTime){
        // Update animation times
        animationTime += deltaTime;
    }

    /** Update Y-position of player.
    * @param incrementBy Value to increase the Y-position of player with.
    */
    public void updateYposition(float incrementBy) {
        this.yPosition += incrementBy;
    }

    /** Update velocity of player.
    * @param changeBy Value to decrease or increase velocity with.
    */
    public void updateVerticalVelocity(float changeBy){
        this.verticalVelocity += changeBy;
    }

    /** Dispose of player assets. */
    public void dispose(){
        atlas.dispose();
    }

    /** Reset all necessary variables in preparation for a new game */
    @Override
    public void reset() {
        yPosition = 135f; // Reset player Y-position
        verticalVelocity = 0f; // Reset vertical speed of player
        isDead = false; // Set dead flag to false
        isJumping = false; // Set jumping flag to false
    }
}
