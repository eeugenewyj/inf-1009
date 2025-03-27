package com.mygdx.game.AbstractCollision;

public interface iCollisionManager {
    // Abstract method to detect collisions between entities
    void detectCollisions();

    void dispose();
}