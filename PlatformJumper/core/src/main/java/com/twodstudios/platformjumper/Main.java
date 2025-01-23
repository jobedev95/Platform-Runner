package com.twodstudios.platformjumper;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.twodstudios.platformjumper.screens.PlayScreen;
import com.twodstudios.platformjumper.screens.StartMenu;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public SpriteBatch spriteBatch;
    public static final float WORLD_WIDTH = 1171;
    public static final float WORLD_HEIGHT = 659;
    public SharedAssets sharedAssets;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        sharedAssets = new SharedAssets(this.spriteBatch);
        setScreen(new StartMenu(this, sharedAssets));

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
