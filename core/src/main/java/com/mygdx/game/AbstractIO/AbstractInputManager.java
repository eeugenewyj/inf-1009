package com.mygdx.game.AbstractIO;

public abstract class AbstractInputManager implements IInputManager {
    protected static boolean useGamepad = false; // Flag to indicate whether a gamepad is being used (shared across all
                                                 // input managers)
    protected static final ControllerInput controllerInput = new ControllerInput(); // Instance of ControllerInput to
                                                                                    // handle gamepad input
    protected final IKeyboardInput keyboardInput; // Instance of IKeyboardInput to handle keyboard input

    // Constructor to initialise the keyboard input manager
    public AbstractInputManager(IKeyboardInput keyboardInput) {
        this.keyboardInput = keyboardInput;
    }

    @Override
    public abstract float getMoveX();

    @Override
    public abstract float getMoveY();

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
    public abstract void dispose();
}
