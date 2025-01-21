package com.twodstudios.platformjumper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Manages all ParticleEffect objects.*/
public class EffectsManager {
    private SpriteBatch spriteBatch;

    // Particle effects
    private ParticleEffect sparklesParticleEffect;
    private ParticleEffect lavaExplosionParticleEffect;
    private boolean explosionStarted; // Flag to ensure the lava explosion plays only once

    /**
     * Create a new instance of EffectsManager to manage ParticleEffect objects.
     * @param spriteBatch Spritebatch that handles drawing in the game.
     */
    public EffectsManager(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        setupEffectsManager();
    }

    /**
     * Method to initialise and populate necessary fields for the EffectsManager.
     */
    private void setupEffectsManager(){
        // Fire sparkles effect
        sparklesParticleEffect = new ParticleEffect();
        sparklesParticleEffect.load(Gdx.files.internal("effects/lava_sparkles.p"), Gdx.files.internal("effects")); // Load asset
        sparklesParticleEffect.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - 150); // Position the effect
        ParticleEmitter sparklesEmitter =  sparklesParticleEffect.getEmitters().first(); // Get and store the particle emitter
        sparklesEmitter.setPosition(Gdx.graphics.getWidth() / 2f, 120);  // Position the emitter
        // Adjust emitter settings
        sparklesParticleEffect.scaleEffect(1.2f); // Scale effect by 20%

        // Lava explosion effect
        lavaExplosionParticleEffect = new ParticleEffect();
        lavaExplosionParticleEffect.load(Gdx.files.internal("effects/lava_explosion.p"), Gdx.files.internal("effects")); // Load asset
        lavaExplosionParticleEffect.setPosition(0, 0); // Position the effect
        ParticleEmitter lavaExplosionEmitter = lavaExplosionParticleEffect.getEmitters().first(); // Get and store the particle emitter
        lavaExplosionParticleEffect.scaleEffect(2f); // Scale effect by 100%
        explosionStarted = false; // Flag to ensure the lava explosion plays only once
    }

    /** Draw continous fire sparkles. */
    public void drawSparkles(float deltaTime){
        sparklesParticleEffect.draw(spriteBatch, deltaTime); // Draw continuous particle effect
    }

    /** Draw a lava explosion. */
    public void drawLavaExplosion(float deltaTime, float xPosition, float yPosition){
        // If explosion has not been started before it starts
        if (!explosionStarted) {
            explosionStarted = true;
            
            // Set position of lava explosion and start the effect
            lavaExplosionParticleEffect.setPosition(xPosition, yPosition);
            lavaExplosionParticleEffect.start();
        }

        lavaExplosionParticleEffect.draw(spriteBatch, deltaTime); // Draw lava explosion effect
    }

    /** Reset the lava explosion animation. */
    public void resetLavaExplosion(){
        explosionStarted = false;
        lavaExplosionParticleEffect.reset();
    }
}
