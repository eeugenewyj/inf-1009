package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;
import com.mygdx.game.AbstractIO.iInputManager;

public class Player extends MovableEntity {
    private Texture texture;
    private iInputManager inputManager; // Injected Input Manager
    private iCollisionCallback collisionCallback; // Using callback instead of direct EntityManager reference

    // Define the movement boundary - player cannot go above this line
    private final float MAX_Y_POSITION;

    // Add this field for inverted controls
    private boolean invertControls = false;

    public Player(float x, float y, float speed, iInputManager inputManager) {
        super(x, y, speed);
        this.texture = new Texture(Gdx.files.internal("player.png"));
        this.inputManager = inputManager;
        
        // Explicitly set previous position to match current position
        setPreviousPosition(x, y);
        
        // Set width and height explicitly using the setters
        setWidth(50);
        setHeight(50);

        // Set the movement boundary at 3/4 of the screen height
        this.MAX_Y_POSITION = Gdx.graphics.getHeight() * 0.75f - getHeight();
    }

    /**
     * Sets the collision callback for this player
     * @param callback The collision callback
     */
    public void setCollisionCallback(iCollisionCallback callback) {
        this.collisionCallback = callback;
    }

    @Override
    public void draw() {
        // Empty implementation - rendering happens in SpriteBatch version
    }

    public void draw(SpriteBatch batch) { // Draw player image
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void update(float deltaTime) {
        moveUserControlled(deltaTime);
    }

    @Override
    public void moveUserControlled(float deltaTime) {
        // Always store current position as previous before moving
        setPreviousPosition(getX(), getY());

        float horizontal = inputManager.getMoveX();
        float vertical = inputManager.getMoveY();

        // Invert controls if the debuff is active
        if (invertControls) {
            horizontal = -horizontal;
            vertical = -vertical;
        }

        // Try moving horizontally
        if (horizontal != 0) {
            float newX = getX() + getSpeed() * deltaTime * horizontal;
            // Ensure entity stays within screen bounds
            newX = Math.max(0, Math.min(newX, Gdx.graphics.getWidth() - getWidth()));

            // Check collision using the callback
            if (collisionCallback == null || !collisionCallback.wouldCollideWithSpikes(newX, getY(), getWidth(), getHeight())) {
                setX(newX); // Only move if no collision would occur or no callback is set
            }
        }

        // Try moving vertically
        if (vertical != 0) {
            float newY = getY() + getSpeed() * deltaTime * vertical;
            // Ensure player stays within vertical bounds
            newY = Math.max(0, Math.min(newY, MAX_Y_POSITION));

            // Check collision using the callback
            if (collisionCallback == null || !collisionCallback.wouldCollideWithSpikes(getX(), newY, getWidth(), getHeight())) {
                setY(newY); // Only move if no collision would occur or no callback is set
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

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void setPosition(float x, float y) {
        // Also update previous position when manually setting position
        setPreviousPosition(getX(), getY());
        
        // Set the x position
        setX(x);
        
        // Apply the vertical movement restriction when setting y position
        setY(Math.min(y, MAX_Y_POSITION));
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
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}