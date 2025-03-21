package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;
import com.mygdx.game.AbstractIO.IInputManager;
import java.util.List;

public class Player extends MovableEntity {
    private Texture texture;
    private IInputManager inputManager; // Injected Input Manager
    private GameEntityManager entityManager; // Reference to entity manager

    public Player(float x, float y, float speed, IInputManager inputManager, GameEntityManager entityManager) {
        super(x, y, speed);
        this.texture = new Texture(Gdx.files.internal("player.png"));
        this.inputManager = inputManager; // Inject dependency
        this.entityManager = entityManager;
        this.previousX = x;
        this.previousY = y;
        this.width = 50; // Make sure width and height are explicitly set
        this.height = 50;
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
        // Store current position before moving
        this.previousX = this.x;
        this.previousY = this.y;
        
        float horizontal = inputManager.getMoveX(); // Uses IOManager for movement input
        float vertical = inputManager.getMoveY();
        
        // Try moving horizontally
        if (horizontal != 0) {
            float newX = this.x + speed * deltaTime * horizontal;
            // Ensure entity stays within screen bounds
            newX = Math.max(0, Math.min(newX, Gdx.graphics.getWidth() - width));
            
            // Check if new position would cause a tree collision
            if (!wouldCollideWithTree(newX, this.y)) {
                this.x = newX; // Only move if no collision would occur
            }
        }
        
        // Try moving vertically
        if (vertical != 0) {
            float newY = this.y + speed * deltaTime * vertical;
            // Ensure entity stays within screen bounds
            newY = Math.max(0, Math.min(newY, Gdx.graphics.getHeight() - height));
            
            // Check if new position would cause a tree collision
            if (!wouldCollideWithTree(this.x, newY)) {
                this.y = newY; // Only move if no collision would occur
            }
        }
    }
    
    // Check if moving to a position would cause a collision with any tree
    private boolean wouldCollideWithTree(float newX, float newY) {
        // Create a temporary rectangle at the potential new position
        Rectangle potentialPosition = new Rectangle(newX, newY, width, height);
        
        // Get all entities and check for tree collisions
        if (entityManager != null) {
            List<Entity> entities = entityManager.getEntities();
            for (Entity entity : entities) {
                if (entity instanceof Tree && potentialPosition.overlaps(entity.getBoundingBox())) {
                    System.out.println("Would collide with tree - movement prevented");
                    return true; // Would collide with a tree
                }
            }
        }
        
        return false; // No collision would occur
    }

    @Override
    public void moveAIControlled() {
        // Not used for Player
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    public void setPosition(float x, float y) {
        this.previousX = this.x;
        this.previousY = this.y;
        this.x = x;
        this.y = y;
    }
    
    // Set entity manager after creation if needed
    public void setEntityManager(GameEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void handleCollision(iCollidable other) {
        if (other instanceof Tree) {
            System.out.println("Player collided with a tree!");
        } else if (other instanceof Ball) {
            System.out.println("Player collected a ball: " + ((Ball)other).getValue());
        }
    }

    public float getPreviousX() {
        return previousX;
    }

    public float getPreviousY() {
        return previousY;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}