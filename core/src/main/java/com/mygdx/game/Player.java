package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;
import com.mygdx.game.AbstractIO.IInputManager;

public class Player extends MovableEntity {
    private Texture texture;
    private IInputManager inputManager; // Injected Input Manager
    private CollisionCallback collisionCallback; // Using callback instead of direct EntityManager reference

    // Define the movement boundary - player cannot go above this line
    private final float MAX_Y_POSITION;

    // Add this field for inverted controls
    private boolean invertControls = false;

    public Player(float x, float y, float speed, IInputManager inputManager) {
        super(x, y, speed);
        this.texture = new Texture(Gdx.files.internal("player.png"));
        this.inputManager = inputManager;
        
        // Explicitly set previous position to match current position
        this.previousX = x;
        this.previousY = y;
        
        this.width = 50; // Make sure width and height are explicitly set
        this.height = 50;

        // Set the movement boundary at 3/4 of the screen height
        this.MAX_Y_POSITION = Gdx.graphics.getHeight() * 0.75f - this.height;
    }

    /**
     * Sets the collision callback for this player
     * @param callback The collision callback
     */
    public void setCollisionCallback(CollisionCallback callback) {
        this.collisionCallback = callback;
    }

    @Override
    public void draw() {
        // Empty implementation - rendering happens in SpriteBatch version
    }

    public void draw(SpriteBatch batch) { // Draw player image
        batch.draw(texture, x, y, width, height);
    }

    @Override
    public void update(float deltaTime) {
        moveUserControlled(deltaTime);
    }

    @Override
    public void moveUserControlled(float deltaTime) {
        // Always store current position as previous before moving
        this.previousX = this.x;
        this.previousY = this.y;

        float horizontal = inputManager.getMoveX();
        float vertical = inputManager.getMoveY();

        // Invert controls if the debuff is active
        if (invertControls) {
            horizontal = -horizontal;
            vertical = -vertical;
        }

        // Try moving horizontally
        if (horizontal != 0) {
            float newX = this.x + speed * deltaTime * horizontal;
            // Ensure entity stays within screen bounds
            newX = Math.max(0, Math.min(newX, Gdx.graphics.getWidth() - width));

            // Check collision using the callback
            if (collisionCallback == null || !collisionCallback.wouldCollideWithSpikes(newX, this.y, width, height)) {
                this.x = newX; // Only move if no collision would occur or no callback is set
            }
        }

        // Try moving vertically
        if (vertical != 0) {
            float newY = this.y + speed * deltaTime * vertical;
            // Ensure player stays within vertical bounds
            newY = Math.max(0, Math.min(newY, MAX_Y_POSITION));

            // Check collision using the callback
            if (collisionCallback == null || !collisionCallback.wouldCollideWithSpikes(this.x, newY, width, height)) {
                this.y = newY; // Only move if no collision would occur or no callback is set
            }
        }
    }

    public void setInvertControls(boolean invert) {
        this.invertControls = invert;
    }

    public boolean areControlsInverted() {
        return invertControls;
    }

    @Override
    public void moveAIControlled() {
        // Not used for Player
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        // Also update previous position when manually setting position
        this.previousX = this.x;
        this.previousY = this.y;
        
        this.x = x;
        // Apply the vertical movement restriction when setting position
        this.y = Math.min(y, MAX_Y_POSITION);
    }

    @Override
    public void handleCollision(iCollidable other) {
        if (other instanceof Spikes) {
            System.out.println("Player collided with spikes!");
        } else if (other instanceof Balloon) {
            System.out.println("Player collected a balloon: " + ((Balloon) other).getValue());
        }
    }

    @Override
    public float getPreviousX() {
        return previousX;
    }

    @Override
    public float getPreviousY() {
        return previousY;
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}