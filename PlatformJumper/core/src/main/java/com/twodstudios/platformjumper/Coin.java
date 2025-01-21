package com.twodstudios.platformjumper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import static com.twodstudios.platformjumper.AnimationManager.*;

/**
* Class to create and handle Coin objects.
*/
public class Coin {
    private float x, y;
    private float width;
    private float height;
    private TextureAtlas atlas;
    private TextureRegion[] textureRegions = new TextureRegion[10];
    private Animation<TextureRegion> animation;
    private Rectangle rectangle;

    /**
     * Create a Coin object.
     * @param x X-coordinate for the coin.
     * @param y Y-coordinate for the coin.
     * @param width Width of coin.
     * @param height Height of coin.
     */
    public Coin(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;

        // Collision rectangle for collision logic
        this.rectangle = new Rectangle(x, y, width, height);
        setupCoin();
    }

    /**
     * Method to initialise and populate necessary fields for the Coin object.
     */
    private void setupCoin(){

        // Load the atlas containing the coin image
        atlas = new TextureAtlas(Gdx.files.internal("atlas/coin.atlas"));

        // Create coin animation
        animation = createAnimation(atlas, textureRegions, "coin", 1/10f);
        animation.setPlayMode(Animation.PlayMode.LOOP); // Set animation in loop-mode
    }

    /** Get X-coordinate of Coin-object.*/
    public float getX() {
        return x;
    }

    /** Get Y-coordinate of Coin-object.*/
    public float getY() {
        return y;
    }

    /** Set X-coordinate of Coin-object.*/
    public void setX(float x) {
        this.x = x;
        rectangle.setX(x);
    }

    /** Get the Animation object of the coin.*/
    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    /** Get width of Coin-object.*/
    public float getWidth() {
        return width;
    }

    /** Get height of Coin-object.*/
    public float getHeight() {
        return height;
    }

    /** Get the Rectangle object of the coin. Can be used for collision logic. */
    public Rectangle getRectangle(){
        return rectangle;
    }
}

