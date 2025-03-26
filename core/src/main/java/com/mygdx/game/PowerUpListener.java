package com.mygdx.game;

/**
 * Interface for objects that can respond to power-up events.
 * This breaks the circular dependency between PowerUpManager and GameScene.
 */
public interface PowerUpListener {
    /**
     * Updates the power-up label to show active effects
     * 
     * @param text The text to display
     */
    void updatePowerUpLabel(String text);
    
    /**
     * Gets the entity manager
     * 
     * @return The entity manager
     */
    GameEntityManager getEntityManager();
    
    /**
     * Creates a visual power-up effect
     * 
     * @param powerUpType The type of power-up
     * @param x X position for effect display
     * @param y Y position for effect display
     */
    void createPowerUpEffect(int powerUpType, float x, float y);
    
    /**
     * Extends the game time by the specified amount
     * 
     * @param seconds The number of seconds to add
     */
    void extendGameTime(float seconds);
    
    /**
     * Reduces the game time by the specified amount
     * 
     * @param seconds The number of seconds to subtract
     */
    void reduceGameTime(float seconds);
}