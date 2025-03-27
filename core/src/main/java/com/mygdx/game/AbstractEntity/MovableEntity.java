package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class MovableEntity extends Entity implements iMovable {
    // Changed from private to private for consistency
    private float speed;
    private float previousX, previousY; // Previous position of entity
    private float directionX, directionY; // Direction of movement

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
        this.previousX = getX();
        this.previousY = getY();

        // Update position using speed, deltaTime, and direction.
        setX(getX() + speed * deltaTime * directionX);
        setY(getY() + speed * deltaTime * directionY);

        // Ensure entity stays within screen bounds
        setX(Math.max(0, Math.min(getX(), Gdx.graphics.getWidth() - getWidth())));
        setY(Math.max(0, Math.min(getY(), Gdx.graphics.getHeight() - getHeight())));
    }

    // Method to change direction when entity collides with something horizontally
    public void reverseXDirection() {
        directionX *= -1;
    }

    // Method to change direction when entity collides with something vertically
    public void reverseYDirection() {
        directionY *= -1;
    }
    
    /**
     * Gets the X direction multiplier
     * @return The X direction multiplier (1 for right, -1 for left)
     */
    public float getDirectionX() {
        return directionX;
    }
    
    /**
     * Gets the Y direction multiplier
     * @return The Y direction multiplier (1 for up, -1 for down)
     */
    public float getDirectionY() {
        return directionY;
    }
    
    /**
     * Sets the X direction multiplier
     * @param dirX The new X direction
     */
    public void setDirectionX(float dirX) {
        this.directionX = dirX;
    }
    
    /**
     * Sets the Y direction multiplier
     * @param dirY The new Y direction
     */
    public void setDirectionY(float dirY) {
        this.directionY = dirY;
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