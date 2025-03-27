package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractIO.IInputManager;

/**
 * Handles the main game loop logic including entity updates and collision detection
 */
public class GameLoopHandler {
    private GameEntityManager entityManager;
    private GameCollisionManager collisionManager;
    private GameStateManager gameStateManager;
    private PowerUpManager powerUpManager;
    private IInputManager inputManager;
    
    private boolean gameActive = true;

    /**
     * Creates a new game loop handler
     * 
     * @param entityManager The entity manager
     * @param collisionManager The collision manager
     * @param gameStateManager The game state manager
     * @param powerUpManager The power-up manager
     * @param inputManager The input manager
     */
    public GameLoopHandler(
            GameEntityManager entityManager, 
            GameCollisionManager collisionManager, 
            GameStateManager gameStateManager,
            PowerUpManager powerUpManager,
            IInputManager inputManager) {
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
        this.gameStateManager = gameStateManager;
        this.powerUpManager = powerUpManager;
        this.inputManager = inputManager;
    }
    
    /**
     * Updates the game state for a single frame
     * 
     * @param delta The time elapsed since the last update
     * @return true if the game is still active, false if game over
     */
    public boolean update(float delta) {
        if (!gameActive) {
            return false;
        }
        
        // Update game state
        boolean stillActive = gameStateManager.update(delta);
        
        if (stillActive) {
            // Update power-ups
            powerUpManager.update(delta);
            
            // Update player movement
            updatePlayers(delta);
            
            // Update entities
            entityManager.updateEntities(delta);
            
            // Collision Detection
            if (!entityManager.getEntities().isEmpty()) {
                collisionManager.detectCollisions();
            }
            
            // Handle escape key
            if (inputManager.isActionPressed(Input.Keys.ESCAPE)) {
                gameStateManager.pauseGame();
                return false; // Signal to open pause menu
            }
        } else {
            // Game is no longer active (game over)
            return false;
        }
        
        return true;
    }
    
    /**
     * Updates all player entities
     * 
     * @param delta The time elapsed since the last update
     */
    private void updatePlayers(float delta) {
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player) {
                ((Player) entity).moveUserControlled(delta);
            }
        }
    }
    
    /**
     * Sets whether the game is active
     * 
     * @param active Whether the game is active
     */
    public void setGameActive(boolean active) {
        this.gameActive = active;
    }
    
    /**
     * Checks if the game is active
     * 
     * @return true if the game is active
     */
    public boolean isGameActive() {
        return gameActive;
    }
    
    /**
     * Initializes a new game
     */
    public void initializeGame() {
        // Reset game state
        gameActive = true;
        
        // Completely clear all entities before spawning new ones
        for (Entity entity : new ArrayList<>(entityManager.getEntities())) {
            entityManager.removeEntity(entity);
        }
        
        // Spawn entities
        entityManager.spawnPlayers(1, inputManager);
        entityManager.spawnSpikes(4);
        entityManager.spawnBalloonsRow();
    }
    
    /**
     * Restarts the game
     */
    public void restartGame() {
        // Reset game state
        gameActive = true;
        gameStateManager.restartGame();
        
        // Reset power-ups
        powerUpManager.resetPowerUps();
        
        // Reset entities
        if (entityManager != null) {
            entityManager.dispose();
        }
        
        // Initialize a new game
        initializeGame();
        
        System.out.println("Game restarted!");
    }
}