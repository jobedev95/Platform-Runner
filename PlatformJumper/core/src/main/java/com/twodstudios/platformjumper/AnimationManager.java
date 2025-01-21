package com.twodstudios.platformjumper;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Handles creation of animations. Can be imported to any class to help with creating Animation objects.*/
public class AnimationManager {

    /** Creates an animation from a TextureRegion array.
     * @param atlas TextureAtlas containing the assets needed for the animation.
     * @param textureRegions Empty array of TextureRegions that together will become the Animation object.
     * @param fileBaseName Base name of the TextureRegions.
     * @param frameDuration Adjusts how long each frame of the animation should be shown e.g. 1/30.
     */
    public static Animation<TextureRegion> createAnimation(TextureAtlas atlas, TextureRegion[] textureRegions, String fileBaseName, float frameDuration){

        int amountOfTextures = textureRegions.length;

        // Save all TextureRegions in an array
        for (int i = 0; i < amountOfTextures; i++) {
            String frameName = fileBaseName + i;
            textureRegions[i] = atlas.findRegion(frameName);
        }

        // Create and return animation object
        return new Animation<TextureRegion>(frameDuration, textureRegions);
    }
}
