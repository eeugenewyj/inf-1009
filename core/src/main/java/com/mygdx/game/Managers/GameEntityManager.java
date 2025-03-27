package com.mygdx.game.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.iCollisionCallback;
import com.mygdx.game.iEntityScoreHandler;
import com.mygdx.game.AbstractEntity.AbstractEntityManager;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractIO.iInputManager;
import com.mygdx.game.Entities.Balloon;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.Entities.PowerUp;
import com.mygdx.game.Entities.Spikes;
import com.mygdx.game.PowerUps.PowerUpType;

public class GameEntityManager extends AbstractEntityManager implements iCollisionCallback {
    // This class now implements CollisionCallback to provide collision detection for the Player
    private static final Random random = new Random();
    private static final int NUM_BALLOONS = 8; // Number of balloons per row
    private static final float GAP_RATIO = 0.1f; // 10% gap between balloons

    private int rowsSpawned = 0; // Tracks the number of spawned rows
    private iEntityScoreHandler scoreHandler; // Reference to score handler instead of GameScene

    private float spikesSpawnTimer = 0; // Timer to track spikes spawn intervals
    private static final float SPIKES_LIFETIME = 5f; // Spikes disappear after 5 seconds
    private static final float SPIKES_SPAWN_INTERVAL = 5f; // Every 5 seconds

    // Power-up related fields
    private float powerUpSpawnTimer = 0;
    private static final float POWERUP_SPAWN_INTERVAL = 3.5f; // Every 3.5 seconds
    private static final float POWERUP_SPAWN_CHANCE = 0.9f; // 90% chance to spawn

    public GameEntityManager() {
        // Default constructor
    }

    // Constructor with EntityScoreHandler reference
    public GameEntityManager(iEntityScoreHandler scoreHandler) {
        this.scoreHandler = scoreHandler;
    }

    public void spawnPlayer(float x, float y, float speed, iInputManager inputManager) {
        // Create Player with the new constructor (without EntityManager)
        Player player = new Player(x, y, speed, inputManager);
        
        // Set this manager as the collision callback
        player.setCollisionCallback(this);
        
        // Add player to entities
        addEntity(player);
    }

    public void spawnBalloonsRow() {
        float startX = 0; // Starting X position
        float topYPosition = Gdx.graphics.getHeight() + (Balloon.getBalloonWidth() / 2); // Place it just outside the screen

        // Spawn balloons in a row
        for (int i = 0; i < NUM_BALLOONS; i++) {
            float xPosition = startX + i * (Balloon.getBalloonWidth() * (1 + GAP_RATIO));
            addEntity(new Balloon(xPosition, topYPosition));
        }

        rowsSpawned++;

        // Start falling animation for previous row
        makeBalloonsFall();
    }

    // Makes all active balloons fall down
    private void makeBalloonsFall() {
        for (Entity entity : getEntities()) {
            if (entity instanceof Balloon) {
                ((Balloon) entity).moveAIControlled();
            }
        }
    }

    // Remove all balloons in the same row as the collided balloon
    public void removeBalloonRow(Balloon collidedBalloon) {
        float rowY = collidedBalloon.getY(); // Get Y position of the collided balloon

        // Just remove all balloons in the same row
        List<Balloon> balloonsToRemove = new ArrayList<>();

        // Identify all balloons to remove
        for (Entity entity : getEntities()) {
            if (entity instanceof Balloon && entity.getY() == rowY) {
                Balloon balloon = (Balloon) entity;
                balloonsToRemove.add(balloon);
            }
        }

        // Remove the balloons
        for (Balloon balloon : balloonsToRemove) {
            balloon.setActive(false);
        }
    }

    // Remove rows that reached the bottom and spawns new row
    private void removeRowIfAtBottomAndSpawn() {
        float bottomThreshold = 0; // bottom of the screen

        List<Float> rowYs = new ArrayList<>();
        for (Entity e : getEntities()) {
            if (e instanceof Balloon) {
                float y = e.getY();
                if (!rowYs.contains(y))
                    rowYs.add(y);
            }
        }

        for (float rowY : rowYs) {
            List<Balloon> rowBalloons = new ArrayList<>();
            for (Entity e : getEntities()) {
                if (e instanceof Balloon && e.getY() == rowY) {
                    rowBalloons.add((Balloon) e);
                }
            }

            if (!rowBalloons.isEmpty() && rowBalloons.get(0).getY() <= bottomThreshold) {
                // Remove and spawn
                for (Balloon balloon : rowBalloons) {
                    balloon.setActive(false);
                }
                spawnBalloonsRow();
            }
        }
    }

