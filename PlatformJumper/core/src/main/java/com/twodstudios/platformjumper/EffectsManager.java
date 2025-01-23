package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Manages all ParticleEffect objects.*/
public class EffectsManager implements Resettable <EffectsManager>{
    private SpriteBatch spriteBatch;

    // Particle effects
    private ParticleEffect sparklesParticleEffect;
    private ParticleEffect lavaExplosionParticleEffect;
    private ParticleEffect mainMenuParticleEffect;
    private boolean explosionStarted; // Flag to ensure the lava explosion plays only once

    /**
     * Create a new instance of EffectsManager to manage ParticleEffect objects.
     * @param spriteBatch Spritebatch that handles drawing in the game.
     */
    public EffectsManager(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
    }

    /** Initialize fire sparkles effect if it hasn't been created already. */
    private ParticleEffect getFireSparklesEffect() {
        // If the sparklesParticleEffect hasn't been created, it will be created
        if (sparklesParticleEffect == null) {
            sparklesParticleEffect = new ParticleEffect();
            sparklesParticleEffect.load(Gdx.files.internal("effects/lava_sparkles.p"), Gdx.files.internal("effects")); // Load asset
            sparklesParticleEffect.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - 150); // Position the effect
            ParticleEmitter sparklesEmitter =  sparklesParticleEffect.getEmitters().first(); // Get and store the particle emitter
            sparklesEmitter.setPosition(Gdx.graphics.getWidth() / 2f, 120);  // Position the emitter
            // Adjust emitter settings
            sparklesParticleEffect.scaleEffect(1.2f); // Scale effect by 20%
        }
        return sparklesParticleEffect;
    }

    /** Initialize lava explosion effect if it hasn't been created already. */
    private ParticleEffect getLavaExplosionParticleEffect() {
        // If the lavaExplosionParticleEffect hasn't been created, it will be created
        if (lavaExplosionParticleEffect == null) {
            lavaExplosionParticleEffect = new ParticleEffect();
            lavaExplosionParticleEffect.load(Gdx.files.internal("effects/lava_explosion.p"), Gdx.files.internal("effects")); // Load asset
            lavaExplosionParticleEffect.setPosition(0, 0); // Position the effect
            ParticleEmitter lavaExplosionEmitter = lavaExplosionParticleEffect.getEmitters().first(); // Get and store the particle emitter
            lavaExplosionParticleEffect.scaleEffect(2f); // Scale effect by 100%
            explosionStarted = false; // Flag to ensure the lava explosion plays only once
        }
        return lavaExplosionParticleEffect;
    }

    /** Initialize main menu sparkles effect if it hasn't been created already. */
    private ParticleEffect getMainMenuParticleEffect() {
        // If the mainMenuParticleEffect hasn't been created, it will be created
        if (mainMenuParticleEffect == null) {
            mainMenuParticleEffect = new ParticleEffect();
            mainMenuParticleEffect.load(Gdx.files.internal("effects/main_menu_sparkles.p"), Gdx.files.internal("effects")); // Load asset
            mainMenuParticleEffect.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - 150); // Position the effect
            ParticleEmitter mainMenuSparklesEmitter =  mainMenuParticleEffect.getEmitters().first(); // Get and store the particle emitter
            mainMenuSparklesEmitter.setPosition(Gdx.graphics.getWidth() / 2f, Main.WORLD_HEIGHT / 2);  // Position the emitter
            // Adjust emitter settings
            mainMenuParticleEffect.scaleEffect(1.5f); // Scale effect by 20%
        }
        return mainMenuParticleEffect;
    }



    /** Draw continuous fire sparkles. */
    public void drawSparkles(float deltaTime){
        sparklesParticleEffect = getFireSparklesEffect(); // Initialize the effect
        sparklesParticleEffect.draw(spriteBatch, deltaTime); // Draw continuous particle effect
    }

    /** Draw a lava explosion. */
    public void drawLavaExplosion(float deltaTime, float xPosition, float yPosition){
        lavaExplosionParticleEffect = getLavaExplosionParticleEffect(); // Initialize the effect

        // If explosion has not been started before it starts
        if (!explosionStarted) {
            explosionStarted = true;

            // Set position of lava explosion and start the effect
            lavaExplosionParticleEffect.setPosition(xPosition, yPosition);
            lavaExplosionParticleEffect.start();
        }

        lavaExplosionParticleEffect.draw(spriteBatch, deltaTime); // Draw lava explosion effect
    }

    /** Draw continuous main menu sparkles. */
    public void drawMainMenuParticles(float deltaTime){
        mainMenuParticleEffect = getMainMenuParticleEffect();
        mainMenuParticleEffect.draw(spriteBatch, deltaTime); // Draw continuous particle effect
    }

    /** Reset the lava explosion animation. */
    private void resetLavaExplosion(){
        explosionStarted = false;
        if (lavaExplosionParticleEffect != null){
            lavaExplosionParticleEffect.reset();
            lavaExplosionParticleEffect.scaleEffect(2f);
        }

    }

    public void dispose(){
        if(mainMenuParticleEffect != null){
            mainMenuParticleEffect.dispose();
        }

        if(sparklesParticleEffect != null) {
            sparklesParticleEffect.dispose();
        }

        if(lavaExplosionParticleEffect != null) {
            lavaExplosionParticleEffect.dispose();
        }
    }

    @Override
    public void reset() {
        resetLavaExplosion();
    }
}
