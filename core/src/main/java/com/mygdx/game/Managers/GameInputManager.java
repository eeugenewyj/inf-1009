package com.mygdx.game.Managers;

import com.mygdx.game.AbstractIO.AbstractInputManager;
import com.mygdx.game.AbstractIO.iKeyboardInput;

public class GameInputManager extends AbstractInputManager {

    public GameInputManager(iKeyboardInput keyboardInput) {
        super(keyboardInput);
    }

    @Override
    public void detectInputType() {
        if (getControllerInput().isControllerConnected()) {
            setUseGamepadStatic(true); // Use controller if detected
        } else {
            setUseGamepadStatic(false); // Default to keyboard if no controller
        }
    }

    @Override
    public float getMoveX() {
        detectInputType(); // Ensure input type is checked
        return isUsingGamepad() ? getControllerInput().getLeftStickX() : getKeyboardInput().getHorizontal();
    }

    @Override
    public float getMoveY() {
        detectInputType(); // Ensure input type is checked
        return isUsingGamepad() ? getControllerInput().getLeftStickY() : getKeyboardInput().getVertical();
    }

    @Override
    public boolean isActionPressed(int actionCode) {
        return getKeyboardInput().isKeyPressed(actionCode);
    }

    @Override
    public void setUseGamepad(boolean useGamepad) {
        setUseGamepadStatic(useGamepad);
    }

    @Override
    public void dispose() {
        // Nothing to dispose
    }
}