    // Spawn player at bottom center
    public void spawnPlayers(int count, iInputManager inputManager) {
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

    public void spawnSpikes(float x, float y) {
        addEntity(new Spikes(x, y));
    }

    // Updated method to spawn spikes anywhere on screen except near the player
    public void spawnSpikes(int count) {
        // Get screen dimensions
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Calculate the top quarter boundary - spikes won't spawn above this line
        float topQuarterBoundary = screenHeight * 0.75f;

        // Find player position to avoid spawning spikes on top of them
        Player player = null;
        for (Entity entity : getEntities()) {
            if (entity instanceof Player) {
                player = (Player) entity;
                break;
            }
        }

        // Buffer distance to keep between spikes and player
        float safeDistance = 100;

        // Keep track of placed spikes to avoid overlapping spikes
        List<Rectangle> placedSpikesBounds = new ArrayList<>();

        // Try to place each spike
        int spikesPlaced = 0;
        int maxAttempts = 100; // Prevent infinite loops

        for (int i = 0; i < count; i++) {
            boolean validPosition = false;
            int attempts = 0;
            float x = 0, y = 0;

            while (!validPosition && attempts < maxAttempts) {
                // Generate random position but restrict Y to be below the top quarter
                x = MathUtils.random(50, screenWidth - 50);
                y = MathUtils.random(50, topQuarterBoundary - 50); // Keep below top quarter boundary

                // Create a rectangle for this potential spike position
                Rectangle potentialSpikesBounds = new Rectangle(x, y, 50, 50);

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

                // Check if too close to balloon rows
                for (Entity entity : getEntities()) {
                    if (entity instanceof Balloon && potentialSpikesBounds.overlaps(entity.getBoundingBox())) {
                        validPosition = false;
                        break;
                    }
                }

                // Check if overlapping with existing spikes
                for (Rectangle existingSpikes : placedSpikesBounds) {
                    if (existingSpikes.overlaps(potentialSpikesBounds)) {
                        validPosition = false;
                        break;
                    }
                }

                attempts++;
            }

            // If we found a valid position, place the spikes
            if (validPosition) {
                Spikes spikes = new Spikes(x, y);
                spikes.setLifetime(SPIKES_LIFETIME);
                addEntity(spikes);
                placedSpikesBounds.add(new Rectangle(x, y, 50, 50));
                spikesPlaced++;
            }
        }

        System.out.println("Spawned " + spikesPlaced + " spikes below the top quarter of the screen");
    }
    
    // Method to spawn a power-up
    public void spawnPowerUp() {
        float screenWidth = Gdx.graphics.getWidth();
        float topYPosition = Gdx.graphics.getHeight() + 20; // Just above screen

        // Randomly position the power-up horizontally
        float xPosition = MathUtils.random(50, screenWidth - 50);

        PowerUp powerUp = PowerUp.createRandomPowerUp(xPosition, topYPosition);
        addEntity(powerUp);

        System.out.println("Spawned PowerUp of type: " +
        (powerUp.getType() == PowerUpType.DOUBLE_POINTS ? "Double Points" : 
         powerUp.getType() == PowerUpType.EXTEND_TIME ? "Extend Time" :
         powerUp.getType() == PowerUpType.REDUCE_TIME ? "Reduce Time" :
         powerUp.getType() == PowerUpType.INVERT_CONTROLS ? "Invert Controls" :
         powerUp.getType() == PowerUpType.SLOW_PLAYER ? "Slow Player" :
         "Unknown"));
    }

    @Override
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

        // Handle spikes spawning timer
        spikesSpawnTimer += deltaTime;
        if (spikesSpawnTimer >= SPIKES_SPAWN_INTERVAL) {
            spawnSpikes(4); // Spawn 4 spikes
            spikesSpawnTimer = 0; // Reset timer
        }

        // Handle power-up spawning timer
        powerUpSpawnTimer += deltaTime;
        if (powerUpSpawnTimer >= POWERUP_SPAWN_INTERVAL) {
            // Chance-based spawning
            if (MathUtils.random() < POWERUP_SPAWN_CHANCE) {
                spawnPowerUp();
            }
            powerUpSpawnTimer = 0; // Reset timer
        }

        // Remove spikes that have existed longer than SPIKES_LIFETIME
        removeExpiredSpikes(deltaTime);
        removeRowIfAtBottomAndSpawn();
    }

    private void removeExpiredSpikes(float deltaTime) {
        for (Entity entity : getEntities()) {
            if (entity instanceof Spikes) {
                Spikes spikes = (Spikes) entity;
                spikes.updateLifeTime(deltaTime);
                if (spikes.isExpired()) {
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

    // Set EntityScoreHandler reference after initialization if needed
    public void setScoreHandler(iEntityScoreHandler scoreHandler) {
        this.scoreHandler = scoreHandler;
    }
    
    // Implementation of CollisionCallback interface
    @Override
    public boolean wouldCollideWithSpikes(float x, float y, float width, float height) {
        // Create a temporary rectangle at the potential new position
        Rectangle potentialPosition = new Rectangle(x, y, width, height);

        // Check for spikes collisions
        for (Entity entity : entities) {
            if (entity instanceof Spikes && potentialPosition.overlaps(entity.getBoundingBox())) {
                return true; // Would collide with a spike
            }
        }
        
        return false; // No collision would occur
    }
}