package com.mygdx.game.AbstractIO;

public interface IInputManager {
    float getMoveX();
    float getMoveY();
    boolean isActionPressed(int actionCode);
    void setUseGamepad(boolean b);
    void dispose();
}
