package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.AbstractEntity.Entity;

/**
 * A simplified power-up manager that handles power-up effects and timers
 */
public class PowerUpManager {
    private boolean doublePointsActive = false;
    private float doublePointsTimer = 0;
    private static final float DOUBLE_POINTS_DURATION = 3f; // 3 seconds
    
    private boolean invertControlsActive = false;
    private float invertControlsTimer = 0;
    private static final float INVERT_CONTROLS_DURATION = 5f; // 5 seconds

    private boolean slowPlayerActive = false;
    private float slowPlayerTimer = 0;
    private static final float SLOW_PLAYER_DURATION = 4f; // 4 seconds
    private float originalPlayerSpeed = 200f; // Store original speed to restore later
    
    private Label powerUpLabel; // For displaying active power-ups
    private GameEntityManager entityManager;
    private GameScene gameScene; // Reference to main game scene

    /**
     * Creates a new power-up manager
     * @param powerUpLabel The label to display active power-ups
     * @param entityManager The entity manager for finding players
     * @param gameScene The main game scene
     */
    public PowerUpManager(Label powerUpLabel, GameEntityManager entityManager, GameScene gameScene) {
        this.powerUpLabel = powerUpLabel;
        this.entityManager = entityManager;
        this.gameScene = gameScene;
        updatePowerUpLabel();
    }

    /**
     * Updates all power-up timers
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {
        // Handle double points timer
        if (doublePointsActive) {
            doublePointsTimer += delta;
            updatePowerUpLabel();
            
            if (doublePointsTimer >= DOUBLE_POINTS_DURATION) {
                doublePointsActive = false;
                updatePowerUpLabel();
                System.out.println("Double Points expired!");
            }
        }
        
        // Handle invert controls timer
        if (invertControlsActive) {
            invertControlsTimer += delta;
            updatePowerUpLabel();
            
            if (invertControlsTimer >= INVERT_CONTROLS_DURATION) {
                invertControlsActive = false;
                // Reset invert flag on all players
                for (Entity entity : entityManager.getEntities()) {
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
            slowPlayerTimer += delta;
            updatePowerUpLabel();
            
            if (slowPlayerTimer >= SLOW_PLAYER_DURATION) {
                slowPlayerActive = false;
                // Reset speed on all players
                for (Entity entity : entityManager.getEntities()) {
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
        StringBuilder labelText = new StringBuilder();
        
        if (doublePointsActive) {
            labelText.append("DOUBLE POINTS! ");
            float remaining = DOUBLE_POINTS_DURATION - doublePointsTimer;
            labelText.append(String.format("(%.1fs)", remaining));
        }
        
        if (invertControlsActive) {
            if (labelText.length() > 0) {
                labelText.append(" | ");
            }
            labelText.append("INVERTED CONTROLS! ");
            float remaining = INVERT_CONTROLS_DURATION - invertControlsTimer;
            labelText.append(String.format("(%.1fs)", remaining));
        }
        
        if (slowPlayerActive) {
            if (labelText.length() > 0) {
                labelText.append(" | ");
            }
            labelText.append("SLOWED! ");
            float remaining = SLOW_PLAYER_DURATION - slowPlayerTimer;
            labelText.append(String.format("(%.1fs)", remaining));
        }
        
        powerUpLabel.setText(labelText.toString());
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
     * Activates inverted controls for a fixed duration
     */
    public void activateInvertControls() {
        invertControlsActive = true;
        invertControlsTimer = 0;
        
        // Set invert flag on all players
        for (Entity entity : entityManager.getEntities()) {
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
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                originalPlayerSpeed = player.getSpeed(); // Save original speed
                player.setSpeed(originalPlayerSpeed * 0.5f); // Reduce to 50%
            }
        }
        
        updatePowerUpLabel();
        System.out.println("Player slowed!");
    }

    /**
     * Process a power-up effect
     * @param powerUpType The type of power-up
     * @param effectX X position for visual effect
     * @param effectY Y position for visual effect
     */
    public void processPowerUp(int powerUpType, float effectX, float effectY) {
        switch (powerUpType) {
            case PowerUp.TYPE_DOUBLE_POINTS:
                activateDoublePoints();
                // Add visual effect for double points
                PowerUpEffect effect = PowerUpEffect.createDoublePointsEffect(effectX, effectY);
                entityManager.addEntity(effect);
                break;
            case PowerUp.TYPE_EXTEND_TIME:
                gameScene.extendGameTime(5f); // Add 5 seconds
                // Add visual effect for time extension
                PowerUpEffect timeEffect = PowerUpEffect.createTimeExtensionEffect(effectX, effectY);
                entityManager.addEntity(timeEffect);
                break;
            case PowerUp.TYPE_REDUCE_TIME:
                gameScene.reduceGameTime(3f); // Subtract 3 seconds
                // Add visual effect for time reduction
                PowerUpEffect reduceTimeEffect = PowerUpEffect.createEffect(effectX, effectY, "-3 SECONDS!", Color.RED, 2.0f);
                entityManager.addEntity(reduceTimeEffect);
                break;
            case PowerUp.TYPE_INVERT_CONTROLS:
                activateInvertControls();
                // Add visual effect for inverted controls
                PowerUpEffect invertEffect = PowerUpEffect.createEffect(effectX, effectY, "CONTROLS INVERTED!", Color.PURPLE, 2.0f);
                entityManager.addEntity(invertEffect);
                break;
            case PowerUp.TYPE_SLOW_PLAYER:
                activateSlowPlayer();
                // Add visual effect for slow player
                PowerUpEffect slowEffect = PowerUpEffect.createEffect(effectX, effectY, "SPEED REDUCED!", Color.ORANGE, 2.0f);
                entityManager.addEntity(slowEffect);
                break;
        }
    }

    /**
     * Checks if double points is active
     */
    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }

    /**
     * Resets all power-ups
     */
    public void reset() {
        doublePointsActive = false;
        doublePointsTimer = 0;
        invertControlsActive = false;
        invertControlsTimer = 0;
        slowPlayerActive = false;
        slowPlayerTimer = 0;
        updatePowerUpLabel();
        
        // Reset any player modifications
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.setInvertControls(false);
                player.setSpeed(originalPlayerSpeed);
            }
        }
    }
}