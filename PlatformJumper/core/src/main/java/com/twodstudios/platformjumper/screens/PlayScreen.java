package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twodstudios.platformjumper.Main;
import static com.badlogic.gdx.math.MathUtils.random;
import static java.lang.Math.abs;

public class PlayScreen implements Screen {
    private Main game;

    // Textures
    private Texture backgroundImage;
    private Texture tile;

    // Character variables
    private float characterXPosition;
    private float characterYPosition;  // y-position of the character
    private float characterAnimationTime; // Time since start of animation
    private float verticalVelocity = 0f; // Speed of which character moves up or down
    private int characterWidth;
    private int characterHeight;
    private boolean isJumping; // Flag to check if the character is jumping
    private boolean isDead; // Flag to check if the character is dead

    // Tile variables
    private int tileWidth;
    private int tileHeight;

    // Background variables
    private float bg1XPosition, bg2XPosition; // X-positions of the two looping backgrounds
    private float backgroundSpeed = 300f; // Background movement speed

    // Tile variables
    private Array<Float> tileXPositions;  // Dynamic array for x-coordinates of tiles
    private Array<Float> tileYPositions;  // Dynamic array for y-coordinates of tiles
    private float minTileDistance = 300; // Minimum horizontal distance between each tile
    private float maxTileDistance = 630; // Maximum horizontal distance between each tile
    private float minVerticalDistance = 130;  // Minimum vertical distance (height difference) between each tile
    private float maxVerticalDistance = 200;   // Maximum vertical distance (height difference) between each tile
    private float maxTileHeight; // Maximum y-coordinate for any tile

    // Collision variables
    private Rectangle characterRectangle;
    private Rectangle tileRectangle;

    // Camera and Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    // Start Mode logic
    private boolean startMode = true; // Flag to initiate the start screen
    private boolean zoomingOut = false; // Flag
    private float initialCameraZoom = 1.0f; // Initial zoom for camera in start mode
    private float newZoomLevel = 1f; // Game Mode zoom level
    private float zoomSpeed = 0.05f; // Camera zoom speed

    // Logo Atlas & Texture Regions
    private TextureAtlas logoAtlas;
    private TextureRegion[] logoTextureRegions = new TextureRegion[66];
    private Animation<TextureRegion> logoAnimation;
    private float logoAnimationTime; // Time since start of animation
    private boolean logoAnimationFinished;


    // Character Atlas, Texture Regions & Animation Objects
    private TextureAtlas characterAtlas; // Atlas with all character animation frames
    private TextureRegion[] idleTextureRegions = new TextureRegion[10];
    private TextureRegion[] runningTextureRegions = new TextureRegion[10];
    private TextureRegion[] jumpingTextureRegions = new TextureRegion[10];
    private TextureRegion[] deadTextureRegions = new TextureRegion[10];
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runningAnimation;
    private Animation<TextureRegion> jumpingAnimation;
    private Animation<TextureRegion> deathAnimation;

    // Constructor
    public PlayScreen(Main game){
        this.game = game;
    }

