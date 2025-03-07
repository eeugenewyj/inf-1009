package com.mygdx.game;

import com.mygdx.game.AbstractEntity.AbstractEntityManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractIO.IInputManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class GameEntityManager extends AbstractEntityManager {
    private static final Random random = new Random();

    public void spawnPlayer(float x, float y, float speed, IInputManager inputManager) {
        addEntity(new Player(x, y, speed, inputManager));
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

    public void spawnEnemy(float x, float y, float speed) {
        addEntity(new Enemy(x, y, speed));
    }

    // Spawn multiple enemies
    public void spawnEnemies(int count) {
        int maxWidth = Gdx.graphics.getWidth();
        int maxHeight = Gdx.graphics.getHeight();
        int enemySize = 50; // Adjust based on actual enemy size

        for (int i = 0; i < count; i++) {
            float x, y;
            boolean validPosition;

            do {
                // Randomly generates x and y within screen boundary
                validPosition = true;
                x = MathUtils.random(50, maxWidth - enemySize);
                y = MathUtils.random(50, maxHeight - enemySize);
                Rectangle newEnemyBounds = new Rectangle(x, y, enemySize, enemySize);

                // Check if new enemy position overlaps any existing enemies
                for (Entity entity : entities) {
                    if (entity instanceof Enemy && newEnemyBounds.overlaps(entity.getBoundingBox())) {
                        validPosition = false;
                        break;
                    }
                }

            } while (!validPosition); // Keep retrying until a valid position is found

            spawnEnemy(x, y, 200);
        }
    }

    public void spawnTree(float x, float y) {
        addEntity(new Tree(x, y));
    }
    // Spawn multiple trees
    public void spawnTrees(int count) {
        int maxWidth = Gdx.graphics.getWidth();
        int maxHeight = Gdx.graphics.getHeight();
        int treeSize = 50;

        for (int i = 0; i < count; i++) {
            float x, y;
            boolean validPosition;

            do {
                validPosition = true;
                x = random.nextInt(maxWidth - treeSize);
                y = random.nextInt(maxHeight - treeSize);
                Rectangle newTreeBounds = new Rectangle(x, y, treeSize, treeSize);

                // Check if tree overlaps with any existing players or enemies
                for (Entity entity : entities) {
                    if (newTreeBounds.overlaps(entity.getBoundingBox())) {
                        validPosition = false;
                        break;
                    }
                }
            } while (!validPosition); // Keep retrying until a valid position is found

            spawnTree(x , y);
        }
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
    }

    @Override
    public void dispose() {
        for (Entity entity : getEntities()) {
            entity.dispose();
        }
    }
}


