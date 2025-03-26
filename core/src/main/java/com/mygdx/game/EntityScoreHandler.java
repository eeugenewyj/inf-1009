package com.mygdx.game;

/**
 * Interface for handling score and power-up related functionality.
 * This breaks the circular dependency between GameScene and entity/collision managers.
 */
public interface EntityScoreHandler {
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
    void processPowerUp(int powerUpType, float x, float y);
}