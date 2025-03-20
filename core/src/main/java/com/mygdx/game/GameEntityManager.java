package com.mygdx.game;

import com.mygdx.game.AbstractEntity.AbstractEntityManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractIO.IInputManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Rectangle;

import java.util.Random;
import java.util.Iterator;

public class GameEntityManager extends AbstractEntityManager {
    private static final Random random = new Random();
    private static final int NUM_BALLS = 8; // Number of balls per row
    private static final float GAP_RATIO = 0.1f; // 10% gap between balls

    private int rowsSpawned = 0; // Tracks the number of spawned rows

    private float treeSpawnTimer = 0; // Timer to track tree spawn intervals
    private static final float TREE_LIFETIME = 5f; // Trees disappear after 5 seconds
    private static final float TREE_SPAWN_INTERVAL = 5f; // Every 5 seconds

    public GameEntityManager() {
        // spawnBallsRow(); // Ensure the first row spawns at game start
    }

    public void spawnPlayer(float x, float y, float speed, IInputManager inputManager) {
        addEntity(new Player(x, y, speed, inputManager));
    }

    public void spawnBallsRow() {
        float startX = 0;
        float topYPosition = Gdx.graphics.getHeight() + (Ball.getBallWidth() / 2); // Place it just outside the screen

        for (int i = 0; i < NUM_BALLS; i++) {
            float xPosition = startX + i * (Ball.getBallWidth() * (1 + GAP_RATIO));
            addEntity(new Ball(xPosition, topYPosition));
        }

        rowsSpawned++;

        // Start falling animation for previous row
        makeBallsFall();
    }

    /**
     * Makes all active balls fall down.
     */
    private void makeBallsFall() {
        for (Entity entity : getEntities()) {
            if (entity instanceof Ball) {
                ((Ball) entity).moveAIControlled();
            }
        }
    }

    public void removeBallRow(Ball collidedBall) {
        float rowY = collidedBall.getY(); // Get Y position of the collided ball

        // Remove all balls in the same row
        Iterator<Entity> iterator = getEntities().iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            if (entity instanceof Ball && entity.getY() == rowY) {
                entity.setActive(false); // Mark ball as inactive
                iterator.remove(); // Remove ball from entity list
            }
        }
    }

    public void spawnPlayers(int count, IInputManager inputManager) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        int playerSize = 50; // Adjust based on actual player size

        // Define spawn zones (e.g., quadrants of the screen)
        float[][] spawnZones = {
                { 0, screenWidth / 2, screenHeight / 2, screenHeight }, // Top Left
                { screenWidth / 2, screenWidth, screenHeight / 2, screenHeight }, // Top Right
                { 0, screenWidth / 2, 0, screenHeight / 2 }, // Bottom Left
                { screenWidth / 2, screenWidth, 0, screenHeight / 2 } // Bottom Right
        };

        for (int i = 0; i < count; i++) {
            // Ensure max player count is 4 (Number of spawn zones)
            if (i >= spawnZones.length) {
                System.err.println("Too many players! Max allowed: " + spawnZones.length);
                break;
            }

            float[] zone = spawnZones[i];
            float playerX = MathUtils.random(zone[0] + playerSize, zone[1] - playerSize);
            float playerY = MathUtils.random(zone[2] + playerSize, zone[3] - playerSize);

            spawnPlayer(playerX, playerY, 200, inputManager);

            System.out.println("Spawned Player " + (i + 1) + " at: " + playerX + ", " + playerY);
        }

    }

    // public void spawnEnemy(float x, float y, float speed) {
    // addEntity(new Enemy(x, y, speed));
    // }

    // // Spawn multiple enemies
    // public void spawnEnemies(int count) {
    // int maxWidth = Gdx.graphics.getWidth();
    // int maxHeight = Gdx.graphics.getHeight();
    // int enemySize = 50; // Adjust based on actual enemy size

    // for (int i = 0; i < count; i++) {
    // float x, y;
    // boolean validPosition;

    // do {
    // // Randomly generates x and y within screen boundary
    // validPosition = true;
    // x = MathUtils.random(50, maxWidth - enemySize);
    // y = MathUtils.random(50, maxHeight - enemySize);
    // Rectangle newEnemyBounds = new Rectangle(x, y, enemySize, enemySize);

    // // Check if new enemy position overlaps any existing enemies
    // for (Entity entity : entities) {
    // if (entity instanceof Enemy &&
    // newEnemyBounds.overlaps(entity.getBoundingBox())) {
    // validPosition = false;
    // break;
    // }
    // }

    // } while (!validPosition); // Keep retrying until a valid position is found

    // spawnEnemy(x, y, 200);
    // }
    // }

    public void spawnTree(float x, float y) {
        addEntity(new Tree(x, y));
    }

    // Spawn multiple trees
    public void spawnTrees(int count) {
        for (int i = 0; i < count; i++) {
            float x = MathUtils.random(50, Gdx.graphics.getWidth() - 50);
            float y = MathUtils.random(50, Gdx.graphics.getHeight() - 50);
            Tree tree = new Tree(x, y);
            tree.setLifetime(TREE_LIFETIME); // Use the setter to set the tree lifetime
            addEntity(tree);
        }

        // int maxWidth = Gdx.graphics.getWidth();
        // int maxHeight = Gdx.graphics.getHeight();
        // int treeSize = 50;

        // for (int i = 0; i < count; i++) {
        // float x, y;
        // boolean validPosition;

        // do {
        // validPosition = true;
        // x = random.nextInt(maxWidth - treeSize);
        // y = random.nextInt(maxHeight - treeSize);
        // Rectangle newTreeBounds = new Rectangle(x, y, treeSize, treeSize);

        // // Check if tree overlaps with any existing players or enemies
        // for (Entity entity : entities) {
        // if (newTreeBounds.overlaps(entity.getBoundingBox())) {
        // validPosition = false;
        // break;
        // }
        // }
        // } while (!validPosition); // Keep retrying until a valid position is found

        // spawnTree(x, y);
        // }
    }

    public void updateEntities(float deltaTime) {
        // Removes inactive entities from list
        entities.removeIf(entity -> {
            if (!entity.isActive()) {
                entity.dispose();
                return true;
            }
            return false;
        });

        // Updates the behavior of each entity
        // : means "for each element in the list of"
        for (Entity entity : entities) {
            entity.update(deltaTime);
        }

        // Handle tree spawning timer
        treeSpawnTimer += deltaTime;
        if (treeSpawnTimer >= TREE_SPAWN_INTERVAL) {
            spawnTrees(4); // Spawn 4 trees
            treeSpawnTimer = 0; // Reset timer
        }

        // Remove trees that have existed longer than TREE_LIFETIME
        removeExpiredTrees(deltaTime);
    }

    private void removeExpiredTrees(float deltaTime) {
        for (Entity entity : getEntities()) {
            if (entity instanceof Tree) {
                Tree tree = (Tree) entity;
                tree.updateLifeTime(deltaTime);
                if (tree.isExpired()) {
                    entity.setActive(false);
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (Entity entity : getEntities()) {
            entity.dispose();
        }
    }
}
