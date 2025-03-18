package com.mygdx.game;

import com.mygdx.game.AbstractCollision.AbstractCollisionManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.IEntityManager;
import com.mygdx.game.AbstractIO.Audio;
//import com.badlogic.gdx.Gdx;

public class GameCollisionManager extends AbstractCollisionManager {
    private final Audio audio = Audio.getInstance();

    public GameCollisionManager(IEntityManager entityManager) { // Fixed: Uses AbstractEntityManager
        super(entityManager);
    }

    @Override
    protected void handleCollision(Entity entity) {
        // Handle Player and Ball collision
        if (entity instanceof Player) {
            Player player = (Player) entity;

            for (Entity other : entityManager.getEntities()) {
                if (other instanceof Ball && player.getBoundingBox().overlaps(other.getBoundingBox())) {
                    Ball ball = (Ball) other;

                    // Add Ball's value to the Player's score
                    player.handleCollision(ball);

                    // Remove the Ball from the game
                    ball.setActive(false);

                    // Play collision sound
                    audio.playSoundEffect("player");

                    break; // Exit loop after first collision
                }
            }
        }
        // // Check for collisions between player and enemy
        // if (entity instanceof Enemy) {
        // boolean hasCollided = false;
        // for (Entity other : entityManager.getEntities()) {
        // if (other instanceof Player) {
        // Enemy enemy = (Enemy) entity;
        // Player player = (Player) other;
        // if (enemy.getBoundingBox().overlaps(player.getBoundingBox())) {
        // if (!enemy.hasCollided()) { // Print only the first time per new collision
        // enemy.handleCollision(player);
        // audio.playSoundEffect("player"); // This will print "Enemy collided with
        // Player!"
        // enemy.setCollided(true);
        // }
        // hasCollided = true;
        // break; // Stop checking once an enemy collides with one player
        // }
        // }
        // }
        // if (!hasCollided) {
        // entity.setCollided(false);
        // }
        // }

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

        // // Check for collision between enemy and tree
        // if (entity instanceof Enemy) {
        // for (Entity other : entityManager.getEntities()) {
        // if (other instanceof Tree) {
        // Enemy enemy = (Enemy) entity;
        // Tree tree = (Tree) other;
        // if (enemy.getBoundingBox().overlaps(tree.getBoundingBox())) {
        // enemy.handleCollision(tree);
        // // Reverse movement direction (bounce effect)
        // enemy.bounce(tree);
        // }
        // }
        // }
        // }

        // //check if enemy hit the display edge
        // if (entity instanceof Enemy) {
        // Enemy enemy = (Enemy) entity;
        // if (enemy.getX() <= 0 || enemy.getX() + enemy.getWidth() >=
        // Gdx.graphics.getWidth()) {
        // enemy.reverseXDirection();
        // }
        // if (enemy.getY() <= 0 || enemy.getY() + enemy.getHeight() >=
        // Gdx.graphics.getHeight()) {
        // enemy.reverseYDirection();
        // }
        // }
    }

    @Override
    public void dispose() {
        // Cleanup logic if necessary
    }
}
