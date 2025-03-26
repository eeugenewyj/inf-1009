package com.mygdx.game.AbstractIO;

public interface IInputManager {
    float getMoveX();

    float getMoveY();

    // Method to check if specific action is pressed, identified by an action code
    boolean isActionPressed(int actionCode);

    // Method to set whether to use a gamepad for input
    void setUseGamepad(boolean b);

    void dispose();
}
