package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.tommyettinger.textra.Font;
import com.twodstudios.platformjumper.*;
import com.github.tommyettinger.textra.TypingLabel;



public class PlayScreen implements Screen, HudListener, GameOverListener {

    private final Main game;
    private StartMenuScreen startMenuScreen;

    public SpriteBatch spriteBatch;
    private Player player;
    private Tiles tiles;
    private Hud hud;
    private CoinManager coinManager;
    private SoundManager soundManager;
    private ScoreManager scoreManager;
    private PhysicsManager physicsManager;
    private final SharedAssets sharedAssets;
    private EffectsManager effectsManager;
    private GameOverHud gameOverHud;
    private PauseState pauseState;

    // "Press ENTER to start" label
    private TypingLabel enterMessageLabel;

    // Background variables
    private Background background;
    private final float backgroundSpeed = 300f; // Background movement speed

    // Camera and Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    // Start Mode variables (for smooth zoom out camera movement)
    private boolean startMode = true; // Flag to initiate the start screen
    private final float initialCameraZoom = 0.7f; // Initial zoom for camera in start mode
    private final float newZoomLevel = 1f; // Game Mode zoom level
    private final float zoomSpeed = 0.05f; // Camera zoom speed


    // Constructor
    public PlayScreen(Main game, StartMenuScreen startMenuScreen){
        this.game = game;
        this.startMenuScreen = startMenuScreen;
        this.spriteBatch = game.spriteBatch;
        this.sharedAssets = game.sharedAssets;
        this.hud = new Hud(this);
    }

    // Stage and table
    private Stage stage;
    private Table table;
    private Skin skin;


    @Override
    public void show() {

        // Initialise all necessary objects for the game
        background = new Background( "atlas/lava_theme.atlas", backgroundSpeed, 6, game);
        tiles = new Tiles(this.spriteBatch, backgroundSpeed);
        coinManager = new CoinManager(this.spriteBatch, tiles, backgroundSpeed);
        player = new Player(this.spriteBatch, 120, 150, Main.WORLD_WIDTH / 2, 135f);
        soundManager = new SoundManager();
        scoreManager = new ScoreManager();
        gameOverHud = new GameOverHud(this, scoreManager);
        physicsManager = new PhysicsManager(player, tiles, soundManager, coinManager.getCoins(), scoreManager);
        effectsManager = new EffectsManager(this.spriteBatch);
        pauseState = new PauseState(game, startMenuScreen);

        // Create "Enter to start" message
        createEnterToStartLabel();

        // Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0); // Center the camera
        camera.update();

        // Play background music
        soundManager.backgroundMusic();
    }

