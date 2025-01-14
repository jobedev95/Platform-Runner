package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twodstudios.platformjumper.Main;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import static com.badlogic.gdx.math.MathUtils.random;
import static java.lang.Math.abs;

public class PlayScreen implements Screen {
    private Main game;

    // CREATE ALL ASSET VARIABLES
    private Texture backgroundImage;
    private Texture tile;
    private Texture[] runAnimation;
    private Texture[] jumpAnimation;
    private Texture[] deathAnimation;
    private Texture[] idleAnimation;
    private Texture[] gameLogoAnimation;

    private float animationTime; // Time since start of animation
    private float logoAnimationTime; // Time since start of animation
    private int currentFrame; // Index for character animation frames
    private int currentDeathFrame; // Index of death animation frames
    private int currentLogoFrame= 0; // Index for game logo animation frames
    private int gameLogoSize;
    private int characterWidth;
    private int characterHeight;
    private int tileWidth;
    private int tileHeight;
    private boolean isJumping; // Flag to check if the character is jumping
    private boolean isDead; // Flag to check if the character is dead
    private float bg1XPosition, bg2XPosition; // X-positions of the two looping backgrounds
    private float backgroundSpeed = 300f; // Background movement speed
    private float verticalVelocity = 0f;
    private float characterXPosition;
    private float characterYPosition;  // y-position of the character

    // TILE VARIABLES
    private Array<Float> tileXPositions;  // Dynamic array for x-coordinates of tiles
    private Array<Float> tileYPositions;  // Dynamic array for y-coordinates of tiles
    private float minTileDistance = 300; // Minimum horizontal distance between each tile
    private float maxTileDistance = 630; // Maximum horizontal distance between each tile
    private float minVerticalDistance = 130;  // Minimum vertical distance (height difference) between each tile
    private float maxVerticalDistance = 200;   // Maximum vertical distance (height difference) between each tile
    private float maxTileHeight; // Maximum y-coordinate for any tile

    // COLLISION VARIABLES
    Rectangle characterRectangle;
    Rectangle tileRectangle;

    // Camera and Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    // Start Mode logic
    private boolean startMode = true; // Flag to initiate the start screen
    private float initialCameraZoom = 1.0f; // Initial zoom for camera in start mode
    private boolean zoomingOut = false;
    private float targetZoom = 1f; // Game Mode zoom level
    private float zoomSpeed = 0.05f;
    BitmapFont startScreenFont;

    // Constructor
    public PlayScreen(Main game){
        this.game = game;
    }

