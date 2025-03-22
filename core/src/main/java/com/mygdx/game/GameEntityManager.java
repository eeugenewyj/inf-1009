package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;
import com.mygdx.game.AbstractEntity.AbstractEntityManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractIO.IInputManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import java.util.Random;
import java.util.ArrayList;
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
        Player player = new Player(x, y, speed, inputManager, this); // Pass this entity manager
        addEntity(player);
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
        
        // Just remove all balls in the same row
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

    // Spawn player at bottom center
    public void spawnPlayers(int count, IInputManager inputManager) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        int playerSize = 50; // Adjust based on actual player size
        
        // Calculate the bottom quarter position
        float bottomQuarterYMin = 0;
        float bottomQuarterYMax = screenHeight / 4;
        
        // Player starts in the middle-bottom of the screen
        float playerX = screenWidth / 2 - playerSize / 2; // Center horizontally
        float playerY = bottomQuarterYMax / 2; // Middle of bottom quarter
        
        spawnPlayer(playerX, playerY, 200, inputManager);
        System.out.println("Spawned Player at: " + playerX + ", " + playerY);
    }

    public void spawnTree(float x, float y) {
        addEntity(new Tree(x, y));
    }

    // Updated method to spawn trees anywhere on screen except near the player
    public void spawnTrees(int count) {
        // Get screen dimensions
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Find player position to avoid spawning trees on top of them
        Player player = null;
        for (Entity entity : getEntities()) {
            if (entity instanceof Player) {
                player = (Player) entity;
                break;
            }
        }
        
        // Buffer distance to keep between trees and player
        float safeDistance = 100; // Increased for better gameplay experience
        
        // Keep track of placed trees to avoid overlapping trees
        List<Rectangle> placedTreeBounds = new ArrayList<>();
        
        // Try to place each tree
        int treesPlaced = 0;
        int maxAttempts = 100; // Prevent infinite loops
        
        for (int i = 0; i < count; i++) {
            boolean validPosition = false;
            int attempts = 0;
            float x = 0, y = 0;
            
            while (!validPosition && attempts < maxAttempts) {
                // Generate random position ANYWHERE on screen
                x = MathUtils.random(50, screenWidth - 50);
                y = MathUtils.random(50, screenHeight - 50);
                
                // Create a rectangle for this potential tree position
                Rectangle potentialTreeBounds = new Rectangle(x, y, 50, 50);
                
                // Assume position is valid until proven otherwise
                validPosition = true;
                
                // Check if too close to player
                if (player != null) {
                    float playerDistance = Vector2.dst(x, y, player.getX(), player.getY());
                    if (playerDistance < safeDistance) {
                        validPosition = false;
                        attempts++;
                        continue;
                    }
                }
                
                // Check if too close to ball rows
                for (Entity entity : getEntities()) {
                    if (entity instanceof Ball && potentialTreeBounds.overlaps(entity.getBoundingBox())) {
                        validPosition = false;
                        break;
                    }
                }
                
                // Check if overlapping with existing trees
                for (Rectangle existingTree : placedTreeBounds) {
                    if (existingTree.overlaps(potentialTreeBounds)) {
                        validPosition = false;
                        break;
                    }
                }
                
                attempts++;
            }
            
            // If we found a valid position, place the tree
            if (validPosition) {
                Tree tree = new Tree(x, y);
                tree.setLifetime(TREE_LIFETIME);
                addEntity(tree);
                placedTreeBounds.add(new Rectangle(x, y, 50, 50));
                treesPlaced++;
            }
        }
        
        System.out.println("Spawned " + treesPlaced + " trees throughout the screen");
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