package com.mygdx.game.AbstractIO;

public interface iInputManager {
    // Method to check if specific action is pressed, identified by an action code
    boolean isActionPressed(int actionCode);

    // Method to set whether to use a gamepad for input
    void setUseGamepad(boolean b);

    float getMoveX();

    float getMoveY();

    void dispose();
}
