package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;

public class StaticObject extends Entity {
    protected Texture texture; // Texture for rendering the static object
    protected Rectangle boundingBox; // Bounding box for collision detection

    public StaticObject(float x, float y, float width, float height, String texturePath) {
        super(x, y); // Call parent constructor
        this.texture = new Texture(Gdx.files.internal(texturePath)); // Load texture from file
        this.boundingBox = new Rectangle(x, y, width, height); // Create bounding box for collisions
    }

    @Override
    public void draw() {
        // Optional: If static objects should have behavior (e.g., doors opening)
    }

    // Method to draw the static object using a SpriteBatch
    @Override
    public void draw(SpriteBatch batch) {
        if (texture != null) {
            // Draw the texture at the object's position with its bounding box dimensions
            batch.draw(texture, x, y, boundingBox.width, boundingBox.height);
        }
    }

    @Override
    public void update(float deltaTime) {
        // Static objects don’t move, so no update logic needed
    }

    // Method to handle collisions with other collidable entities
    @Override
    public void handleCollision(iCollidable other) {
    }

    public Rectangle getBoundingBox() {
        return boundingBox; // Ensure bounding box can be accessed for collisions
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}