    @Override
    public void show() {

        // Texture Atlas
        logoAtlas = new TextureAtlas(Gdx.files.internal("atlas/main_logo.atlas"));
        characterAtlas = new TextureAtlas(Gdx.files.internal("atlas/character.atlas"));

        // Create idle animation
        idleAnimation = createCharacterAnimation(idleTextureRegions, "Idle__00", 1/10f);

        // Create running animation
        runningAnimation = createCharacterAnimation(runningTextureRegions, "Run__00", 1/10f);

        // Create jumping animation
        jumpingAnimation = createCharacterAnimation(jumpingTextureRegions, "Jump__00", 1/10f);

        // Create death animation
        deathAnimation = createCharacterAnimation(deadTextureRegions, "Dead__00", 1/25f);

        // Create main logo animation
        for (int i = 0; i < logoTextureRegions.length; i++) {
            String frameName = String.format("main_logo" + i);
            logoTextureRegions[i] = logoAtlas.findRegion(frameName);
        }

        // Create the animation game logo object
        float frameDuration = 1/30f;
        logoAnimation = new Animation<TextureRegion>(frameDuration, logoTextureRegions);
        logoAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        // Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        camera.setToOrtho(false, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        camera.position.set(characterXPosition, characterYPosition, 0); // Center the camera
        camera.zoom = initialCameraZoom; // Start with camera zoomed in
        camera.update();

        backgroundImage = new Texture("gameBG.png");
        tile = new Texture("tile.png");

        // Initialise animation time for logo and character
        logoAnimationTime = 0f;
        characterAnimationTime = 0f;

        // Initialise character variables
        characterWidth = 120;
        characterHeight = 150;
        characterXPosition = viewport.getWorldWidth() / 2;
        characterYPosition = 130f; // Initial y-position of the character
        isDead = false;
        isJumping = false;
        bg1XPosition = 0;
        bg2XPosition = -backgroundImage.getWidth();

        // Set maximum height placement of tiles
        maxTileHeight = Gdx.graphics.getHeight() - 300; // 300 pixels from the top of the screen

        // TILE LOGIC
        tileWidth = 230;
        tileHeight = 30;
        tileXPositions = new Array<>(); // Array to hold x-positions of all tiles to be drawn
        tileYPositions = new Array<>(); // Array to hold y-positions of all tiles to be drawn
        prepareInitialTiles(); // Preparing the first 15 tiles to be rendered

        // COLLISION RECTANGLES (for collision checks)
        characterRectangle = new Rectangle(characterXPosition, characterYPosition, characterWidth, characterHeight);
        tileRectangle = new Rectangle(0, 100, tileWidth, tileHeight);
    }

