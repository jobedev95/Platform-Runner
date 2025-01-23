package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Class for creating and drawing different parallax backgrounds.
 */
public class Background {

    private Main game;

    // Textures
    private TextureAtlas atlas; // Texture atlas object.
    private TextureRegion[] backgroundImages; // Array of texture regions.
    private TextureRegion ground;

    // Background variables
    private float[][] bgXPositions;
    private float backgroundSpeed;
    private Array<Float> speeds; // Array that will store different speeds for the parallax background textures.
    private Array<Float> parallaxMultipliers;
    private float initialXPosition;

    // Ground variables
    private float groundXPosition = 0;
    private int groundWidth;
    private int groundHeight;
    private int numOfGrounds;
    private int totalDuplicateGrounds;

    /**
     * Constructor for the Background class.
     * @param atlasFileName File path to .atlas file
     * @param backgroundSpeed Base speed of parallax background.
     * @param parallaxMultipliers Float array of background speed multipliers. Used to calculate the speed of every background texture.
     * @param numOfAssets Number of backgrounds.
     * @param game to be decided.
     */
    public Background(String atlasFileName, float backgroundSpeed, Array<Float> parallaxMultipliers, int numOfAssets, float initialXPosition, Main game) {

        this.game = game;
        this.parallaxMultipliers = parallaxMultipliers;
        this.initialXPosition = initialXPosition;

        // Creating atlas object
        this.atlas = new TextureAtlas(Gdx.files.internal(atlasFileName));

        // Initialising background variables.
        this.backgroundSpeed = backgroundSpeed;
        this.backgroundImages = new TextureRegion[numOfAssets]; // Array with all texture regions of the backgrounds
        this.bgXPositions = new float[this.backgroundImages.length][3]; // Preparing array for x positions of background images
        setSpeeds(); // Create array of speeds for each background.



        // Initialising ground variables.
        this.ground = this.atlas.findRegion("ground");
        this.groundWidth = ground.getRegionWidth();
        this.groundHeight = ground.getRegionHeight();
        this.numOfGrounds = (int) Main.WORLD_WIDTH / groundWidth;
        this.totalDuplicateGrounds = (int) (numOfGrounds * 2.1);



        // For loop to load all background texture regions and assign initial x position of all backgrounds.
        for(int i = 0; i < backgroundImages.length; i++){

            // Loading all texture regions
            String imageName = String.format("bg_" + i);
            backgroundImages[i] = atlas.findRegion(imageName);

            backgroundImages[i] = atlas.findRegion(imageName);
                if (backgroundImages[i] == null) {
                    Gdx.app.log("Background Load Error", "No region found for: " + imageName);
                }
            // Assign initial x positions
            float bgWidth = backgroundImages[i].getRegionWidth();// Get width of all texture region.
            this.bgXPositions[i][0] = initialXPosition;
            this.bgXPositions[i][1] = initialXPosition + bgWidth;
            this.bgXPositions[i][2] = this.bgXPositions[i][1] + bgWidth;
        }
    }

    /** Calculates and sets the speeds for the different backgrounds based on the given multipliers. */
    private void setSpeeds(){

        int amountOfSpeeds = this.parallaxMultipliers.size; // Store amount of speeds
        this.speeds = new Array<>(); // Create a new array with the same amount of floats

        // Loop through each multiplier to calculate the background speeds for each background
        for (int i = 0; i < amountOfSpeeds; i++){
            this.speeds.add(this.backgroundSpeed * parallaxMultipliers.get(i));
        }
    }


    public void changeSpeedMultipliers(Array<Float> speedMultipliers){

        parallaxMultipliers = speedMultipliers;


        int amountOfSpeeds = this.parallaxMultipliers.size; // Store amount of speeds


        // Loop through each multiplier to calculate the background speeds for each background
        for (int i = 0; i < amountOfSpeeds; i++){
            this.speeds.set(i, this.backgroundSpeed * parallaxMultipliers.get(i));
        }
    }


    /**
     * Draws a set of texture regions with different speeds to create a parallax effect.
     * @param shouldMove If true the background will move, or else it will stay static.
     */
    public void drawBackgroundSet(boolean shouldMove, float deltaTime){


        for(int i = 0; i < this.backgroundImages.length; i++){
            drawBackground(this.backgroundImages[i], shouldMove, deltaTime, speeds.get(i), bgXPositions[i]);
        }
    }

    /**
     * Set new background speed for the parallax background.
     * @param newBackgroundSpeed Background speed to be set.
     */
    public void setBackgroundSpeed(float newBackgroundSpeed){this.backgroundSpeed = newBackgroundSpeed;}

    /**
     * Returns height of ground texture.
     */
    public int getGroundHeight(){return this.groundHeight;}

    /**
     * Draws a texture region at a giving speed. Enable or disable moving background using the shouldMove parameter.
     * @param background Texture region to be drawn.
     * @param shouldMove If true the background will move, or else it will stay static.
     * @param speed Scrolling speed of the moving background.
     * @param bgXPositions Array of three x positions for seamless background movement.
     */
    public void drawBackground(TextureRegion background, boolean shouldMove, float deltaTime, float speed, float[] bgXPositions){

        if (shouldMove) {

            // Update background positions for endless scrolling of background.
            bgXPositions[0] -= speed * deltaTime;
            bgXPositions[1] -= speed * deltaTime;
            bgXPositions[2] -= speed * deltaTime;


            // Reset background positions when they reach the edge.
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
        // Draw background images.
        game.spriteBatch.draw(background, bgXPositions[0], 0, background.getRegionWidth(), Main.WORLD_HEIGHT);
        game.spriteBatch.draw(background, bgXPositions[1], 0, background.getRegionWidth(), Main.WORLD_HEIGHT);
        game.spriteBatch.draw(background, bgXPositions[2], 0, background.getRegionWidth(), Main.WORLD_HEIGHT);
    }

    /**
     * Draws moving ground texture in the set background speed.
     */
    public void drawGround(boolean shouldMove, float deltaTime){

        if (shouldMove) {
            // Update base x position of ground for endless scrolling of ground.
            groundXPosition -= this.backgroundSpeed * deltaTime;

            // Reset base x position when ground textures reaches the edge.
            if (groundXPosition < -Main.WORLD_WIDTH) {
                groundXPosition += this.numOfGrounds * groundWidth;
            }
        }

        // Draw ground textures.
        for (int i = 0; i < this.totalDuplicateGrounds; i++) {
            float currentGroundX = groundXPosition + (groundWidth * i); // Stores x position of texture about to be drawn.
            // Draws texture if it is within view.
            if (currentGroundX > -groundWidth && currentGroundX < Main.WORLD_WIDTH) {
                game.spriteBatch.draw(this.ground, groundXPosition + (groundWidth * i), 0);
            }
        }
    }


}
