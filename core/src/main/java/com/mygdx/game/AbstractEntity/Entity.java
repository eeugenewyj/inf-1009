package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity implements iCollidable {
    // Make all fields private for proper encapsulation
    private float width = 50, height = 50; // Dimensions of the entity
    private boolean hasCollided = false; // Flag to determine if the entity has collided with another entity
    private boolean isActive; // Flag to determine if the entity is active
    private float x, y; // Position in the game world
    private Rectangle boundingBox; // Cache for bounding box to avoid recreating it frequently

    // Constructor to initialise the entity's position and set it as active
    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
        this.isActive = true; // Default to active when created
        updateBoundingBox(); // Initialize the bounding box
    }

    // Abstract method to handle collisions with other collidable entities
    @Override
    public abstract void handleCollision(iCollidable other);

    // Abstract method to draw the entity
    public abstract void draw();

    // Abstract method to draw the entity using a SpriteBatch
    public abstract void draw(SpriteBatch batch);

    // Abstract method to update the entity
    public abstract void update(float deltaTime);

    // Private method to update the bounding box when position or size changes
    private void updateBoundingBox() {
        boundingBox = new Rectangle(x, y, width, height);
    }

    // Getter for collision status
    public boolean hasCollided() {
        return hasCollided;
    }

    // Setter for collision status
    public void setCollided(boolean collided) {
        this.hasCollided = collided;
    }

    // Getter for active status
    public boolean isActive() {
        return isActive;
    }

    // Setter for active status
    public void setActive(boolean active) {
        this.isActive = active;
    }

    // Getter for the bounding box of the entity
    @Override
    public Rectangle getBoundingBox() {
        // Return a copy to prevent external modification
        return new Rectangle(boundingBox);
    }

    // Getter for x position
    public float getX() {
        return x;
    }

    // Getter for y position
    public float getY() {
        return y;
    }

    // Setter for x position with bounding box update
    public void setX(float x) {
        this.x = x;
        updateBoundingBox();
    }

    // Setter for y position with bounding box update
    public void setY(float y) {
        this.y = y;
        updateBoundingBox();
    }

    // Getter for width
    public float getWidth() {
        return width;
    }

    // Setter for width with bounding box update
    public void setWidth(float width) {
        this.width = width;
        updateBoundingBox();
    }

    // Getter for height
    public float getHeight() {
        return height;
    }

    // Setter for height with bounding box update
    public void setHeight(float height) {
        this.height = height;
        updateBoundingBox();
    }

    // Method to set position
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateBoundingBox();
    }

    // Abstract method for resource cleanup
    public abstract void dispose();
}