    @Override
    public void render(float delta) {

        float deltaTime = Gdx.graphics.getDeltaTime(); // Gets time lapsed since last frame
        characterAnimationTime += deltaTime; // Update animation time
        logoAnimationTime += deltaTime;

        // Exits start mode when Enter is pressed
        if (startMode && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startMode = false;
            zoomingOut = true;
        }

        if (!startMode && !isDead) {
            // If space-bar is pressed or mouse is clicked and the character is not already in a jumping state increase velocity
            if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) && !isJumping) {
                // Reset animation time so that each jump starts at first animation frame
                characterAnimationTime = 0;
                isJumping = true;
                verticalVelocity = 600;
            }
            applyGravity(deltaTime); // Enable gravity
            checkCollision(); // Check for tile and floor collisions
            generateBufferTiles(); // Prepares a buffer of tiles when needed
            moveTiles(deltaTime); // Continuously moves all tiles towards the left
        }

        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined); // Link spritBatch to camera
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 0f); // Clear screen with black color

        game.spriteBatch.begin();
        if (startMode) {
            camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0);
            camera.zoom = 0.7f;
            drawBackground(false, deltaTime); // Draw scrolling background animation
            drawTiles();
            drawLogoAnimation(logoAnimationTime, false);
            drawIdleAnimation(); // Draw the character idle animation if in start mode
        } else {
            // Zoom out camera smootly to game mode zoom position in slow speed
            smoothZoom(newZoomLevel, zoomSpeed, deltaTime);

            // IF CHARACTER IS ALIVE
            if (!isDead) {
                drawBackground(true, deltaTime); // Draw scrolling background animation
                drawTiles();
                if (!logoAnimationFinished){
                    System.out.println("HELLO" + logoAnimationFinished );
                    drawLogoAnimation(logoAnimationTime, true);
                }
                drawRunOrJump();// Draw running or jumping animation depending on character state

            // IF CHARACTER IS DEAD
            } else {
                smoothZoom(newZoomLevel, 0.5f, deltaTime);
                drawBackground(false, deltaTime); // Draw last state of background
                drawTiles(); // Draw last state of the tiles
                drawDeathAnimation();
            }
        }
        game.spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Adapt viewport after window size
        camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        backgroundImage.dispose();
        tile.dispose();
        characterAtlas.dispose();
        logoAtlas.dispose();
    }

    /** Prepares the initial tiles for rendering. */
    private void prepareInitialTiles() {
        float initialTileX = -350; // Sets the initial x-position of the left-most tile
        for (int i = 0; i < 15; i++) { // for-loop to generate the x- and y-positions of the first 15 tiles
            tileXPositions.add(initialTileX); // Adds the x-position to the tileXPositions array
            tileYPositions.add(100f); // Adds the y-position to the tileXPositions array
            initialTileX += 100; // Sets the x-position of the next tile in the loop
        }
    }

    /** Applies physics so that the character falls according to gravity. */
    private void applyGravity(float deltaTime) {

        float gravity = 980f; // Acceleration of gravity (pixels/s²)

        verticalVelocity -= gravity * deltaTime; // Decrease or increase velocity according to gravity over time (pixels/s)
        characterYPosition += verticalVelocity * deltaTime; // Change character position up or down based on velocity and how much time has passed
    }

    /** Checks if the character has collided with a tile or hit the bottom.
     * When collision is detected it will reset the velocity to 0 and stop
     * the jumping animation. It will also initiate the death animation if the bottom has been reached. */
    private void checkCollision() {

        // If character is going down check for potential tile collision
        if (verticalVelocity <= 0) {

            // Set position of rectangle representing the character
            characterRectangle.setPosition(characterXPosition, characterYPosition);

            // Loop through all current tiles
            for (int i = 0; i < tileXPositions.size; i++) {
                float tileX = tileXPositions.get(i); // Temporarily store x position of tile
                float tileY = tileYPositions.get(i); // Temporarily store y position of tile

                // Set position of rectangle representing the tile
                tileRectangle.setPosition(tileX, tileY);

                // Checks if the character overlaps with any tile that is under the character
                if (characterRectangle.overlaps(tileRectangle) && characterRectangle.y >= tileRectangle.y) {
                    characterYPosition = tileY + tileHeight; // Put character on top of the tile
                    isJumping = false; // Flag to stop the jumping animation
                    verticalVelocity = 0; // Set velocity to 0 to stop character from falling
                    break;
                }
            }
        }
        // Logic that checks if character touches the ground
        if (characterYPosition <= 0) {
            characterYPosition = 0;  // Set character position firmly to 0 to ensure it's not set beyond the floor
            isDead = true; // Change flag to initiate death animation
            characterAnimationTime = 0; // Reset animation time so death animation starts at first animation frame
            verticalVelocity = 0; // Set velocity to 0 to stop character from falling
            isJumping = false; // Stop jumping animation instantly
        }
    }

    /** Prepares a buffer of tiles for rendering and removes tiles that have moved off-screen. */
    private void generateBufferTiles() {
        float screenRightEdge = Gdx.graphics.getWidth(); // Gets the x-position of the right edge of the screen
        float lastTileXPosition = tileXPositions.peek(); // Gets the x-position of latest tile
        float lastTileYPosition = tileYPositions.peek(); // Gets the y-position of latest tile

        // Loop that prepares a buffer of tiles to be rendered up to 500 pixels beyond the edge of the screen
        while (lastTileXPosition < screenRightEdge + 500) {

            // Calculate random new x-position by adding a minimum distance with a random offset within the specified range
            float randomDistance = minTileDistance + random.nextFloat() * (maxTileDistance - minTileDistance);

            // Set new x-position
            float newXPosition = lastTileXPosition + randomDistance;

            // Set new y-position within allowed vertical limits
            float newYPosition = getNewTileHeight(maxTileHeight, lastTileYPosition);

            // Add latest tile X- and Y-coordinates to their respective array
            tileXPositions.add(newXPosition);
            tileYPositions.add(newYPosition);

            // Update lastTileX to store the x-position of the recently added tile
            lastTileXPosition = newXPosition;
        }

        // Remove a tile from the arrays if it has moved beyond the left edge of the screen
        while (tileXPositions.first() + tileWidth < 0 && !tileXPositions.isEmpty()) {
            tileXPositions.removeIndex(0);
            tileYPositions.removeIndex(0);
        }
    }

    /** Generates a new y-position that is within the given vertical limits for a tile.*/
    private float getNewTileHeight(float maxTileHeight, float lastTileYPosition) {

        float randomY;
        float verticalDistance;
        do {
            randomY = random.nextFloat() * maxTileHeight; // Generate a random float between 0.0 and maxTileHeight
            verticalDistance = abs(randomY - lastTileYPosition); // Get the absolute difference of distance from the last tile
        }
        // Loop until the vertical distance is within the specified range
        while (verticalDistance < minVerticalDistance || verticalDistance > maxVerticalDistance);
        return randomY;
    }

    /** Updates x-positions of all tiles, including buffer tiles */
    private void moveTiles(float deltaTime) {
        if (!isDead) {  // Only update tiles if the character is not dead
            // for-loop that iterates through the x-position of all active and buffer tiles
            for (int i = 0; i < tileXPositions.size; i++) {
                float updatedXPosition = tileXPositions.get(i) - backgroundSpeed * deltaTime;
                tileXPositions.set(i, updatedXPosition);
            }
        }
    }

    /** Draws the background. Enable or disable moving background using the shouldMove parameter.
     * @param shouldMove If true the background will move, or else it will stay static. */
    private void drawBackground(boolean shouldMove, float deltaTime){

        if (shouldMove) {
            // Update background positions for endless scrolling of background
            bg1XPosition -= backgroundSpeed * deltaTime;
            bg2XPosition -= backgroundSpeed * deltaTime;

            // Reset background positions when they reach the edge
            if (bg1XPosition + backgroundImage.getWidth() <= 0) { // If background 1 is fully off the screen...
                bg1XPosition = bg2XPosition + backgroundImage.getWidth(); // Set position of background 1 to the right of bg2
            }
            if (bg2XPosition + backgroundImage.getWidth() <= 0) { // If background 2 is fully off the screen...
                bg2XPosition = bg1XPosition + backgroundImage.getWidth(); // Set position of background 2 to the right of bg1
            }
        }

        // Draw background images
        game.spriteBatch.draw(backgroundImage, bg1XPosition, 0);
        game.spriteBatch.draw(backgroundImage, bg2XPosition, 0);
    }

    /** Draw all current tiles. */
    private void drawTiles(){
        for (int i = 0; i < tileXPositions.size; i++) {
            game.spriteBatch.draw(tile, tileXPositions.get(i), tileYPositions.get(i), tileWidth, tileHeight);
        }
    }
    /** Draw idle animation for the start screen. */
    private void drawIdleAnimation() {
        TextureRegion atlasFrame;
        atlasFrame = idleAnimation.getKeyFrame(characterAnimationTime, true); // Looping

        // Draw the current frame
        game.spriteBatch.draw(atlasFrame, characterXPosition - characterWidth / 2f, characterYPosition, characterWidth, characterHeight);
    }

    /**
     * Draws the main logo animation.
     * @param logoAnimationTime State time of the animation.
     * @param endAnimation Flag to control if logo animation should end.
     */
    public void drawLogoAnimation(float logoAnimationTime, boolean endAnimation) {
        TextureRegion atlasFrame; // Will store the frame to be drawn

        // When set to end, the animation is marked as finished when it has returned to the starting frame
        if (endAnimation && logoAnimation.getKeyFrameIndex(logoAnimationTime) == 0) {
            logoAnimationFinished = true;
        }

        // Get the current frame for the animation
        atlasFrame = logoAnimation.getKeyFrame(logoAnimationTime, true);

        // Draw the current frame
        game.spriteBatch.draw(atlasFrame, Main.WORLD_WIDTH / 2f - 250, Main.WORLD_HEIGHT - 300, 500, 109);
    }

    /** Draw run or jump animation depending on character state. */
    private void drawRunOrJump(){
        Animation<TextureRegion> runOrJumpAnimation = isJumping ? jumpingAnimation : runningAnimation;
        TextureRegion runOrJumpFrame;

        // Prepare jumping animation by entering normal animation mode and reset animationTime to 0
        if (isJumping && runOrJumpAnimation.getPlayMode() != Animation.PlayMode.NORMAL) {
            runOrJumpAnimation.setPlayMode(Animation.PlayMode.NORMAL);
            characterAnimationTime = 0;

        // Prepare run animation by entering loop animation mode
        } else if (!isJumping && runOrJumpAnimation.getPlayMode() != Animation.PlayMode.LOOP) {
            runOrJumpAnimation.setPlayMode(Animation.PlayMode.LOOP);
        }

        // Draw run or jump animation depending on character state
        if (isJumping) {
            runOrJumpFrame = runOrJumpAnimation.getKeyFrame(characterAnimationTime, false); // Looping off
        }else {
            runOrJumpFrame = runOrJumpAnimation.getKeyFrame(characterAnimationTime, true); // Looping on
        }

        // Draw the current frame
        game.spriteBatch.draw(runOrJumpFrame, characterXPosition - characterWidth / 2f, characterYPosition, characterWidth, characterHeight);
    }

    /** Draw death animation of character. */
    private void drawDeathAnimation(){
        TextureRegion atlasFrame;
        atlasFrame = deathAnimation.getKeyFrame(characterAnimationTime, false);
        // Draw the current frame
        game.spriteBatch.draw(atlasFrame, characterXPosition - characterWidth / 2f, characterYPosition, characterWidth, characterHeight);
    }

    /** Creates an animation from a TextureRegion array.
     * @param textureRegions Array of TextureRegions that together will become the Animation object
     * @param fileBaseName Base name of the Texture Regions
     * @param frameDuration Adjusts how long each frame of the animation should be shown e.g. 1/30
     */
    private Animation<TextureRegion> createCharacterAnimation(TextureRegion[] textureRegions, String fileBaseName, float frameDuration){

        int amountOfTextures = textureRegions.length;

        // Save all TextureRegions in an array
        for (int i = 0; i < amountOfTextures; i++) {
            String frameName = String.format(fileBaseName + i);
            textureRegions[i] = characterAtlas.findRegion(frameName);
        }

        // Create and return animation object
        return new Animation<TextureRegion>(frameDuration, textureRegions);
    }

    /** Adjust the zoom of the camera to a given zoom position.
     * @param newZoomLevel Target zoom level.
     * @param zoomSpeed Speed of zoom. Set to a number between 0.01-1.0f.
     */
    private void smoothZoom(float newZoomLevel, float zoomSpeed, float deltaTime){

        // Flags to determine zoom direction, both becomes to false when target zoom is reached
        boolean zoomIn = newZoomLevel < camera.zoom;
        boolean zoomOut = newZoomLevel > camera.zoom;

        // ZOOM IN INCREMENTALLY TO GIVEN FINAL ZOOM POSITION
        if (zoomIn) {
            camera.zoom -= zoomSpeed * deltaTime; // Zoom in closer to target level

            // Readjust zoom to target if it's set below target level
            if (camera.zoom < newZoomLevel){
                camera.zoom = newZoomLevel;
            }

            camera.update();
        }

        // ZOOM OUT INCREMENTALLY TO GIVEN FINAL ZOOM POSITION
        if (zoomOut) {
            camera.zoom += zoomSpeed * deltaTime; // Zoom out towards target level

            // Readjust zoom to target if it's set above target level
            if (camera.zoom > newZoomLevel){
                camera.zoom = newZoomLevel;
            }

            camera.update();
        }
    }
}
