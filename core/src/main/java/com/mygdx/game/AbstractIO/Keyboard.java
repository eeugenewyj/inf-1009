package com.mygdx.game.AbstractIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Keyboard implements IKeyboardInput {
    private static Keyboard instance; // Singleton instance of the Keyboard class

    // Private constructor to enforce the singleton pattern
    private Keyboard() {
    }

    // Method to get the singleton instance of the Keyboard class
    public static Keyboard getInstance() {
        if (instance == null) {
            instance = new Keyboard(); // Create a new instance if it doesn't exist
        }
        return instance;
    }

    @Override
    public float getHorizontal() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            return -1;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            return 1;
        return 0;
    }

    @Override
    public float getVertical() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            return 1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            return -1;
        return 0;
    }

    @Override
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }
}
