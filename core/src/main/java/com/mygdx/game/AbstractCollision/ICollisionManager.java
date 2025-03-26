package com.mygdx.game.AbstractCollision;

public interface ICollisionManager {
    // Abstract method to detect collisions between entities
    void detectCollisions();

    void dispose();
}