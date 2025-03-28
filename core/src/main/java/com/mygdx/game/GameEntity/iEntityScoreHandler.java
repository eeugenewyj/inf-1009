package com.mygdx.game.GameEntity;

import com.mygdx.game.GamePowerups.PowerUpType;

/**
 * Interface for handling score and power-up related functionality.
 * This breaks the circular dependency between GameScene and entity/collision managers.
 */
public interface iEntityScoreHandler {
    /**
     * Adds points to the player's score
     * 
     * @param points The points to add
     */
    void addScore(int points);
    
    /**
     * Processes a power-up effect
     * 
     * @param powerUpType The type of power-up
     * @param x           X position for effect
     * @param y           Y position for effect
     */
    void processPowerUp(PowerUpType powerUpType, float x, float y);
    
    /**
     * Processes a power-up effect (for backward compatibility)
     * 
     * @param powerUpTypeId The integer ID of the power-up type
     * @param x           X position for effect
     * @param y           Y position for effect
     */
    void processPowerUp(int powerUpTypeId, float x, float y);
}