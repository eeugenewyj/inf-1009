package com.mygdx.game.AbstractEntity;

public interface iMovable {
    // Abstract method to move the entity in an AI-controlled manner
    void moveAIControlled();

    // Abstract method to move the entity in a user-controlled manner, using delta
    // time for smooth movement
    void moveUserControlled(float deltaTime);
}