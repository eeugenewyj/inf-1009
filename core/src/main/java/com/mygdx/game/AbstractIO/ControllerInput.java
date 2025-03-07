package com.mygdx.game.AbstractIO;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.ControllerAdapter;

public class ControllerInput extends ControllerAdapter {

    private Controller activeController;
    private static final float DEAD_ZONE = 0.2f; // Stick drift fix

    // Default axis mapping
    private int leftStickXAxis = 0;
    private int leftStickYAxis = 1;

    private static boolean leftStickLeftPressed = false;
    private static boolean leftStickRightPressed = false;
    private static boolean leftStickUpPressed = false;
    private static boolean leftStickDownPressed = false;

    public ControllerInput() {
        // Check for available controllers
        if (Controllers.getControllers().size > 0) {
            activeController = Controllers.getControllers().first();
            activeController.addListener(this);
            System.out.println("Controller connected: " + activeController.getName());
        }
        // Listen for new controllers being connected/disconnected
        Controllers.addListener(this);
    }

    public float getLeftStickX() {

        if (activeController == null)
            return 0f;
        float value = activeController.getAxis(leftStickXAxis);

        if (Math.abs(value) > DEAD_ZONE) {
            if (value < 0 && !leftStickLeftPressed) {
                System.out.println("Controller: Moving Left");
                leftStickLeftPressed = true;
            } else if (value > 0 && !leftStickRightPressed) {
                System.out.println("Controller: Moving Right");
                leftStickRightPressed = true;
            }
        } else {
            // Reset movement flags when joystick is back to neutral
            leftStickLeftPressed = false;
            leftStickRightPressed = false;
        }

        return Math.abs(value) > DEAD_ZONE ? value : 0f;
    }

    public float getLeftStickY() {

        if (activeController == null)
            return 0f;
        // Invert Y axis
        float value = -activeController.getAxis(leftStickYAxis);

        if (Math.abs(value) > DEAD_ZONE) {
            if (value < 0 && !leftStickDownPressed) {
                System.out.println("Controller: Moving Down");
                leftStickDownPressed = true;
            } else if (value > 0 && !leftStickUpPressed) {
                System.out.println("Controller: Moving Up");
                leftStickUpPressed = true;
            }
        } else {
            leftStickUpPressed = false;
            leftStickDownPressed = false;
        }

        return Math.abs(value) > DEAD_ZONE ? value : 0f;
    }

    // Check if a button is pressed
    public boolean isButtonPressed(int button) {
        if (activeController == null)
            return false;
        return activeController.getButton(button);
    }

    @Override
    public void connected(Controller controller) {
        if (activeController == null) {
            activeController = controller;
            System.out.println("New Controller Connected: " + controller.getName());
        }
    }

    @Override
    public void disconnected(Controller controller) {
        if (activeController == controller) {
            System.out.println("Controller Disconnected: " + controller.getName());
            activeController = null; // Reset active controller
        }
    }

    public Controller getActiveController() {
        return activeController;
    }

    public boolean isControllerConnected() {
        return activeController != null;
    }

}
