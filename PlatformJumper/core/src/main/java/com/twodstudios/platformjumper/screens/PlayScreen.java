package com.twodstudios.platformjumper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.tommyettinger.textra.Font;
import com.twodstudios.platformjumper.*;
import com.github.tommyettinger.textra.TypingLabel;
import com.github.tommyettinger.textra.KnownFonts;



public class PlayScreen implements Screen, HudListener, GameOverListener {

    private final Main game;
    private Hud hud;

    public SpriteBatch spriteBatch;

    private Player player;
    private Tiles tiles;
    private SoundManager soundManager;
    private ScoreManager scoreManager;
    private PhysicsManager physicsManager;
    private CoinManager coinManager;
    private final SharedAssets sharedAssets;
    private EffectsManager effectsManager;
    private GameOverState gameOverState;
    private PauseState pauseState;

    // Fonts
    private TypingLabel enterMessageLabel;
    private BitmapFont font;

    // Background variables
    private Background background;
    private final float backgroundSpeed = 300f; // Background movement speed

    // Camera and Viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    // Start Mode variables
    private boolean startMode = true; // Flag to initiate the start screen
    private final float initialCameraZoom = 5.0f; // Initial zoom for camera in start mode
    private final float newZoomLevel = 1f; // Game Mode zoom level
    private final float zoomSpeed = 0.05f; // Camera zoom speed


    // Constructor
    public PlayScreen(Main game){

        this.game = game;
        this.spriteBatch = game.spriteBatch;
        this.sharedAssets = game.sharedAssets;
        this.hud = new Hud(this);
    }
    // fields for stage and table
    private Stage stage;
    private Table table;
    private Skin skin;


    @Override
    public void show() {

        // Initialise all necessary objects for the game
        player = new Player(this.spriteBatch, 120, 150, Main.WORLD_WIDTH / 2, 135f);
        tiles = new Tiles(this.spriteBatch, backgroundSpeed);
        coinManager = new CoinManager(this.spriteBatch, tiles, backgroundSpeed);
        soundManager = new SoundManager();
        scoreManager = new ScoreManager();
        gameOverState = new GameOverState(this, scoreManager);
        physicsManager = new PhysicsManager(player, tiles, soundManager, coinManager.getCoins(), scoreManager);
        effectsManager = new EffectsManager(this.spriteBatch);
        pauseState = new PauseState(game,sharedAssets);

        // Create "Enter to start" message
        createEnterToStartMessage();

        // Camera and Viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(Main.WORLD_WIDTH, Main.WORLD_HEIGHT, camera);
        camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0); // Center the camera
        camera.position.set(player.getXPosition(), player.getYPosition(), 0); // Center the camera
        camera.zoom = initialCameraZoom; // Start with camera zoomed in
        camera.update();

        background = new Background( "atlas/lava_theme.atlas", backgroundSpeed, 6, game);
        // play background music
        soundManager.backgroundMusic();
    }

    @Override
    public void render(float delta) {

        float deltaTime = Gdx.graphics.getDeltaTime(); // Gets time lapsed since last frame

        // Kolla om "P" trycks för att toggla pausläget
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) && !player.isDead()) {
            pauseState.togglePause();
        }

        // Om spelet är pausat, rendera pausmenyn och returnera
        if (pauseState.isPaused()) {
            pauseOpacity(deltaTime);
            pauseState.render();
            return;
        }
        player.updateAnimationTime(deltaTime);
        // Update animation times
        player.updateAnimationTime(deltaTime);
        sharedAssets.updateMainLogoAnimationTime(deltaTime);
        coinManager.updateAnimationTime(deltaTime);

        // Exits start mode when Enter is pressed
        if (startMode && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startMode = false;
        }

        if (!startMode && !player.isDead()) {

            // Set Hud as input processor
            Gdx.input.setInputProcessor(hud.getStage());

            // If space-bar is pressed or mouse is clicked and the character is not already in a jumping state increase velocity
            if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !player.isJumping())){
                player.startJump();
            }

            physicsManager.applyGravity(deltaTime); // Enable gravity
            physicsManager.checkCollision(); // Check for tile and floor collisions
            hud.setScore(scoreManager.getScore()); // Update score in HUD
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
            sharedAssets.drawLogoAnimation(500, 109, 300, false);
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
                    sharedAssets.drawLogoAnimation(500, 109, 300, true);
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

        }
        game.spriteBatch.end();

        if (startMode){
            stage.act();
            stage.draw();
        }else {
            hud.render(deltaTime);
        }

        if (player.isDead()){
            Gdx.input.setInputProcessor(gameOverState.getStage());
            gameOverState.render(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Adapt viewport after window size
        hud.resize(width, height);
        camera.position.set(Main.WORLD_WIDTH / 2, Main.WORLD_HEIGHT / 2, 0);
        gameOverState.resize(width, height);
        camera.update();
        pauseState.togglePause();
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
        font.dispose();
        soundManager.dispose();
        effectsManager.dispose();
        stage.dispose();
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
    private void pauseOpacity(float deltaTime) {
        game.spriteBatch.begin();
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 0.0f); // clear screen with black color
        game.spriteBatch.setColor(1f, 1f, 1f, 0.7f); // 70% opacity
        background.drawBackgroundSet(false, deltaTime);
        game.spriteBatch.setColor(1f, 1f, 1f, 1f);
        game.spriteBatch.end();
    }

    private void createEnterToStartMessage(){


        // OLD
        // Creating bitmap font object
        font = new BitmapFont();

        // Load skin which includes the font
        skin = new Skin(Gdx.files.internal("skins/game_over_skin.json"));

        // Create a font family (because TypingLabel does not support skins)
        Font.FontFamily fontFamily = new Font.FontFamily(skin);

        // Create "Press ENTER to start message
        enterMessageLabel = new TypingLabel("{FADE}Press {GRADIENT=ffffffff;90ffa7ff;1.0;3.6}{WAVE=0.5;1.0;1.0}ENTER{ENDWAVE}{ENDGRADIENT} to start...{ENDFADE}", fontFamily.connected[0]);

        // Create stage and table
        stage = new Stage();
        table = new Table();
        table.setFillParent(true);

        // Add message to table, and table to stage
        table.add(enterMessageLabel).center().pad(10, 300, 10, 300);
        stage.addActor(table);
    }


    private <T extends Resettable<T>> void resetObject(T object) {
        object.reset();
    }

    /** Reset the game. */
    @Override
    public void resetGame() {

        resetObject(player);
        resetObject(tiles); // Reset tiles to prepare for new game
        resetObject(scoreManager); // Reset score
        resetObject(coinManager); // Reset all coins
        resetObject(soundManager); // Reset sound play flag
        resetObject(effectsManager); // Reset lava particle effect
        resetObject(sharedAssets);
        resetObject(gameOverState);
        startMode = true; // Set flag to show start mode again
    }


    @Override
    public void pauseGame() {
        pauseState.togglePause();
    }
}
