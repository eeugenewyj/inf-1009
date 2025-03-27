package com.mygdx.game.AbstractIO;

public interface iKeyboardInput {
    // Abstract method to check if a specific key is currently pressed
    boolean isKeyPressed(int key);

    float getHorizontal();

    float getVertical();
}
