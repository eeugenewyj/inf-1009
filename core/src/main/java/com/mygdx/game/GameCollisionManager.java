package com.mygdx.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.AbstractCollision.AbstractCollisionManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.iEntityManager;
import com.mygdx.game.AbstractIO.Audio;

public class GameCollisionManager extends AbstractCollisionManager {
    private final Audio audio = Audio.getInstance(); // Singleton instance of Audio class
    private EntityScoreHandler scoreHandler; // Reference to score handler instead of GameScene
    private Circle tempCircle = new Circle(); // Reusable circle for collision detection

    public GameCollisionManager(iEntityManager entityManager) {
        super(entityManager);
    }

    // Additional constructor that takes a reference to the EntityScoreHandler
    public GameCollisionManager(iEntityManager entityManager, EntityScoreHandler scoreHandler) {
        super(entityManager);
        this.scoreHandler = scoreHandler;
    }

    @Override
    protected void handleCollision(Entity entity) {
        // Improved spikes collision handling for players
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // First check for spikes collisions
            boolean hasSpikesCollision = false;
            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Spikes && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    hasSpikesCollision = true;

                    // Play sound if this is a new collision
                    if (!player.hasCollided()) {
                        player.handleCollision((Spikes) other);
                        audio.playSoundEffect("spikes");
                        player.setCollided(true);
                    }

                    // Move player back to previous position before collision
                    player.setX(player.getPreviousX());
                    player.setY(player.getPreviousY());
                    break;
                }
            }

            // Reset collision state when no longer colliding with any spikes
            if (!hasSpikesCollision && player.hasCollided()) {
                player.setCollided(false);
            }

            // After handling spikes collisions, check for balloon collisions
            Balloon collidedBalloon = null;
            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Balloon && other.isActive() && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    collidedBalloon = (Balloon) other;
                    break; // Only handle first collision
                }
            }

            if (collidedBalloon != null) {
                // Get the balloon's value before processing collision
                int balloonValue = collidedBalloon.getValue();

                // Add Balloon's value to the Player's score
                player.handleCollision(collidedBalloon);

                // Update score in scoreHandler if available - add only this individual balloon's value
                if (scoreHandler != null) {
                    scoreHandler.addScore(balloonValue);
                }

                // Call removeBalloonRow() from GameEntityManager to remove all balloons in the same row
                if (entityManager instanceof GameEntityManager) {
                    ((GameEntityManager) entityManager).removeBalloonRow(collidedBalloon);
                }

                // Play collision sound
                audio.playSoundEffect("player");

                // Spawn the next row of balloons
                if (entityManager instanceof GameEntityManager) {
                    ((GameEntityManager) entityManager).spawnBalloonsRow();
                }
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
                // Get the position where the effect should appear
                float effectX = collidedPowerUp.getX();
                float effectY = collidedPowerUp.getY();

                // Process power-up effect through the scoreHandler if available
                if (scoreHandler != null) {
                    scoreHandler.processPowerUp(collidedPowerUp.getType(), effectX, effectY);
                }

                // Mark power-up as collected
                collidedPowerUp.setActive(false);

                // Play power-up sound effect - different sound for debuffs
                if (collidedPowerUp.isDebuff()) {
                    audio.playSoundEffect("debuff");
                } else {
                    audio.playSoundEffect("powerup");
                }
            }
        }

        // Enhanced Balloon and Spikes collision with multi-layered approach
        if (entity instanceof Balloon) {
            Balloon balloon = (Balloon) entity;

            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Spikes) {
                    Spikes spikes = (Spikes) other;

                    // 1. Standard bounding box check
                    if (balloon.getBoundingBox().overlaps(spikes.getBoundingBox())) {
                        balloon.setActive(false);
                        System.out.println("Balloon collided with spike (direct hit)");
                        audio.playSoundEffect("spikes");
                        return; // Exit early if collision detected
                    }

                    // 2. Circle-Rectangle intersection check
                    // This is more accurate for round balloons
                    float balloonCenterX = balloon.getX() + Balloon.getBalloonRadius();
                    float balloonCenterY = balloon.getY() + Balloon.getBalloonRadius();
                    tempCircle.set(balloonCenterX, balloonCenterY, Balloon.getBalloonRadius());

                    if (Intersector.overlaps(tempCircle, spikes.getBoundingBox())) {
                        balloon.setActive(false);
                        System.out.println("Balloon collided with spike (circle-rect)");
                        audio.playSoundEffect("spikes");
                        return; // Exit early if collision detected
                    }

                    // 3. Path-based collision detection
                    // Get previous position
                    float prevX = balloon.getPreviousX();
                    float prevY = balloon.getPreviousY();

                    // If previous position is valid (not 0,0)
                    if (prevX != 0 || prevY != 0) {
                        float currentX = balloon.getX();
                        float currentY = balloon.getY();

                        // Check if path intersects with spike
                        if (lineIntersectsRectangle(
                                prevX + Balloon.getBalloonRadius(), prevY + Balloon.getBalloonRadius(),
                                currentX + Balloon.getBalloonRadius(), currentY + Balloon.getBalloonRadius(),
                                spikes.getBoundingBox())) {
                            balloon.setActive(false);
                            System.out.println("Balloon collided with spike (path intersection)");
                            audio.playSoundEffect("spikes");
                            return; // Exit early if collision detected
                        }

                        // 4. Distance-based check (extra safety)
                        // Check if balloon is close enough to spike to warrant extra checks
                        float spikesLeft = spikes.getX();
                        float spikesRight = spikes.getX() + spikes.getWidth();

                        // If balloon is moving downward and crosses the spike's horizontal bounds
                        if (prevY > currentY && // Moving downward
                                ((prevX + Balloon.getBalloonWidth() < spikesLeft && currentX + Balloon.getBalloonWidth() >= spikesLeft)
                                        || // Moving right into spike
                                        (prevX > spikesRight && currentX <= spikesRight))) { // Moving left into spike

                            // If balloon is just above the spike, it's likely to collide next frame
                            if (Math.abs(currentY - (spikes.getY() + spikes.getHeight())) < Balloon.getBalloonWidth()) {
                                balloon.setActive(false);
                                System.out.println("Balloon collided with spike (predictive)");
                                audio.playSoundEffect("spikes");
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Determines if a line intersects with a rectangle
     * This helps detect if a fast-moving balloon passes through a spike
     */
    private boolean lineIntersectsRectangle(float x1, float y1, float x2, float y2, Rectangle rect) {
        // Check if the line intersects any of the rectangle's edges
        return lineLine(x1, y1, x2, y2, rect.x, rect.y, rect.x + rect.width, rect.y) ||
                lineLine(x1, y1, x2, y2, rect.x + rect.width, rect.y, rect.x + rect.width, rect.y + rect.height) ||
                lineLine(x1, y1, x2, y2, rect.x + rect.width, rect.y + rect.height, rect.x, rect.y + rect.height) ||
                lineLine(x1, y1, x2, y2, rect.x, rect.y + rect.height, rect.x, rect.y);
    }
    
    /**
     * Determines if two line segments intersect
     */
    private boolean lineLine(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        // Calculate the direction of the lines
        float denominator = ((y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1));

        // Lines are parallel or coincident
        if (denominator == 0) {
            return false;
        }

        float uA = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;
        float uB = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;

        // If uA and uB are between 0-1, lines are colliding
        return (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1);
    }

    // Set the score handler (useful for changing scenes)
    public void setScoreHandler(EntityScoreHandler scoreHandler) {
        this.scoreHandler = scoreHandler;
    }

    @Override
    public void dispose() {
        // Cleanup logic if necessary
    }
}