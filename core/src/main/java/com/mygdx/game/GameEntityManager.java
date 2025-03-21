package com.mygdx.game;

import com.mygdx.game.AbstractEntity.AbstractEntityManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractIO.IInputManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Rectangle;

import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEntityManager extends AbstractEntityManager {
    private static final Random random = new Random();
    private static final int NUM_BALLS = 8; // Number of balls per row
    private static final float GAP_RATIO = 0.1f; // 10% gap between balls

    private int rowsSpawned = 0; // Tracks the number of spawned rows
    private GameScene gameScene; // Reference to GameScene

    private float treeSpawnTimer = 0; // Timer to track tree spawn intervals
    private static final float TREE_LIFETIME = 5f; // Trees disappear after 5 seconds
    private static final float TREE_SPAWN_INTERVAL = 5f; // Every 5 seconds

    public GameEntityManager() {
        // Default constructor
    }
    
    // Constructor with GameScene reference
    public GameEntityManager(GameScene gameScene) {
        this.gameScene = gameScene;
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

    /* Makes all active balls fall down. */
    private void makeBallsFall() {
        for (Entity entity : getEntities()) {
            if (entity instanceof Ball) {
                ((Ball) entity).moveAIControlled();
            }
        }
    }

    public void removeBallsRow(Ball collidedBall) {
        float rowY = collidedBall.getY(); // Get Y position of the collided ball
        
        // Just remove all balls in the same row (don't calculate total value)
        List<Ball> ballsToRemove = new ArrayList<>();
        
        // Identify all balls to remove
        for (Entity entity : getEntities()) {
            if (entity instanceof Ball && entity.getY() == rowY) {
                Ball ball = (Ball) entity;
                ballsToRemove.add(ball);
            }
        }
        
        // Remove the balls
        for (Ball ball : ballsToRemove) {
            ball.setActive(false);
        }
        
        // Note: We don't update the score here anymore - that's handled in GameCollisionManager
    }

    private void removeRowIfAtBottomAndSpawn() {
        float bottomThreshold = 0; // bottom of the screen

        List<Float> rowYs = new ArrayList<>();
        for (Entity e : getEntities()) {
            if (e instanceof Ball) {
                float y = e.getY();
                if (!rowYs.contains(y))
                    rowYs.add(y);
            }
        }

        for (float rowY : rowYs) {
            List<Ball> rowBalls = new ArrayList<>();
            for (Entity e : getEntities()) {
                if (e instanceof Ball && e.getY() == rowY) {
                    rowBalls.add((Ball) e);
                }
            }

            if (!rowBalls.isEmpty() && rowBalls.get(0).getY() <= bottomThreshold) {
                // Remove and spawn
                for (Ball ball : rowBalls) {
                    ball.setActive(false);
                }
                spawnBallsRow();
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
        removeRowIfAtBottomAndSpawn();
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
    
    // Set GameScene reference after initialization if needed
    public void setGameScene(GameScene gameScene) {
        this.gameScene = gameScene;
    }
}