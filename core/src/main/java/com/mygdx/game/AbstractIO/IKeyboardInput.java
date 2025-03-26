package com.mygdx.game.AbstractIO;

public interface IKeyboardInput {
    // Abstract method to check if a specific key is currently pressed
    boolean isKeyPressed(int key);

    float getHorizontal();

    float getVertical();
}
