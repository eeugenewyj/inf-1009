package com.mygdx.game.AbstractCollision;

public interface ICollisionManager {
    // Abstract method to detect collisions between entities
    void detectCollisions();

    // Abstract method to dispose of resources
    void dispose();
}