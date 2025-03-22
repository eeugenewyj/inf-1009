package com.mygdx.game;

import com.mygdx.game.AbstractCollision.AbstractCollisionManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.IEntityManager;
import com.mygdx.game.AbstractIO.Audio;

public class GameCollisionManager extends AbstractCollisionManager {
    private final Audio audio = Audio.getInstance();
    private GameScene gameScene; // Reference to GameScene for score updates

    public GameCollisionManager(IEntityManager entityManager) {
        super(entityManager);
    }
    
    // Additional constructor that takes a reference to the GameScene
    public GameCollisionManager(IEntityManager entityManager, GameScene gameScene) {
        super(entityManager);
        this.gameScene = gameScene;
    }

    @Override
    protected void handleCollision(Entity entity) {
        // Improved tree collision handling for players
        if (entity instanceof Player) {
            Player player = (Player) entity;
            
            // First check for tree collisions
            boolean hasTreeCollision = false;
            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Tree && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    hasTreeCollision = true;
                    
                    // Play sound if this is a new collision
                    if (!player.hasCollided()) {
                        player.handleCollision((Tree)other);
                        audio.playSoundEffect("tree");
                        player.setCollided(true);
                    }
                    
                    // Move player back to previous position before collision
                    player.setX(player.getPreviousX());
                    player.setY(player.getPreviousY());
                    break;
                }
            }
            
            // Reset collision state when no longer colliding with any tree
            if (!hasTreeCollision && player.hasCollided()) {
                player.setCollided(false);
            }
            
            // After handling tree collisions, check for ball collisions
            Ball collidedBall = null;
            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Ball && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    collidedBall = (Ball) other;
                    break; // Only detect first collision
                }
            }

            if (collidedBall != null) {
                // Get the ball's value before processing collision
                int ballValue = collidedBall.getValue();
                
                // Add Ball's value to the Player's score
                player.handleCollision(collidedBall);
                
                // Update score in GameScene if available - add only this individual ball's value
                if (gameScene != null) {
                    gameScene.addScore(ballValue);
                }

                // Call removeBallRow() from GameEntityManager to remove all balls in the same row
                ((GameEntityManager) entityManager).removeBallsRow(collidedBall);

                // Play collision sound
                audio.playSoundEffect("player");

                // Spawn the next row of balls
                ((GameEntityManager) entityManager).spawnBallsRow();
            }
            
            // Check for power-up collisions
            PowerUp collidedPowerUp = null;
            for (Entity other : entityManager.getEntities()) {
                if (other instanceof PowerUp && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    collidedPowerUp = (PowerUp) other;
                    break; // Only detect first collision
                }
            }

            if (collidedPowerUp != null) {
                // Get the position where the effect should appear (near where the power-up was collected)
                float effectX = collidedPowerUp.getX();
                float effectY = collidedPowerUp.getY();
                
                // Process power-up effect
                switch (collidedPowerUp.getType()) {
                    case PowerUp.TYPE_DOUBLE_POINTS:
                        if (gameScene != null) {
                            gameScene.activateDoublePoints();
                            // Add visual effect for double points
                            PowerUpEffect effect = PowerUpEffect.createDoublePointsEffect(effectX, effectY);
                            ((GameEntityManager) entityManager).addEntity(effect);
                        }
                        break;
                    case PowerUp.TYPE_EXTEND_TIME:
                        if (gameScene != null) {
                            gameScene.extendGameTime(5f); // Add 5 seconds
                            // Add visual effect for time extension
                            PowerUpEffect effect = PowerUpEffect.createTimeExtensionEffect(effectX, effectY);
                            ((GameEntityManager) entityManager).addEntity(effect);
                        }
                        break;
                }
                
                // Mark power-up as collected
                collidedPowerUp.setActive(false);
                
                // Play power-up sound effect
                audio.playSoundEffect("powerup");
            }
        }

        // Handle Ball and Tree collision
        if (entity instanceof Ball) {
            Ball ball = (Ball) entity;

            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Tree && ball.getBoundingBox().overlaps(other.getBoundingBox())) {
                    ball.setActive(false); // Remove only the ball that touches the tree
                    break;
                }
            }
        }
    }

    @Override
    public void dispose() {
        // Cleanup logic if necessary
    }
}