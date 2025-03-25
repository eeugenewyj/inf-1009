package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class StaticObject extends Entity {
    protected Texture texture;
    protected Rectangle boundingBox;

    public StaticObject(float x, float y, float width, float height, String texturePath) {
        super(x, y);
        this.texture = new Texture(Gdx.files.internal(texturePath));
        this.boundingBox = new Rectangle(x, y, width, height); // Create bounding box for collisions
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, x, y, boundingBox.width, boundingBox.height);
        }
    }

    @Override
    public void draw() {
        // Optional: If static objects should have behavior (e.g., doors opening)
    }

    @Override
    public void update(float deltaTime) {
        // Static objects don’t move, so no update logic needed
    }

    public Rectangle getBoundingBox() {
        return boundingBox; // Ensure bounding box can be accessed for collisions
    }

    @Override
    public void handleCollision(iCollidable other) {
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}