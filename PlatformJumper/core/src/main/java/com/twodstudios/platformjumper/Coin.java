package com.twodstudios.platformjumper;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Coin {
    private Texture coinTexture;
    private float x, y;
    private final float coinWidth = 50;
    private final float coinHeight = 50;
    private Rectangle coinRectangle;

    public Coin(float x, float y) {
        this.x = x;
        this.y = y;
        this.coinTexture = new Texture("coin.png");
        this.coinRectangle = new Rectangle(x, y, coinWidth, coinHeight);
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public void setX(float x) {
        this.x = x;
        coinRectangle.setX(x);
    }
    public void draw(SpriteBatch batch) {
        batch.draw(coinTexture, x, y);
    }
    public Rectangle getRectangle(){
        return coinRectangle;
    }
}

