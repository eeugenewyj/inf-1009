package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class MovableEntity extends Entity implements iMovable {
    protected float speed;
    protected float previousX, previousY; // Previous position of entity
    protected float directionX, directionY; // Direction of movement

    public MovableEntity(float x, float y, float speed) {
        super(x, y); // Call parent constructor
        this.speed = speed;
        // Initialize previous position
        this.previousX = x;
        this.previousY = y;
        // set default direction
        directionX = 1;
        directionY = 1;
    }

    // Implementation of moveAIControlled method
    @Override
    public void moveAIControlled() {
        System.out.println("AI moving entity...");
    }

    // Implementation of moveUserControlled method
    @Override
    public void moveUserControlled(float deltaTime) {
        System.out.println("User controlling movement...");
    }

    // Abstract method to draw the entity using a SpriteBatch
    public abstract void draw(SpriteBatch batch);

    // Protected method to move the entity based on deltaTime and direction
    protected void move(float deltaTime, float directionX, float directionY) {
        // Save current position as previous before moving
        this.previousX = this.x;
        this.previousY = this.y;

        // Update position using speed, deltaTime, and direction.
        this.x += speed * deltaTime * directionX;
        this.y += speed * deltaTime * directionY;

        // Ensure entity stays within screen bounds
        this.x = Math.max(0, Math.min(this.x, Gdx.graphics.getWidth() - width));
        this.y = Math.max(0, Math.min(this.y, Gdx.graphics.getHeight() - height));
    }

    // Method to change direction when entity collides with something horizontally
    public void reverseXDirection() {
        directionX *= -1;
    }

    // Method to change direction when entity collides with something vertically
    public void reverseYDirection() {
        directionY *= -1;
    }

    // Method to set previous position explicitly (useful for state restoration)
    public void setPreviousPosition(float prevX, float prevY) {
        this.previousX = prevX;
        this.previousY = prevY;
    }

    public float getPreviousX() {
        return previousX;
    }

    public float getPreviousY() {
        return previousY;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}