package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.AbstractEntity.Entity;

/**
 * Manages power-ups and their effects in the game
 */
public class PowerUpManager {
    // Power-up states
    private boolean doublePointsActive = false;
    private float doublePointsTimer = 0;
    private static final float DOUBLE_POINTS_DURATION = 3f;
    
    private boolean invertControlsActive = false;
    private float invertControlsTimer = 0;
    private static final float INVERT_CONTROLS_DURATION = 5f;
    
    private boolean slowPlayerActive = false;
    private float slowPlayerTimer = 0;
    private static final float SLOW_PLAYER_DURATION = 4f;
    private float originalPlayerSpeed = 200f;
    
    // Reference to game scene for UI updates and entity management
    private GameScene gameScene;
    private GameStateManager gameStateManager;
    
    /**
     * Creates a new PowerUpManager
     * @param gameScene The game scene this manager will work with
     * @param gameStateManager The game state manager
     */
    public PowerUpManager(GameScene gameScene, GameStateManager gameStateManager) {
        this.gameScene = gameScene;
        this.gameStateManager = gameStateManager;
    }
    
    /**
     * Updates all active power-up timers
     * @param deltaTime The time elapsed since the last update
     */
    public void update(float deltaTime) {
        // Handle double points timer
        if (doublePointsActive) {
            doublePointsTimer += deltaTime;
            updatePowerUpLabel();
            
            if (doublePointsTimer >= DOUBLE_POINTS_DURATION) {
                doublePointsActive = false;
                updatePowerUpLabel();
                System.out.println("Double Points expired!");
            }
        }
        
        // Handle invert controls timer
        if (invertControlsActive) {
            invertControlsTimer += deltaTime;
            updatePowerUpLabel();
            
            if (invertControlsTimer >= INVERT_CONTROLS_DURATION) {
                invertControlsActive = false;
                // Reset invert flag on all players
                for (Entity entity : gameScene.getEntityManager().getEntities()) {
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
            
            if (slowPlayerTimer >= SLOW_PLAYER_DURATION) {
                slowPlayerActive = false;
                // Reset speed on all players
                for (Entity entity : gameScene.getEntityManager().getEntities()) {
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
            float remaining = DOUBLE_POINTS_DURATION - doublePointsTimer;
            labelText += String.format("(%.1fs)", remaining);
        }
        
        if (invertControlsActive) {
            if (!labelText.isEmpty()) {
                labelText += " | ";
            }
            labelText += "INVERTED CONTROLS! ";
            float remaining = INVERT_CONTROLS_DURATION - invertControlsTimer;
            labelText += String.format("(%.1fs)", remaining);
        }
        
        if (slowPlayerActive) {
            if (!labelText.isEmpty()) {
                labelText += " | ";
            }
            labelText += "SLOWED! ";
            float remaining = SLOW_PLAYER_DURATION - slowPlayerTimer;
            labelText += String.format("(%.1fs)", remaining);
        }
        
        // Update the power-up label in GameScene
        gameScene.updatePowerUpLabel(labelText);
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
     * @return true if double points is active
     */
    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }
    
    /**
     * Extends the game time by the specified amount
     * @param seconds The number of seconds to add
     */
    public void extendGameTime(float seconds) {
        gameStateManager.extendGameTime(seconds);
        updatePowerUpLabel();
        System.out.println("Game time extended by " + seconds + " seconds!");
    }
    
    /**
     * Reduces the game time by the specified amount
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
        for (Entity entity : gameScene.getEntityManager().getEntities()) {
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
        for (Entity entity : gameScene.getEntityManager().getEntities()) {
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
     * Creates a visual power-up effect
     * @param powerUpType The type of power-up
     * @param x The x position for the effect
     * @param y The y position for the effect
     */
    public void createPowerUpEffect(int powerUpType, float x, float y) {
        PowerUpEffect effect = null;
        
        switch (powerUpType) {
            case PowerUp.TYPE_DOUBLE_POINTS:
                effect = PowerUpEffect.createDoublePointsEffect(x, y);
                break;
            case PowerUp.TYPE_EXTEND_TIME:
                effect = PowerUpEffect.createTimeExtensionEffect(x, y);
                break;
            case PowerUp.TYPE_REDUCE_TIME:
                effect = PowerUpEffect.createEffect(x, y, "-3 SECONDS!", Color.RED, 2.0f);
                break;
            case PowerUp.TYPE_INVERT_CONTROLS:
                effect = PowerUpEffect.createEffect(x, y, "CONTROLS INVERTED!", Color.PURPLE, 2.0f);
                break;
            case PowerUp.TYPE_SLOW_PLAYER:
                effect = PowerUpEffect.createEffect(x, y, "SPEED REDUCED!", Color.ORANGE, 2.0f);
                break;
        }
        
        if (effect != null) {
            gameScene.getEntityManager().addEntity(effect);
        }
    }
    
    /**
     * Process power-up activation
     * @param powerUpType The type of power-up collected
     * @param x X position for effect display
     * @param y Y position for effect display
     */
    public void processPowerUp(int powerUpType, float x, float y) {
        switch (powerUpType) {
            case PowerUp.TYPE_DOUBLE_POINTS:
                activateDoublePoints();
                break;
            case PowerUp.TYPE_EXTEND_TIME:
                extendGameTime(5f); // Add 5 seconds
                break;
            case PowerUp.TYPE_REDUCE_TIME:
                reduceGameTime(3f); // Subtract 3 seconds
                break;
            case PowerUp.TYPE_INVERT_CONTROLS:
                activateInvertControls();
                break;
            case PowerUp.TYPE_SLOW_PLAYER:
                activateSlowPlayer();
                break;
        }
        
        // Create visual effect
        createPowerUpEffect(powerUpType, x, y);
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
        for (Entity entity : gameScene.getEntityManager().getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.setInvertControls(false);
                player.setSpeed(originalPlayerSpeed);
            }
        }
        
        updatePowerUpLabel();
    }
}