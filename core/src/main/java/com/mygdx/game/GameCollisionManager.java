package com.mygdx.game;

import com.mygdx.game.AbstractCollision.AbstractCollisionManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.IEntityManager;
import com.mygdx.game.AbstractIO.Audio;
//import com.badlogic.gdx.Gdx;

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
        // Handle Player and Ball collision
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Ball collidedBall = null;

            // Detect collision between Player and a Ball
            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Ball && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    collidedBall = (Ball) other;
                    break; // Only detect first collision
                }
            }

            if (collidedBall != null) {
                // Add Ball's value to the Player's score
                player.handleCollision(collidedBall);
                
                // Update score in GameScene if available
                if (gameScene != null) {
                    gameScene.addScore(collidedBall.getValue());
                }

                // Call removeBallRow() from GameEntityManager to remove all balls in the same
                // row
                ((GameEntityManager) entityManager).removeBallsRow(collidedBall);

                // Play collision sound
                audio.playSoundEffect("player");

                // Spawn the next row of balls
                ((GameEntityManager) entityManager).spawnBallsRow();
            }
        }

        // Handle Tree and Ball collision
        if (entity instanceof Ball) {
            Ball ball = (Ball) entity;

            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Tree && ball.getBoundingBox().overlaps(other.getBoundingBox())) {
                    ball.setActive(false); // Remove only the ball that touches the tree
                    break;
                }
            }
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;

            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Tree && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    player.setX(player.getPreviousX());
                    player.setY(player.getPreviousY());
                    break;
                }
            }
        }

        // Prevent players from moving into trees
        if (entity instanceof Player) {
            boolean isColliding = false;
            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Tree) {
                    Player player = (Player) entity;
                    Tree tree = (Tree) other;
                    if (player.getBoundingBox().overlaps(tree.getBoundingBox())) {
                        isColliding = true; // is colliding
                        if (player.hasCollided()) {
                            player.handleCollision(tree);
                            audio.playSoundEffect("tree");
                            player.setCollided(true);
                        }

                        // Move player back to prevent moving into the tree
                        player.setX(player.getPreviousX());
                        player.setY(player.getPreviousY());
                        break;
                    }
                }
            }
            // Reset `hasCollided` only when the entity moves away from all trees
            if (!isColliding && entity.hasCollided()) {
                entity.setCollided(false);
            }
        }
    }

    @Override
    public void dispose() {
        // Cleanup logic if necessary
    }
}