package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.math.MathUtils.random;
import static java.lang.Math.abs;

/** Class to create and manage Tile objects. */
public class Tiles implements Resettable <Tiles> {

    private final SpriteBatch spriteBatch;
    private final float backgroundSpeed;
    private TextureAtlas atlas;
    private TextureRegion textureRegion = new TextureRegion();
    private Array<Float> xPositions;
    private Array<Float> yPositions;
    private Rectangle rectangle;
    private int tileWidth;
    private int tileHeight;

    private final float minTileDistance = 300; // Minimum horizontal distance between each tile
    private final float maxTileDistance = 600; // Maximum horizontal distance between each tile
    private final float minVerticalDistance = 130; // Minimum vertical distance (height difference) between each tile
    private final float maxVerticalDistance = 170; // Maximum vertical distance (height difference) between each tile
    private float maxHeight; // Maximum Y-coordinate for any tile
    private final float minHeight = 64;

    /**
     * Create a new instance of Tiles to create and manage tiles.
     * @param spriteBatch Spritebatch that handles drawing in the game.
     * @param backgroundSpeed Speed of moving background so that tiles can match the movement.
     */
    public Tiles(SpriteBatch spriteBatch, float backgroundSpeed) {
        this.spriteBatch = spriteBatch;
        this.backgroundSpeed = backgroundSpeed;
        setupTiles();
        prepareInitialTiles();// Preparing the first 15 tiles to be rendered
    }

    /**
     * Method to initialise and populate necessary fields for Tiles.
     */
    private void setupTiles(){
        this.tileWidth = 230;
        this.tileHeight = 40;
        xPositions = new Array<>(); // Array to hold X-positions of all tiles to be drawn
        yPositions = new Array<>(); // Array to hold Y-positions of all tiles to be drawn
        atlas = new TextureAtlas(Gdx.files.internal("atlas/lava_theme.atlas"));
        textureRegion = atlas.findRegion("tile_01");

        // Set maximum height placement of tiles
        maxHeight = Main.WORLD_HEIGHT - 300; // 300 pixels from the top of the screen

        // Collision rectangle representing each tile (for collision checks)
        rectangle = new Rectangle(0, 100, tileWidth, tileHeight);
    }

    /** Prepares the initial tiles for rendering. */
    public void prepareInitialTiles() {
        float initialTileX = 0; // Sets the initial x-position of the left-most tile
        for (int i = 0; i < 15; i++) { // for-loop to generate the X- and Y-positions of the first 15 tiles
            xPositions.add(initialTileX); // Adds the X-position to the tileXPositions array
            yPositions.add(100f); // Adds the Y-position to the tileXPositions array
            initialTileX += 100; // Sets the X-position of the next tile in the loop
        }
    }

    /** Prepares a buffer of tiles for rendering and removes tiles that have moved off-screen. */
    public void generateBufferTiles() {
        float screenRightEdge = Main.WORLD_WIDTH; // Gets the X-position of the right edge of the screen
        float lastTileXPosition = xPositions.peek(); // Gets the X-position of latest tile
        float lastTileYPosition = yPositions.peek(); // Gets the Y-position of latest tile

        // Loop that prepares a buffer of tiles to be rendered up to 500 pixels beyond the edge of the screen
        while (lastTileXPosition < screenRightEdge + 500) {

            // Calculate random new X-position by adding a minimum distance with a random offset within the specified range
            float randomDistance = minTileDistance + random.nextFloat() * (maxTileDistance - minTileDistance);

            // Set new X-position
            float newXPosition = lastTileXPosition + randomDistance;

            // Set new Y-position within allowed vertical limits
            float newYPosition = getNewTileHeight(minHeight, maxHeight, lastTileYPosition);

            // Add latest tile X- and Y-coordinates to their respective array
            xPositions.add(newXPosition);
            yPositions.add(newYPosition);

            // Update lastTileX to store the X-position of the recently added tile
            lastTileXPosition = newXPosition;
        }

        // Remove a tile from the arrays if it has moved beyond the left edge of the screen
        while (xPositions.first() + tileWidth < 0 && !xPositions.isEmpty()) {
            xPositions.removeIndex(0);
            yPositions.removeIndex(0);
        }
    }

    /** Generates a new Y-position that is within the given vertical limits for a tile.
    * @param minTileHeight Minimum possible height of generated tile
    * @param maxTileHeight Maximum possible height of generated tile
    * @param lastTileYPosition Y-position of the latest generated tile
    */
    private float getNewTileHeight(float minTileHeight, float maxTileHeight, float lastTileYPosition) {

        float randomHeight;
        float verticalDistance;
        float heightRange = maxTileHeight - minTileHeight; // Total height range

        do {
            // Generate a random float between minTileHeight and heightRange
            randomHeight = minTileHeight + random.nextFloat() * heightRange;

            // Calculate the absolute vertical distance between the randomHeight and the last tile positions
            verticalDistance = abs(randomHeight - lastTileYPosition);
        }
        // Loop until the vertical distance is within the specified range
        while (verticalDistance < minVerticalDistance || verticalDistance > maxVerticalDistance);
        return randomHeight;
    }

    /** Updates X-positions of all tiles, including buffer tiles */
    public void moveTiles(float deltaTime) {
        // for-loop that iterates through the x-position of all active and buffer tiles
        for (int i = 0; i < xPositions.size; i++){
            float updatedXPosition = xPositions.get(i) - backgroundSpeed * deltaTime;
            xPositions.set(i, updatedXPosition);
        }
    }

    /** Draw all current tiles. */
    public void drawTiles(){
        for (int i = 0; i < xPositions.size; i++) {
            spriteBatch.draw(textureRegion, xPositions.get(i), yPositions.get(i), tileWidth, tileHeight);
        }
    }

    /** Update position of Rectangle to the current position of the given tile. */
    public void updateRectanglePosition(int tileNumber){
        // Set position of rectangle representing the tile
        rectangle.setPosition(getXPosition(tileNumber), getYPosition(tileNumber));
    }



    /** Get X-position of one specific active tile. */
    public float getXPosition(int tileNumber) {
        return xPositions.get(tileNumber);
    }

    /** Get Y-position of one specific active tile. */
    public float getYPosition(int tileNumber) {
        return yPositions.get(tileNumber);
    }

    /** Get X-positions of all currently active tiles. */
    public Array<Float> getXPositions() {
        return xPositions;
    }

    /** Get Y-positions of all currently active tiles. */
    public Array<Float> getYPositions() {
        return yPositions;
    }

    /** Get tile Rectangle. Can be used for collision logic. */
    public Rectangle getRectangle() {
        return rectangle;
    }

    /** Get tile width. */
    public int getTileWidth() {
        return tileWidth;
    }

    /** Get tile height. */
    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    /** Reset all tile position arrays and prepare tiles for a new game. */
    @Override
    public void reset(){
        xPositions.clear(); // Clear X-position of tiles
        yPositions.clear(); // Clear Y-position of tiles
        prepareInitialTiles(); // Create the starting tiles
    }

    public void dispose(){
        atlas.dispose();
    }
}
