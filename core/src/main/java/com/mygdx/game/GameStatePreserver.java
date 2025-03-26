package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.MovableEntity;

/**
 * Singleton class to preserve game state between scene transitions
 */
public class GameStatePreserver {
    private static GameStatePreserver instance; // Singleton instance of the GameStatePreserver

    // Game state variables
    private boolean hasPreservedState = false; // Flag to indicate if a state has been preserved
    private int playerScore = 0;
    private float gameTimer = 0;
    private boolean doublePointsActive = false; // Whether the double points power-up is active
    private float doublePointsTimer = 0; // Timer for the double points power-up
    private boolean invertControlsActive = false; // Whether the invert controls power-up is active
    private float invertControlsTimer = 0; // Timer for the invert controls power-up
    private boolean slowPlayerActive = false; // Whether the slow player power-up is active
    private float slowPlayerTimer = 0; // Timer for the slow player power-up
    
    // New fields to store entity states
    private Map<Integer, EntityState> entityStates = new HashMap<>();
    private float playerSpeed = 200f; // Default player speed
    
    // Class to store entity state data
    private static class EntityState {
        private final String className; // Entity class name for type identification
        private final float x, y; // Position
        private final boolean isActive; // Active state
        private final float width, height; // Dimensions
        
        // Additional properties for specific entity types
        private final Float speed; // For MovableEntity
        private final Integer value; // For Balloon
        private final String displayText; // For Balloon
        private final Boolean usesMathOperation; // For Balloon
        private final Integer balloonColorIndex; // For Balloon's color
        private final Boolean invertControls; // For Player
        private final Float lifeTime; // For Spikes
        
        public EntityState(Entity entity) {
            this.className = entity.getClass().getSimpleName();
            this.x = entity.getX();
            this.y = entity.getY();
            this.isActive = entity.isActive();
            this.width = entity.getWidth();
            this.height = entity.getHeight();
            
            // Store specific properties based on entity type
            if (entity instanceof MovableEntity) {
                this.speed = ((MovableEntity) entity).getSpeed();
                
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    this.invertControls = player.areControlsInverted();
                } else {
                    this.invertControls = null;
                }
            } else {
                this.speed = null;
                this.invertControls = null;
            }
            
            if (entity instanceof Balloon) {
                Balloon balloon = (Balloon) entity;
                this.value = balloon.getValue();
                this.displayText = balloon.getDisplayText();
                this.usesMathOperation = balloon.usesMathOperation();
                this.balloonColorIndex = balloon.getBalloonColorIndex();
            } else {
                this.value = null;
                this.displayText = null;
                this.usesMathOperation = null;
                this.balloonColorIndex = null;
            }
            
