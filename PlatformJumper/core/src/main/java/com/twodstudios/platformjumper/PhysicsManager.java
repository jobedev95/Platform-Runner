package com.twodstudios.platformjumper;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/** Class to handle all physics and collision checks in the game. */
public class PhysicsManager {

    private Player player;
    private Tiles tiles;
    private Array<Coin> coins;
    private SoundManager soundManager;
    private ScoreUpdater scoreUpdater;
    private Array<Float> tileXPositions;
    private Array<Float> tileYPositions;
    private Rectangle playerRectangle;
    private Rectangle tileRectangle;
    private int tileHeight;
    private float gravity = 980f;

    /**
     * Create instance of PhysicsManager.
     * @param player Player object for PhysicsManager to make collision checks with.
     * @param tiles Tiles object for PhysicsManager to make collision checks with.
     * @param soundManager Sound manager to play game over sound when player dies.
     * @param coins Coins manager for PhysicsManager to make collision checks with.
     * @param scoreUpdater ScoreUpdater object for PhysicsManager to update score when colliding with coins.
     */
    public PhysicsManager(Player player, Tiles tiles, SoundManager soundManager, Array<Coin> coins, ScoreUpdater scoreUpdater) {
        this.player = player;
        this.tiles = tiles;
        this.soundManager = soundManager;
        this.coins = coins;
        this.scoreUpdater = scoreUpdater;
    }

    /** Checks if the player has collided with a tile or hit the bottom.
     * When collision is detected it will reset the velocity to 0 and stop
     * the jumping animation. It will also initiate the death animation if the bottom has been reached. */
    public void checkCollision() {

        // Set position of rectangle representing the player
        player.updateRectanglePosition();
        playerRectangle = player.getRectangle();


        tileHeight = tiles.getTileHeight();

        // If character is going down check for potential tile collision
        if (player.getVerticalVelocity() <= 0) {

            tileXPositions = tiles.getXPositions();

            // Loop through all current tiles
            for (int i = 0; i < tileXPositions.size; i++) {
                float tileY = tiles.getYPosition(i); // Temporarily store Y-position of tile

                // Set position of rectangle representing the tile
                tiles.updateRectanglePosition(i);
                tileRectangle = tiles.getRectangle();

                // Checks if the player overlaps with any tile that is under the player
                if (playerRectangle.overlaps(tileRectangle) && playerRectangle.y >= tileRectangle.y + (tileHeight * 0.6)) {
                    player.setYPosition(tileY + tileHeight); // Put player on top of the tile
                    player.setJumping(false); // Flag to stop the jumping animation
                    player.setVerticalVelocity(0f); // Set velocity to 0 to stop player from falling
                    break;
                }
            }

        }
        checkCoinCollision(); // Check for collision with any coins

        checkGroundCollision(); // Check if player has collided with the ground, if so player dies
    }

    /** Check for collision with any coins. */
    private void checkCoinCollision(){
        for (int i = 0; i < coins.size; i++) {
            Coin coin = coins.get(i);
            if (playerRectangle.overlaps(coin.getRectangle())) {
                coins.removeIndex(i);
                scoreUpdater.increaseScore();
                break;
            }
        }
    }
    
    /** Checks if player has collided with the ground. Sets player state to dead after collision is confirmed. */
    private void checkGroundCollision(){
        // Logic that checks if character touches the ground
        if (player.getYPosition() <= 0) {
            player.setYPosition(0);  // Set character position firmly to 0 to ensure it's not set beyond the floor
            player.setDead(true); // Change flag to initiate death animation
            player.setAnimationTime(0); // Reset animation time so death animation starts at first animation frame
            player.setVerticalVelocity(0f); // Set velocity to 0 to stop character from falling
            player.setJumping(false); // Stop jumping animation instantly

            soundManager.playGameOverSound(); // Play game over sound

        }
    }

    /** Applies physics so that the character falls according to gravity. */
    public void applyGravity(float deltaTime) {
        player.updateVerticalVelocity(-(gravity * deltaTime)); // Decrease or increase velocity according to gravity over time (pixels/s)
        player.updateYposition(player.getVerticalVelocity() * deltaTime); // Change character position up or down based on velocity and how much time has passed
    }
}
