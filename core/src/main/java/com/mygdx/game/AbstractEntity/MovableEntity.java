package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class MovableEntity extends Entity implements iMovable {
    protected float speed;
    protected float previousX, previousY;
    protected float directionX, directionY;

    public MovableEntity(float x, float y, float speed) {
        super(x, y);
        this.speed = speed;
        // Initialize previous position
        this.previousX = x;
        this.previousY = y;
        // set default direction
        directionX = 1;
        directionY = 1;
    }

    public abstract void draw(SpriteBatch batch);

    // Extracted common movement logic
    protected void move(float deltaTime, float directionX, float directionY) {
        // Store last position before movement
        this.previousX = this.x;
        this.previousY = this.y;

        // deltaTime ensures that the movement is move - deltaTime = the time taken per
        // frame
        this.x += speed * deltaTime * directionX;
        this.y += speed * deltaTime * directionY;

        // Ensure entity stays within screen bounds
        this.x = Math.max(0, Math.min(this.x, Gdx.graphics.getWidth() - width));
        this.y = Math.max(0, Math.min(this.y, Gdx.graphics.getHeight() - height));
    }

    @Override
    public void moveAIControlled() {
        System.out.println("AI moving entity...");
    }

    @Override
    public void moveUserControlled(float deltaTime) {
        System.out.println("User controlling movement...");
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

    public void reverseXDirection() {
        directionX *= -1;
    }

    public void reverseYDirection() {
        directionY *= -1;
    }

    public void updateDirection() {
        move(Gdx.graphics.getDeltaTime(), directionX, directionY);
    }
}
