package com.mygdx.game.AbstractIO;

public abstract class AbstractInputManager implements IInputManager {
    protected static boolean useGamepad = false;  // Shared across all input managers
    protected static final ControllerInput controllerInput = new ControllerInput();  // Shared controller input
    protected final IKeyboardInput keyboardInput;

    public AbstractInputManager(IKeyboardInput keyboardInput) {
        this.keyboardInput = keyboardInput;
    }

    @Override
    public abstract float getMoveX();
    @Override
    public abstract float getMoveY();
    @Override
    public abstract boolean isActionPressed(int actionCode);

    public abstract void setUseGamepad(boolean useGamepad);

    // Universal method to detect input type (Keyboard vs Gamepad)
    protected void detectInputType() {
        if (controllerInput.isControllerConnected()) {
            useGamepad = true;
        } else {
            useGamepad = false;
        }
    }

    // Helper method for subclasses to check if gamepad is active
    protected boolean isUsingGamepad() {
        return useGamepad;
    }

    @Override
    public abstract void dispose();
}


