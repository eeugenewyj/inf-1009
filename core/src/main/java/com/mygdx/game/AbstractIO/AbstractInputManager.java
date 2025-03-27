package com.mygdx.game.AbstractIO;

public abstract class AbstractInputManager implements iInputManager {
    // Changed from protected to private for better encapsulation
    private static boolean useGamepad = false;

    // Instance of ControllerInput to handle gamepad input - now private
    private static final ControllerInput controllerInput = new ControllerInput();

    // Instance of IKeyboardInput to handle keyboard input - now private
    private final iKeyboardInput keyboardInput;

    // Constructor to initialise the keyboard input manager
    public AbstractInputManager(iKeyboardInput keyboardInput) {
        this.keyboardInput = keyboardInput;
    }

    // Abstract method to check if a specific action is pressed
    @Override
    public abstract boolean isActionPressed(int actionCode);

    // Abstract method to set whether to use the gamepad
    public abstract void setUseGamepad(boolean useGamepad);

    // Universal method to detect input type (Keyboard vs Gamepad) - now protected
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
    
    // Protected getter for keyboard input
    protected iKeyboardInput getKeyboardInput() {
        return keyboardInput;
    }
    
    // Protected getter for controller input
    protected ControllerInput getControllerInput() {
        return controllerInput;
    }
    
    // Static setter for useGamepad (since field is static)
    protected static void setUseGamepadStatic(boolean value) {
        useGamepad = value;
    }

    @Override
    public abstract float getMoveX();

    @Override
    public abstract float getMoveY();

    @Override
    public abstract void dispose();
}