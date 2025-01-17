package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * class for creating and drawing different parallax backgrounds.
 */
public class Background {
    private TextureAtlas atlas; // Texture atlas object.
    private TextureRegion[] backgroundImages; // Array of texture regions.
    private float[][] bgXPositions;
    private float backgroundSpeed;
    private Main game;
    private float[] speeds; // Array that will store different speeds for the parallax background textures.

    /**
     * Constructor for the Background class.
     * @param backgroundSpeed Base speed of parallax background.
     * @param atlasFileName File path to .atlas file
     * @param numOfAssets Number of backgrounds.
     * @param game to be decided.
     */
    public Background(float backgroundSpeed, String atlasFileName, int numOfAssets, Main game) {
        this.backgroundSpeed = backgroundSpeed;
        this.backgroundImages = new TextureRegion[numOfAssets]; // Array with all texture regions of the backgrounds
        this.bgXPositions = new float[this.backgroundImages.length][3]; // Preparing array for x positions of background images
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasFileName));// Creating atlas object
        this.game = game;
        this.speeds = new float[]{this.backgroundSpeed * 0.10f, this.backgroundSpeed * 0.15f, this.backgroundSpeed * 0.20f, this.backgroundSpeed * 0.45f,
        this.backgroundSpeed * 0.45f, this.backgroundSpeed * 0.8f};// Array of speeds for each background

        // For loop to load all background texture regions and assign initial x position of all backgrounds
        for(int i = 0; i < backgroundImages.length; i++){
            // Loading all texture regions
            String imageName = String.format("bg_" + i);
            backgroundImages[i] = atlas.findRegion(imageName);
            // Assign initial x positions
            //
            float bgWidth = backgroundImages[i].getRegionWidth();// Get width of all texture region
            this.bgXPositions[i][0] = 0;
            this.bgXPositions[i][1] = bgWidth;
            this.bgXPositions[i][2] = this.bgXPositions[i][1] + bgWidth;
        }
    }

    /**
     * Draws a set of texture regions with different speeds to create a parallax effect.
     * @param shouldMove If true the background will move, or else it will stay static.
     */
    public void drawBackgroundSet(boolean shouldMove, float deltaTime){
        for(int i = 0; i < this.backgroundImages.length; i++){
            drawBackground(this.backgroundImages[i], shouldMove, deltaTime, speeds[i], bgXPositions[i]);
        }
    }

    public void setBackgroundSpeed(float newBackgroundSpeed){this.backgroundSpeed = newBackgroundSpeed;}

    /**
     * Draws a texture region at a giving speed. Enable or disable moving background using the shouldMove parameter.
     * @param background Texture region to be drawn.
     * @param shouldMove If true the background will move, or else it will stay static.
     * @param speed Scrolling speed of the moving background.
     * @param bgXPositions Array of three x positions for seamless background movement.
     */
    public void drawBackground(TextureRegion background, boolean shouldMove, float deltaTime, float speed, float[] bgXPositions){

        if (shouldMove) {

            // Update background positions for endless scrolling of background
            bgXPositions[0] -= speed * deltaTime;
            bgXPositions[1] -= speed * deltaTime;
            bgXPositions[2] -= speed * deltaTime;


            // Reset background positions when they reach the edge
            if (bgXPositions[0] + background.getRegionWidth() <= 0) { // If background 1 is fully off the screen...
                bgXPositions[0] = bgXPositions[2] + background.getRegionWidth(); // Set position of background 1 to the right of bg2
            }
            if (bgXPositions[1] + background.getRegionWidth() <= 0) { // If background 2 is fully off the screen...
                bgXPositions[1] = bgXPositions[0] + background.getRegionWidth(); // Set position of background 2 to the right of bg1
            }
            if (bgXPositions[2] + background.getRegionWidth() <= 0) { // If background 3 is fully off the screen...
                bgXPositions[2] = bgXPositions[1] + background.getRegionWidth(); // Set position of background 3 to the right of bg2
            }
        }
        // Draw background images
        game.spriteBatch.draw(background, bgXPositions[0], 0, background.getRegionWidth(), Main.WORLD_HEIGHT);
        game.spriteBatch.draw(background, bgXPositions[1], 0, background.getRegionWidth(), Main.WORLD_HEIGHT);
        game.spriteBatch.draw(background, bgXPositions[2], 0, background.getRegionWidth(), Main.WORLD_HEIGHT);
    }
}
