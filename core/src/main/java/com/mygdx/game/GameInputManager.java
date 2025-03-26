package com.mygdx.game;

import com.mygdx.game.AbstractIO.AbstractInputManager;
import com.mygdx.game.AbstractIO.IKeyboardInput;

public class GameInputManager extends AbstractInputManager {

    public GameInputManager(IKeyboardInput keyboardInput) {
        super(keyboardInput);
    }

    public void detectInputType() {
        if (controllerInput.isControllerConnected()) {
            useGamepad = true; // Use controller if detected
        } else {
            useGamepad = false; // Default to keyboard if no controller
        }
    }

    @Override
    public float getMoveX() {
        detectInputType(); // Ensure input type is checked
        return useGamepad ? controllerInput.getLeftStickX() : keyboardInput.getHorizontal();
    }

    @Override
    public float getMoveY() {
        detectInputType(); // Ensure input type is checked
        return useGamepad ? controllerInput.getLeftStickY() : keyboardInput.getVertical();
    }

    @Override
    public boolean isActionPressed(int actionCode) {
        return keyboardInput.isKeyPressed(actionCode);
    }

    public void setUseGamepad(boolean useGamepad) {
        this.useGamepad = useGamepad;
    }

    @Override
    public void dispose() {
    }
}