package com.mygdx.game;

/**
 * Interface for collision checking callbacks.
 * This breaks the circular dependency between Player and EntityManager.
 */
public interface CollisionCallback {
    /**
     * Checks if a move to the specified position would collide with spikes
     * 
     * @param x The x position to check
     * @param y The y position to check
     * @param width The width of the entity
     * @param height The height of the entity
     * @return true if the position would collide with spikes
     */
    boolean wouldCollideWithSpikes(float x, float y, float width, float height);
}