            if (entity instanceof Spikes) {
                Spikes spikes = (Spikes) entity;
                this.lifeTime = spikes.getLifeTime();
            } else {
                this.lifeTime = null;
            }
        }
    }

    // Private constructor for singleton
    private GameStatePreserver() {
        // Private constructor for singleton
    }

    public static GameStatePreserver getInstance() {
        if (instance == null) {
            instance = new GameStatePreserver(); // Create a new instance if it doesn't exist
        }
        return instance;
    }

    // Stores the current game state
    public void preserveGameState(GameScene gameScene, GameStateManager stateManager, PowerUpManager powerUpManager) {
        this.playerScore = stateManager.getPlayerScore();
        this.gameTimer = stateManager.getGameTimer();
        this.doublePointsActive = powerUpManager.isDoublePointsActive();
        this.invertControlsActive = false; // Reset inverted controls on pause
        this.slowPlayerActive = false; // Reset slow player on pause
        
        // Save entity states
        entityStates.clear();
        GameEntityManager entityManager = gameScene.getEntityManager();
        
        // Store current player speed if there is a player
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                playerSpeed = player.getSpeed();
                break;
            }
        }
        
        // Store all entity states
        int entityId = 0;
        for (Entity entity : entityManager.getEntities()) {
            entityStates.put(entityId++, new EntityState(entity));
        }

        System.out.println("Game state preserved: Score=" + playerScore + ", Timer=" + gameTimer + 
                           ", Entity count=" + entityStates.size());
        this.hasPreservedState = true;
    }

    // Determines if there's a preserved state to restore
    public boolean hasPreservedState() {
        return hasPreservedState;
    }

    // Clears the preserved state (e.g., when starting a new game)
    public void clearPreservedState() {
        hasPreservedState = false;
        playerScore = 0;
        gameTimer = 0;
        doublePointsActive = false;
        doublePointsTimer = 0;
        invertControlsActive = false;
        invertControlsTimer = 0;
        slowPlayerActive = false;
        slowPlayerTimer = 0;
        entityStates.clear();

        System.out.println("Game state cleared");
    }

    // Restores the preserved state to the given objects
    public void restoreGameState(GameScene gameScene, GameStateManager stateManager, PowerUpManager powerUpManager) {
        if (!hasPreservedState) {
            System.out.println("No state to restore");
            return;
        }

        // Restore state to the managers
        stateManager.setPlayerScore(playerScore);
        stateManager.setGameTimer(gameTimer);
        if (doublePointsActive) {
            powerUpManager.activateDoublePoints();
        }
        
        // First, clear all existing entities
        GameEntityManager entityManager = gameScene.getEntityManager();
        
        // Make a copy of entities to avoid concurrent modification
        List<Entity> entitiesToRemove = new ArrayList<>(entityManager.getEntities());
        for (Entity entity : entitiesToRemove) {
            entityManager.removeEntity(entity);
        }
        
        // Restore all saved entities
        for (EntityState state : entityStates.values()) {
            Entity entity = createEntityFromState(state, entityManager, gameScene);
            if (entity != null) {
                entityManager.addEntity(entity);
            }
        }

        System.out.println("Game state restored: Score=" + playerScore + ", Timer=" + gameTimer + 
                           ", Entity count=" + entityStates.size());
    }

    // Helper method to create entities from saved state
    private Entity createEntityFromState(EntityState state, GameEntityManager entityManager, GameScene gameScene) {
        Entity entity = null;
        
        switch (state.className) {
            case "Player":
                // Create Player without EntityManager, then set callbacks
                Player player = new Player(state.x, state.y, playerSpeed, gameScene.getInputManager());
                player.setCollisionCallback(entityManager);
                if (state.invertControls != null && state.invertControls) {
                    player.setInvertControls(true);
                }
                entity = player;
                break;
                
            case "Balloon":
                // Create a new balloon at the same position with the SAME VALUE and DISPLAY TEXT
                if (state.value != null && state.displayText != null && state.usesMathOperation != null) {
                    Balloon balloon = new Balloon(
                        state.x, 
                        state.y, 
                        state.value, 
                        state.displayText, 
                        state.usesMathOperation
                    );
                    
                    // Restore balloon color if available
                    if (state.balloonColorIndex != null) {
                        balloon.setBalloonColor(state.balloonColorIndex);
                    }
                    
                    entity = balloon;
                } else {
                    // Fallback to default constructor if we don't have all the data
                    Balloon balloon = new Balloon(state.x, state.y);
                    entity = balloon;
                }
                break;
                
            case "Spikes":
                Spikes spikes = new Spikes(state.x, state.y);
                if (state.lifeTime != null) {
                    spikes.setLifetime(state.lifeTime);
                }
                entity = spikes;
                break;
                
            case "PowerUp":
                // Based on position, recreate power-ups (simplified)
                entity = PowerUp.createRandomPowerUp(state.x, state.y);
                break;
                
            case "PowerUpEffect":
                // Visual effects don't need to be restored
                break;
                
            default:
                System.out.println("Unknown entity type: " + state.className);
        }
        
        if (entity != null) {
            entity.setActive(state.isActive);
        }
        
        return entity;
    }

    // Getters for state variables
    public int getPlayerScore() {
        return playerScore;
    }

    public float getGameTimer() {
        return gameTimer;
    }

    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }
}