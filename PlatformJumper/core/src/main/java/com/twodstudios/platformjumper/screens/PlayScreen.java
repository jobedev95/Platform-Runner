package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.twodstudios.platformjumper.*;



public class PlayScreen implements Screen, ResetListener {

    private Main game;

    public SpriteBatch spriteBatch;

    private Player player;
    private Tiles tiles;
    private SoundManager soundManager;
    private ScoreManager scoreManager;
    private PhysicsManager physicsManager;
    private CoinManager coinManager;
    private SharedAssets sharedAssets;
    private EffectsManager effectsManager;
    private GameOverState gameOverState;

    private BitmapFont font;

    // Background variables
    private Background background;
    private float backgroundSpeed = 300f; // Background movement speed

    // Camera and Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    // Start Mode variables
    private boolean startMode = true; // Flag to initiate the start screen
    private float initialCameraZoom = 5.0f; // Initial zoom for camera in start mode
    private float newZoomLevel = 1f; // Game Mode zoom level
    private float zoomSpeed = 0.05f; // Camera zoom speed

    // Pause state
    private boolean paused = false;

    // Constructor
    public PlayScreen(Main game){
        this.game = game;
        this.spriteBatch = game.spriteBatch;
        this.sharedAssets = game.sharedAssets;
    }

    @Override
    public void show() {
        // Creating bitmap font object
        font = new BitmapFont();

        // Initialise all necessary objects for the game
        player = new Player(this.spriteBatch, 120, 150, Main.WORLD_WIDTH / 2, 135f);
        tiles = new Tiles(this.spriteBatch, backgroundSpeed);
        coinManager = new CoinManager(this.spriteBatch, tiles, backgroundSpeed);
        soundManager = new SoundManager();
        scoreManager = new ScoreManager();
        gameOverState = new GameOverState(this, scoreManager);
        physicsManager = new PhysicsManager(player, tiles, soundManager, coinManager.getCoins(), scoreManager);
        effectsManager = new EffectsManager(this.spriteBatch);

        // Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0); // Center the camera
        camera.position.set(player.getXPosition(), player.getYPosition(), 0); // Center the camera
        camera.zoom = initialCameraZoom; // Start with camera zoomed in
        camera.update();

        background = new Background( "atlas/lava_theme.atlas", backgroundSpeed, 6, game);
    }

    @Override
    public void render(float delta) {

        float deltaTime = Gdx.graphics.getDeltaTime(); // Gets time lapsed since last frame

        // Toggle pause state if "p" is pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }

        // If game is paused show pause screen and stop game logic
        if (paused) {
            drawPausedScreen(deltaTime);
            return;
        }

        // Update animation times
        player.updateAnimationTime(deltaTime);
        sharedAssets.updateMainLogoAnimationTime(deltaTime);
        coinManager.updateAnimationTime(deltaTime);

        // Exits start mode when Enter is pressed
        if (startMode && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startMode = false;
            soundManager.backgroundMusic();
        }

        if (!startMode && !player.isDead()) {
            // If space-bar is pressed or mouse is clicked and the character is not already in a jumping state increase velocity
            if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isTouched()) && !player.isJumping()) {
                player.startJump();
            }

            physicsManager.applyGravity(deltaTime); // Enable gravity
            physicsManager.checkCollision(); // Check for tile and floor collisions
            tiles.generateBufferTiles(); // Prepares a buffer of tiles for rendering
            coinManager.generateCoins(deltaTime); // Prepares coins for rendering
            tiles.moveTiles(deltaTime);// Continuously moves all tiles towards the left
            coinManager.moveCoins(deltaTime); // Continuously moves all coins towards the left
        }

        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined); // Link spriteBatch to camera
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 0f); // Clear screen with black color

        game.spriteBatch.begin();
        if (startMode) {
            camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0);
            camera.zoom = 0.7f;
            background.drawBackgroundSet(false, deltaTime);  // Draw first state of background
            background.drawGround(false, deltaTime);  // Draw first state of dangerous ground
            tiles.drawTiles(); // Draw initial tiles
            sharedAssets.drawLogoAnimation(false);
            player.drawIdleAnimation(); // Draw the character idle animation if in start mode
            effectsManager.drawSparkles(deltaTime);// Draw continous particle effect
        } else {
            // Zoom out camera smoothly to the game mode zoom position in slow speed
            smoothZoom(newZoomLevel, zoomSpeed, deltaTime);

            // IF CHARACTER IS ALIVE
            if (!player.isDead()) {
                background.drawBackgroundSet(true, deltaTime);
                background.drawGround(true, deltaTime);
                tiles.drawTiles();
                coinManager.drawCoins();

                if (!sharedAssets.isLogoAnimationFinished()) {
                    sharedAssets.drawLogoAnimation(true);
                }
                player.drawRunOrJump(); // Draw running or jumping animation depending on character state

                effectsManager.drawSparkles(deltaTime); // Draw continous particle effect

            // IF CHARACTER IS DEAD
            } else {
                smoothZoom(newZoomLevel, 0.5f, deltaTime);
                background.drawBackgroundSet(false, deltaTime);  // Draw last state of background
                background.drawGround(false, deltaTime);  // Draw last state of ground
                tiles.drawTiles(); // Draw last state of the tiles
                coinManager.drawCoins(); // Draw last state of the coins
                player.drawDeathAnimation();

                float deathXPosition = player.getXPosition() - player.getWidth() / 5f;
                float deathYPosition = background.getGroundHeight() / 2f;
                effectsManager.drawLavaExplosion(deltaTime, deathXPosition, deathYPosition); // Draw lava explosion effect

                if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                    resetGame();
                }
            }

            // Render score
            BitmapFont font = new BitmapFont();
            font.draw(game.spriteBatch, "Score: " + scoreManager.getScore(), 10, Gdx.graphics.getHeight() - 10);
        }
        game.spriteBatch.end();

        if (player.isDead()){
            gameOverState.render();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Adapt viewport after window size
        camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0);
        gameOverState.resize(width, height);
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
        player.dispose();
        sharedAssets.dispose();
        tiles.dispose();
        font.dispose();
        soundManager.dispose();
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

    /** Reset the game. */
    @Override
    public void resetGame() {
        player.reset();
        tiles.reset(); // Reset tiles to prepare for new game
        scoreManager.reset(); // Reset score
        coinManager.reset(); // Reset all coins
        soundManager.setGameOverSoundPlayed(false);// Reset sound play flag
        sharedAssets.setLogoAnimationTime(0); // Reset logo animation
        effectsManager.resetLavaExplosion(); // Reset lava particle effect
        sharedAssets.reset();
        gameOverState.reset();
        startMode = true; // Set flag to show start mode again
    }

    /** Draw "Paused" on screen when p is pressed and return to normal once pressed again. */
    private void drawPausedScreen(float deltaTime) {
        game.spriteBatch.begin();

        // Draw darkened background
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 0f); // Clear screen with black color
        game.spriteBatch.setColor(1f, 1f, 1f, 0.7f); // Set opacity to 70%
        background.drawBackgroundSet(false, deltaTime);
        game.spriteBatch.setColor(1f, 1f, 1f, 1f); // Reset opacity to 100%

        // Print "Paused" on screen
        font.draw(game.spriteBatch, "Paused", Main.WORLD_WIDTH / 2f - 25, Main.WORLD_HEIGHT / 2f);
        game.spriteBatch.end();
    }
}
