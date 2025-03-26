package com.mygdx.game.AbstractIO;

public interface IKeyboardInput {
    float getHorizontal();

    float getVertical();

    // Abstract method to check if a specific key is currently pressed
    boolean isKeyPressed(int key);
}
