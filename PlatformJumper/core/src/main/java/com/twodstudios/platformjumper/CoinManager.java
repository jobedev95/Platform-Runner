package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.math.MathUtils.random;


/** Class to generate, manage and handle Coin objects.*/
public class CoinManager {

    private SpriteBatch spriteBatch;
    private Tiles tiles;

    private float coinWidth;
    private float coinHeight;
    private float maxHeight; // Maximum y-coordinate for any coin
    private float minHeight; // Minimum y-coordinate for any coin
    private float backgroundSpeed;
    private float animationTime;
    private Array<Coin> coins; // Array to hold coin objects
    private Array<Float> tileXPositions;
    private Array<Float> tileYPositions;

    // Cooldown time that determines the minimum amount of time between each generated coin
    private float cooldownTime;

    /**
     * Create a new instance of CoinManager to manage Coin objects.
     * @param spriteBatch Spritebatch that handles drawing in the game.
     * @param tiles Tiles object.
     * @param backgroundSpeed Speed of moving background so that coins can match the movement.
     */
    public CoinManager(SpriteBatch spriteBatch, Tiles tiles, float backgroundSpeed) {
        this.spriteBatch = spriteBatch;
        this.tiles = tiles;
        this.backgroundSpeed = backgroundSpeed;
        setupCoinManager();
    }

    /**
     * Method to initialise and populate necessary fields for the CoinManager.
     */
    private void setupCoinManager(){
        coins = new Array<Coin>(); // Array to hold all Coin objects

        // Set maximum height placement of tiles
        maxHeight = Gdx.graphics.getHeight() - 150; // 150 pixels from the top of the screen
        animationTime = 0f;
        coinWidth = 60;
        coinHeight = 60;

        // Cooldown time that determines the minimum amount of time between each generated coin
        cooldownTime = 2f;
    }

    /** Get the coins array holding all the Coin objects. */
    public Array<Coin> getCoins() {
        return coins;
    }

    /** Draws all currently active coins. */
    public void drawCoins(){
        for (Coin coin : coins) {
            drawCoinAnimation(coin);
        }
    }

    /** Generates a random float between 2-5 to dynamically set the amount of seconds for the cooldown time. */
    private void generateNewCooldownTime(){
        cooldownTime = 2 + random.nextFloat() * 3;
    }

    /** Updates the cooldown time by decreasing it with the given deltatime until it reaches 0. */
    private void updateCooldownTime(float deltaTime){
        cooldownTime -= deltaTime;

        // Set firmly to 0 when cooldown time becomes 0 or less
        if (cooldownTime <= 0){
            cooldownTime = 0;
        }
    }

    /** Generate coins at random. After each cooldown time, there is a 50% chance a new coin will spawn.
    * Spawn logic ensures the position of each coin will be reachable for the player to some extent.
    */
    public void generateCoins(float deltaTime){
        updateCooldownTime(deltaTime); // Decrease cooldown time by deltatime

        // When cooldown time reaches 0, check to see if a new coin should be spawned
        if (cooldownTime == 0) {
            
            // 50% chance of spawning coin on new tile
            if (random.nextFloat() < 0.5f) {
                float screenRightEdge = Gdx.graphics.getWidth(); // Gets the x-position of the right edge of the screen
                tileXPositions = tiles.getXPositions();  // Get x-positions of all active tiles (including buffer tiles)
                tileYPositions = tiles.getYPositions();  // Get y-positions of all active tiles (including buffer tiles)

                float coinX = screenRightEdge;  // X-position of all coins will always be at the right edge of the screen
                float coinY = getNewCoinHeight();  // Sets a randomly generated height for the coin (within reachable distance)
                
                // Add new Coin object to coins array with given positions
                coins.add(new Coin(coinX, coinY, coinWidth, coinHeight)); 

                generateNewCooldownTime(); // Generate new cooldown time (Up to 5s)
            }

            // As long as the coins array is not empty, remove any coins that has gone beyond the left edge from array
            while (!coins.isEmpty() && coins.first().getX() + coinWidth < 0) {
                coins.removeIndex(0);
            }

        }
    }

    /** Generates a new Y-position that is within the given vertical limits for a coin. */
    private float getNewCoinHeight() {
        tileYPositions = tiles.getYPositions(); // Get Y-positions of all currently active tiles

        float randomHeightPosition;

        do {
            // Mininum height set to height of latest tile + 50
            minHeight = tileYPositions.peek() + 50;

            // Generate a random number between minHeight and maxHeight
            randomHeightPosition = minHeight + random.nextFloat() * (maxHeight - minHeight);

        }
        // Loop until the randomly generated height is within the specified range
        while (randomHeightPosition > maxHeight || randomHeightPosition < minHeight);

        return randomHeightPosition;
    }

    /** Updates X-positions of all coins */
    public void moveCoins(float deltaTime) {
        // For-loop to re-position all currently active coins to the left in accordance with the background speed
        for (Coin coin : coins) {
            float updatedCoinX = coin.getX() - backgroundSpeed * deltaTime;
            coin.setX(updatedCoinX);
        }
    }

    /** Update the animation time which controls animation of the coins. */
    public void updateAnimationTime(float deltaTime){
        // Update animation time
        animationTime += deltaTime;
    }

    /**
     * Draw coin animation.
     * @param coin Coin object to be drawn.
     */
    private void drawCoinAnimation(Coin coin) {
        TextureRegion atlasFrame;
        atlasFrame = coin.getAnimation().getKeyFrame(animationTime, true); // Looping set to true

        // Draw the current frame
        spriteBatch.draw(atlasFrame, coin.getX(), coin.getY(), coin.getWidth(), coin.getHeight());
    }

    /** Reset array of coins and the cooldown time for the coins spawn */
    public void reset(){
        coins.clear(); // Clear coin objects from array
        cooldownTime = 2f; // Reset cooldownTime to its default
    }
}
