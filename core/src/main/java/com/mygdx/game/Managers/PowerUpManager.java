package com.mygdx.game.Managers;

import com.mygdx.game.iSceneContext;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.Entities.Player;
import com.mygdx.game.PowerUps.PowerUpType;

/**
 * Manages power-ups and their effects in the game.
 * Decoupled from GameScene through the SceneContext interface.
 */
public class PowerUpManager {
    // Power-up states
    private boolean doublePointsActive = false;
    private float doublePointsTimer = 0;

    private boolean invertControlsActive = false;
    private float invertControlsTimer = 0;

    private boolean slowPlayerActive = false;
    private float slowPlayerTimer = 0;
    private float originalPlayerSpeed = 200f;

    // Reference to scene context for UI updates and entity management
    private iSceneContext sceneContext;
    
    // The game state manager is just used as a service, not creating a circular dependency
    private GameStateManager gameStateManager;

    /**
     * Creates a new PowerUpManager
     * 
     * @param sceneContext The scene context providing necessary callbacks and services
     * @param gameStateManager The game state manager for time and score manipulation
     */
    public PowerUpManager(iSceneContext sceneContext, GameStateManager gameStateManager) {
        this.sceneContext = sceneContext;
        this.gameStateManager = gameStateManager;
    }

    /**
     * Updates all active power-up timers
     * 
     * @param deltaTime The time elapsed since the last update
     */
    public void update(float deltaTime) {
        // Handle double points timer
        if (doublePointsActive) {
            doublePointsTimer += deltaTime;
            updatePowerUpLabel();

            if (doublePointsTimer >= PowerUpType.DOUBLE_POINTS.getDuration()) {
                doublePointsActive = false;
                updatePowerUpLabel();
                System.out.println("Double Points expired!");
            }
        }

        // Handle invert controls timer
        if (invertControlsActive) {
            invertControlsTimer += deltaTime;
            updatePowerUpLabel();

            if (invertControlsTimer >= PowerUpType.INVERT_CONTROLS.getDuration()) {
                invertControlsActive = false;
                // Reset invert flag on all players
                for (Entity entity : sceneContext.getEntityManager().getEntities()) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        player.setInvertControls(false);
                    }
                }
                updatePowerUpLabel();
                System.out.println("Controls back to normal!");
            }
        }

        // Handle slow player timer
        if (slowPlayerActive) {
            slowPlayerTimer += deltaTime;
            updatePowerUpLabel();

            if (slowPlayerTimer >= PowerUpType.SLOW_PLAYER.getDuration()) {
                slowPlayerActive = false;
                // Reset speed on all players
                for (Entity entity : sceneContext.getEntityManager().getEntities()) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        player.setSpeed(originalPlayerSpeed);
                    }
                }
                updatePowerUpLabel();
                System.out.println("Player speed back to normal!");
            }
        }
    }

    /**
     * Updates the power-up label to show active effects
     */
    private void updatePowerUpLabel() {
        String labelText = "";

        if (doublePointsActive) {
            labelText += "DOUBLE POINTS! ";
            float remaining = PowerUpType.DOUBLE_POINTS.getDuration() - doublePointsTimer;
            labelText += String.format("(%.1fs)", remaining);
        }

        if (invertControlsActive) {
            if (!labelText.isEmpty()) {
                labelText += " | ";
            }
            labelText += "INVERTED CONTROLS! ";
            float remaining = PowerUpType.INVERT_CONTROLS.getDuration() - invertControlsTimer;
            labelText += String.format("(%.1fs)", remaining);
        }

        if (slowPlayerActive) {
            if (!labelText.isEmpty()) {
                labelText += " | ";
            }
            labelText += "SLOWED! ";
            float remaining = PowerUpType.SLOW_PLAYER.getDuration() - slowPlayerTimer;
            labelText += String.format("(%.1fs)", remaining);
        }

        // Update the power-up label through the scene context
        sceneContext.updatePowerUpLabel(labelText);
    }

    /**
     * Activates double points for a fixed duration
     */
    public void activateDoublePoints() {
        doublePointsActive = true;
        doublePointsTimer = 0;
        updatePowerUpLabel();
        System.out.println("Double Points activated!");
    }

    /**
     * Checks if double points power-up is active
     * 
     * @return true if double points is active
     */
    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }

    /**
     * Extends the game time by the specified amount
     * 
     * @param seconds The number of seconds to add
     */
    public void extendGameTime(float seconds) {
        gameStateManager.extendGameTime(seconds);
        updatePowerUpLabel();
        System.out.println("Game time extended by " + seconds + " seconds!");
    }

    /**
     * Reduces the game time by the specified amount
     * 
     * @param seconds The number of seconds to subtract
     */
    public void reduceGameTime(float seconds) {
        gameStateManager.reduceGameTime(seconds);
        updatePowerUpLabel();
        System.out.println("Game time reduced by " + seconds + " seconds!");
    }

    /**
     * Activates inverted controls for a fixed duration
     */
    public void activateInvertControls() {
        invertControlsActive = true;
        invertControlsTimer = 0;

        // Set invert flag on all players
        for (Entity entity : sceneContext.getEntityManager().getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.setInvertControls(true);
            }
        }

        updatePowerUpLabel();
        System.out.println("Controls inverted!");
    }

    /**
     * Activates slow player movement for a fixed duration
     */
    public void activateSlowPlayer() {
        slowPlayerActive = true;
        slowPlayerTimer = 0;

        // Slow down all players
        for (Entity entity : sceneContext.getEntityManager().getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                originalPlayerSpeed = player.getSpeed();
                player.setSpeed(originalPlayerSpeed * 0.5f);
            }
        }

        updatePowerUpLabel();
        System.out.println("Player slowed!");
    }

    /**
     * Process power-up activation
     * 
     * @param powerUpType The type of power-up collected
     * @param x           X position for effect display
     * @param y           Y position for effect display
     */
    public void processPowerUp(PowerUpType powerUpType, float x, float y) {
        switch (powerUpType) {
            case DOUBLE_POINTS:
                activateDoublePoints();
                break;
            case EXTEND_TIME:
                extendGameTime(5f); // Add 5 seconds
                break;
            case REDUCE_TIME:
                reduceGameTime(3f); // Subtract 3 seconds
                break;
            case INVERT_CONTROLS:
                activateInvertControls();
                break;
            case SLOW_PLAYER:
                activateSlowPlayer();
                break;
        }

        // Create visual effect through the scene context
        sceneContext.createPowerUpEffect(powerUpType, x, y);
    }
    
    /**
     * Process power-up activation (overloaded for backward compatibility)
     * 
     * @param powerUpTypeId The integer ID of the power-up type
     * @param x           X position for effect display
     * @param y           Y position for effect display
     */
    public void processPowerUp(int powerUpTypeId, float x, float y) {
        // Convert old int type to new enum type for backward compatibility
        PowerUpType powerUpType;
        switch (powerUpTypeId) {
            case 0: // Old TYPE_DOUBLE_POINTS
                powerUpType = PowerUpType.DOUBLE_POINTS;
                break;
            case 1: // Old TYPE_EXTEND_TIME
                powerUpType = PowerUpType.EXTEND_TIME;
                break;
            case 2: // Old TYPE_REDUCE_TIME
                powerUpType = PowerUpType.REDUCE_TIME;
                break;
            case 3: // Old TYPE_INVERT_CONTROLS
                powerUpType = PowerUpType.INVERT_CONTROLS;
                break;
            case 4: // Old TYPE_SLOW_PLAYER
                powerUpType = PowerUpType.SLOW_PLAYER;
                break;
            default:
                System.out.println("Unknown power-up type ID: " + powerUpTypeId);
                return;
        }
        
        processPowerUp(powerUpType, x, y);
    }

    /**
     * Resets all power-up states
     */
    public void resetPowerUps() {
        doublePointsActive = false;
        doublePointsTimer = 0;
        invertControlsActive = false;
        invertControlsTimer = 0;
        slowPlayerActive = false;
        slowPlayerTimer = 0;

        // Reset any player modifications
        GameEntityManager entityManager = sceneContext.getEntityManager();
        if (entityManager != null) {
            for (Entity entity : entityManager.getEntities()) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    player.setInvertControls(false);
                    player.setSpeed(originalPlayerSpeed);
                }
            }
        }

        updatePowerUpLabel();
    }
}