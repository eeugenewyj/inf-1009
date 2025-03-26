package com.mygdx.game;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Circle;
import com.mygdx.game.AbstractCollision.AbstractCollisionManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.IEntityManager;
import com.mygdx.game.AbstractIO.Audio;

public class GameCollisionManager extends AbstractCollisionManager {
    private final Audio audio = Audio.getInstance(); // Singleton instance of Audio class
    private GameScene gameScene; // Reference to GameScene for score updates
    private Circle tempCircle = new Circle(); // Reusable circle for collision detection

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
                        player.handleCollision((Tree) other);
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

            // After handling tree collisions, check for balloon collisions
            Ball collidedBall = null;
            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Ball && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    collidedBall = (Ball) other;
                    break; // Only detect first collision
                }
            }

            if (collidedBall != null) {
                // Get the balloon's value before processing collision
                int ballValue = collidedBall.getValue();

                // Add Ball's value to the Player's score
                player.handleCollision(collidedBall);

                // Update score in GameScene if available - add only this individual ball's
                // value
                if (gameScene != null) {
                    gameScene.addScore(ballValue);
                }

                // Call removeBallRow() from GameEntityManager to remove all balls in the same
                // row
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
                // Get the position where the effect should appear
                float effectX = collidedPowerUp.getX();
                float effectY = collidedPowerUp.getY();

                // Process power-up effect through the GameScene's PowerUpManager
                if (gameScene != null) {
                    gameScene.processPowerUp(collidedPowerUp.getType(), effectX, effectY);
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

        // Enhanced Ball and Tree collision with multi-layered approach
        if (entity instanceof Ball) {
            Ball ball = (Ball) entity;

            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Tree) {
                    Tree tree = (Tree) other;

                    // 1. Standard bounding box check
                    if (ball.getBoundingBox().overlaps(tree.getBoundingBox())) {
                        ball.setActive(false);
                        System.out.println("Ball collided with spike (direct hit)");
                        audio.playSoundEffect("tree");
                        return; // Exit early if collision detected
                    }

                    // 2. Circle-Rectangle intersection check
                    // This is more accurate for round balls
                    float ballCenterX = ball.getX() + Ball.getBallRadius();
                    float ballCenterY = ball.getY() + Ball.getBallRadius();
                    tempCircle.set(ballCenterX, ballCenterY, Ball.getBallRadius());

                    if (Intersector.overlaps(tempCircle, tree.getBoundingBox())) {
                        ball.setActive(false);
                        System.out.println("Ball collided with spike (circle-rect)");
                        audio.playSoundEffect("tree");
                        return; // Exit early if collision detected
                    }

                    // 3. Path-based collision detection
                    // Get previous position
                    float prevX = ball.getPreviousX();
                    float prevY = ball.getPreviousY();

                    // If previous position is valid (not 0,0)
                    if (prevX != 0 || prevY != 0) {
                        float currentX = ball.getX();
                        float currentY = ball.getY();

                        // Check if path intersects with spike
                        if (lineIntersectsRectangle(
                                prevX + Ball.getBallRadius(), prevY + Ball.getBallRadius(),
                                currentX + Ball.getBallRadius(), currentY + Ball.getBallRadius(),
                                tree.getBoundingBox())) {
                            ball.setActive(false);
                            System.out.println("Ball collided with spike (path intersection)");
                            audio.playSoundEffect("tree");
                            return; // Exit early if collision detected
                        }

                        // 4. Distance-based check (extra safety)
                        // Check if ball is close enough to spike to warrant extra checks
                        float treeLeft = tree.getX();
                        float treeRight = tree.getX() + tree.getWidth();

                        // If ball is moving downward and crosses the spike's horizontal bounds
                        if (prevY > currentY && // Moving downward
                                ((prevX + Ball.getBallWidth() < treeLeft && currentX + Ball.getBallWidth() >= treeLeft)
                                        || // Moving right into spike
                                        (prevX > treeRight && currentX <= treeRight))) { // Moving left into spike

                            // If ball is just above the spike, it's likely to collide next frame
                            if (Math.abs(currentY - (tree.getY() + tree.getHeight())) < Ball.getBallWidth()) {
                                ball.setActive(false);
                                System.out.println("Ball collided with spike (predictive)");
                                audio.playSoundEffect("tree");
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
     * This helps detect if a fast-moving ball passes through a spike
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

    @Override
    public void dispose() {
        // Cleanup logic if necessary
    }
}