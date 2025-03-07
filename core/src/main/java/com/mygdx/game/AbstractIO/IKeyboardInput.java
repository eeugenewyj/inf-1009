package com.mygdx.game.AbstractIO;

public interface IKeyboardInput {
    float getHorizontal();
    float getVertical();
    boolean isKeyPressed(int key);
}
