package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Entity implements iCollidable {
    protected float width = 50, height = 50; // Dimensions of the entity
    protected boolean hasCollided = false; // Flag to determine if the entity has collided with another entity
    protected boolean isActive; // Flag to determine if the entity is active

    protected float x, y; // Position in the game world

    // Constructor to initialise the entity's position and set it as active
    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
        this.isActive = true; // Default to active when created
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
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public abstract void dispose();
}