    @Override
    public void show() {
        // Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        camera.setToOrtho(false, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        camera.position.set(characterXPosition, characterYPosition, 0); // Center the camera
        camera.zoom = initialCameraZoom; // Start with camera zoomed in
        camera.update();

        backgroundImage = new Texture("gameBG.png");
        tile = new Texture("tile.png");

        gameLogoAnimation = new Texture[66];
        for (int i = 0; i < gameLogoAnimation.length; i++) {
            String fileName = String.format("platform_runner_logo/platform_runner_logo%05d.png", i);
            System.out.println(fileName);
            gameLogoAnimation[i] = new Texture(fileName);
        }

        idleAnimation = new Texture[10];
        for (int i = 0; i < idleAnimation.length; i++) {
            idleAnimation[i] = new Texture("Idle__00" + i + ".png");
        }

        runAnimation = new Texture[10];
        for (int i = 0; i < runAnimation.length; i++) {
            runAnimation[i] = new Texture("Run__00" + i + ".png");
        }

        jumpAnimation = new Texture[10];
        for (int i = 0; i < jumpAnimation.length; i++) {
            jumpAnimation[i] = new Texture("Jump__00" + i + ".png");
        }

        deathAnimation = new Texture[10];
        for (int i = 0; i < deathAnimation.length; i++) {
            deathAnimation[i] = new Texture("Dead__00" + i + ".png");
        }

        animationTime = 0f;
        logoAnimationTime = 0f;
        currentFrame = 0;
        currentDeathFrame = 0;
        gameLogoSize = 500;
        characterWidth = 120;
        characterHeight = 150;
        characterXPosition = viewport.getWorldWidth() / 2;
        characterYPosition = 130f; // Initial y-position of the character
        isDead = false;
        isJumping = false;
        bg1XPosition = 0;
        bg2XPosition = -backgroundImage.getWidth();
        startScreenFont = new BitmapFont();

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
        animationTime += deltaTime; // Update animation time
        logoAnimationTime += deltaTime;

        // Exits start mode when Enter is pressed
        if (startMode && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startMode = false;
            currentLogoFrame = gameLogoAnimation.length - 1;
            zoomingOut = true;
        }

        if (!startMode) {
            // If space-bar is pressed or mouse is clicked and the character is not already in a jumping state increase velocity
            if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) && !isJumping) {
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
            drawLogoAnimation(false);
            //startScreenFont.draw(game.spriteBatch, "Press enter to start", Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2);
            drawIdleAnimation(); // Draw the character idle animation if in start mode
        } else {

            if (zoomingOut && camera.zoom < targetZoom) {
                camera.zoom += zoomSpeed * deltaTime;
                camera.update();
            }
            else {
                camera.zoom = targetZoom; // Ensure we don't go over the target zoom
                camera.update();
                zoomingOut = false; // Stop zooming once we reach the target
            }
            // IF CHARACTER IS ALIVE
            if (!isDead) {
                drawBackground(true, deltaTime); // Draw scrolling background animation
                drawTiles();
                System.out.println(currentFrame);
                if (currentLogoFrame != 0) {
                    drawLogoAnimation(true);
                }
                drawRunOrJump();// Draw running or jumping animation depending on character state


                // IF CHARACTER IS DEAD
            } else {
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
        disposeAnimationTextures(idleAnimation);
        disposeAnimationTextures(runAnimation);
        disposeAnimationTextures(jumpAnimation);
        disposeAnimationTextures(deathAnimation);
        disposeAnimationTextures(gameLogoAnimation);
        startScreenFont.dispose();
    }

    private void disposeAnimationTextures(Texture[] textures){
        for (Texture texture : textures) {
            texture.dispose();
        }
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
        if (animationTime >= 0.13f) { // Control the frame rate of idle animation
            currentFrame = (currentFrame + 1) % idleAnimation.length;
            animationTime = 0f;
        }
        game.spriteBatch.draw(idleAnimation[currentFrame], characterXPosition - characterWidth / 2f, characterYPosition, characterWidth, characterHeight);
    }

    /** Draw idle animation for the start screen. */
    private void drawLogoAnimation(boolean drawReversed) {
        if (!drawReversed){
            if (logoAnimationTime >= 0.03f) { // Control the frame rate of idle animation
                currentLogoFrame = (currentLogoFrame + 1) % gameLogoAnimation.length;
                logoAnimationTime = 0f;
            }
        }else{
            if (logoAnimationTime >= 0.03f) {
                currentLogoFrame = (currentLogoFrame - 1 + gameLogoAnimation.length) % gameLogoAnimation.length;
                logoAnimationTime = 0f;
            }
        }
        game.spriteBatch.draw(gameLogoAnimation[currentLogoFrame], Main.WORLD_WIDTH / 2f - (gameLogoSize / 2f), Main.WORLD_HEIGHT - gameLogoSize, gameLogoSize, gameLogoSize);
    }

    /** Draw run or jump animation depending on character state. */
    private void drawRunOrJump(){
        Texture[] runOrJumpAnimation = isJumping ? jumpAnimation : runAnimation;
        if (animationTime >= 0.1f) { // Update animation frame every 0.1 seconds
            currentFrame = (currentFrame + 1) % runOrJumpAnimation.length;
            animationTime = 0f; // Reset animation time
        }
        game.spriteBatch.draw(runOrJumpAnimation[currentFrame], characterXPosition - characterWidth / 2f, characterYPosition, characterWidth, characterHeight);
    }

    /** Draw death animation of character. */
    private void drawDeathAnimation(){
        // Draw death animation and end it at the last frame (frame 9)
        if (animationTime >= 0.03f && currentDeathFrame < 9) { // Update animation frame every 0.03 seconds
            currentDeathFrame++; // Set animation to next death frame
            animationTime = 0f; // Reset animation time
        }
        game.spriteBatch.draw(deathAnimation[currentDeathFrame], characterXPosition - characterWidth / 2f, 0, characterWidth, characterHeight);
    }
}
