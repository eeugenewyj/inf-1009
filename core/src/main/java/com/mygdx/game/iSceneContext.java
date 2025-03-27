package com.mygdx.game;

/**
 * Interface providing contextual information from the scene to managers.
 * This breaks circular dependencies by providing only the necessary methods.
 */
public interface iSceneContext {
    /**
     * Gets the entity manager
     * 
     * @return The entity manager
     */
    GameEntityManager getEntityManager();
    
    /**
     * Creates a power-up effect at the specified location
     * 
     * @param powerUpType The type of power-up
     * @param x X position for effect display
     * @param y Y position for effect display
     */
    void createPowerUpEffect(PowerUpType powerUpType, float x, float y);
    
    /**
     * Creates a power-up effect (for backward compatibility)
     * @param powerUpTypeId The integer ID of the power-up type
     * @param x X position for effect display
     * @param y Y position for effect display
     */
    void createPowerUpEffect(int powerUpTypeId, float x, float y);
    
    /**
     * Updates the power-up status display
     * 
     * @param text Text to display
     */
    void updatePowerUpLabel(String text);
    
    /**
     * Gets the input manager
     * 
     * @return The input manager
     */
    com.mygdx.game.AbstractIO.iInputManager getInputManager();
}