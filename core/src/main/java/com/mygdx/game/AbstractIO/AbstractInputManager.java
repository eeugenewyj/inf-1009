package com.mygdx.game.AbstractIO;

public abstract class AbstractInputManager implements iInputManager {
    // Flag to indicate whether a gamepad is being used (shared across all input
    // managers)
    protected static boolean useGamepad = false;

    // Instance of ControllerInput to handle gamepad input
    protected static final ControllerInput controllerInput = new ControllerInput();

    // Instance of IKeyboardInput to handle keyboard input
    protected final iKeyboardInput keyboardInput;

    // Constructor to initialise the keyboard input manager
    public AbstractInputManager(iKeyboardInput keyboardInput) {
        this.keyboardInput = keyboardInput;
    }

    // Abstract method to check if a specific action is pressed
    @Override
    public abstract boolean isActionPressed(int actionCode);

    // Abstract method to set whether to use the gamepad
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
    public abstract float getMoveX();

    @Override
    public abstract float getMoveY();

    @Override
    public abstract void dispose();
}