    @Override
    public void render(float deltaTime) {

        // Toggle Pause state with "P"
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) && !player.isDead()) {
            pauseState.togglePause();

            // Set input processor to pause state if paused
            if (pauseState.isPaused()) {
                Gdx.input.setInputProcessor(pauseState.getStage());
            }
        }

        // If game is paused, render pause menu and return
        if (pauseState.isPaused()) {
            drawTransparentPauseBackground(deltaTime);
            pauseState.render();
            return;
        }

        // Update animation times
        player.updateAnimationTime(deltaTime);
        sharedAssets.updateMainLogoAnimationTime(deltaTime);
        coinManager.updateAnimationTime(deltaTime);

        // Exits start mode when Enter is pressed
        if (startMode && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startMode = false;
        }

        if (!startMode && !player.isDead()) {

            // Set HUD as input processor
            Gdx.input.setInputProcessor(hud.getStage());

            // If space bar is pressed or mouse is clicked and the character is not already in a jumping state, increase velocity
            if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !player.isJumping())){
                player.startJump();
            }

            physicsManager.applyGravity(deltaTime); // Enable gravity
            physicsManager.checkCollision(); // Check for tile and floor collisions
            hud.setScore(scoreManager.getScore()); // Update score in HUD
            tiles.generateBufferTiles(); // Prepare a buffer of tiles for rendering
            coinManager.generateCoins(deltaTime); // Prepare coins for rendering
            tiles.moveTiles(deltaTime); // Continuously move all tiles towards the left
            coinManager.moveCoins(deltaTime); // Continuously move all coins towards the left
        }

        camera.update();
        game.spriteBatch.setProjectionMatrix(camera.combined); // Link spriteBatch to camera
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 0f); // Clear screen with black color

        game.spriteBatch.begin();
        if (startMode) {
            camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0); // Center camera
            camera.zoom = initialCameraZoom; // Set zoom to initial zoom level
            background.drawBackgroundSet(false, deltaTime);  // Draw first state of background
            background.drawGround(false, deltaTime);  // Draw first state of dangerous ground
            tiles.drawTiles(); // Draw initial tiles
            sharedAssets.drawLogoAnimation(500, 109, 300, false);
            player.drawIdleAnimation(); // Draw the character idle animation if in start mode
            effectsManager.drawSparkles(deltaTime); // Draw continous particle sparkles effect
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
                    sharedAssets.drawLogoAnimation(500, 109, 300, true);
                }
                player.drawRunOrJump(); // Draw running or jumping animation depending on character state

                effectsManager.drawSparkles(deltaTime); // Draw continous particle effect

            // IF CHARACTER IS DEAD
            } else {
                smoothZoom(newZoomLevel, 0.5f, deltaTime); // Zoom out quickly if player dies super early in the game
                background.drawBackgroundSet(false, deltaTime);  // Draw last state of background
                background.drawGround(false, deltaTime);  // Draw last state of ground
                tiles.drawTiles(); // Draw last state of the tiles
                coinManager.drawCoins(); // Draw last state of the coins
                player.drawDeathAnimation();

                // Draw lava explosion at death spot
                float deathXPosition = player.getXPosition() - player.getWidth() / 5f;
                float deathYPosition = background.getGroundHeight() / 2f;
                effectsManager.drawLavaExplosion(deltaTime, deathXPosition, deathYPosition); // Draw lava explosion effect

                // If up-button is pressed, reset the game
                if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                    resetGame();
                }
            }
        }
        game.spriteBatch.end();

        if (startMode) {
            // Draw animated "Press enter to start" label
            stage.act();
            stage.draw();
        }else {
            hud.render(deltaTime); // Draw HUD (Coin score tracker and pause button)
        }

        // Draw game over HUD if player dies
        if (player.isDead()){
            Gdx.input.setInputProcessor(gameOverHud.getStage());
            gameOverHud.render(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Adapt viewport after window size
        hud.resize(width, height); // Adapt HUD after window size
        gameOverHud.resize(width, height); // Adapt game over HUD after window size
        camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0);  // Center camera
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
        soundManager.stopBackgroundMusic();
    }

    @Override
    public void dispose() {
        player.dispose();
        sharedAssets.dispose();
        tiles.dispose();
        soundManager.dispose();
        effectsManager.dispose();
        stage.dispose();
    }

    /** Adjust the zoom of the camera to a given zoom position.
     * @param newZoomLevel Target zoom level.
     * @param zoomSpeed Speed of zoom. Set to a number between 0.01-1.0f.
     */
    private void smoothZoom(float newZoomLevel, float zoomSpeed, float deltaTime){

        // Flags to determine zoom direction, both becomes false when target zoom is reached
        boolean zoomIn = newZoomLevel < camera.zoom;
        boolean zoomOut = newZoomLevel > camera.zoom;

        // ZOOM IN INCREMENTALLY TO GIVEN FINAL ZOOM POSITION
        if (zoomIn) {
            camera.zoom -= zoomSpeed * deltaTime; // Zoom in closer to target level

            // Re-adjust zoom to target zoom level if it's set below target level
            if (camera.zoom < newZoomLevel){
                camera.zoom = newZoomLevel;
            }

            camera.update();
        }

        // ZOOM OUT INCREMENTALLY TO GIVEN FINAL ZOOM POSITION
        if (zoomOut) {
            camera.zoom += zoomSpeed * deltaTime; // Zoom out towards target level

            // Re-adjust zoom to target zoom level if it's set above target level
            if (camera.zoom > newZoomLevel){
                camera.zoom = newZoomLevel;
            }

            camera.update();
        }
    }

    /** Draw transparent pause menu background. */
    private void drawTransparentPauseBackground(float deltaTime) {
        game.spriteBatch.begin();
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 0.0f); // Clear screen with black color
        game.spriteBatch.setColor(1f, 1f, 1f, 0.7f); // Set opacity to 70%
        background.drawBackgroundSet(false, deltaTime); // Draw background with 70% opacity
        game.spriteBatch.setColor(1f, 1f, 1f, 1f); // Reset opacity to normal
        game.spriteBatch.end();
    }

    /** Create "Press ENTER to start"-label. */
    private void createEnterToStartLabel(){

        // Load skin which includes the font
        skin = new Skin(Gdx.files.internal("skins/game_over_skin.json"));

        // Create a font family (because TypingLabel does not support skins)
        Font.FontFamily fontFamily = new Font.FontFamily(skin);

        // Create "Press ENTER to start" message
        enterMessageLabel = new TypingLabel("{FADE}Press {GRADIENT=ffffffff;90ffa7ff;1.0;3.6}{WAVE=0.5;1.0;1.0}ENTER{ENDWAVE}{ENDGRADIENT} to start...{ENDFADE}", fontFamily.connected[0]);

        // Create stage and table
        stage = new Stage();
        table = new Table();
        table.setFillParent(true);

        // Add message to table, and table to stage
        table.add(enterMessageLabel)
            .center()
            .pad(10, 300, 10, 300);
        stage.addActor(table);
    }

    private <T extends Resettable<T>> void resetObject(T object) {
        object.reset();
    }

    /** Reset the game. */
    @Override
    public void resetGame() {

        resetObject(player); // Reset player velocity, position and states
        resetObject(tiles); // Reset tiles to prepare for new game
        resetObject(scoreManager); // Reset score
        resetObject(coinManager); // Reset all coins
        resetObject(soundManager); // Reset sound play flag
        resetObject(effectsManager); // Reset lava particle effect
        resetObject(gameOverHud); // Reset Game Over HUD
        resetObject(sharedAssets);
        startMode = true; // Set flag to show start mode again
    }

    @Override
    public void pauseGame() {
        pauseState.togglePause();
    }
}
