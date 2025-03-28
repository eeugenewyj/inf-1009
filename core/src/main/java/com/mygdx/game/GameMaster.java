package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.mygdx.game.AbstractIO.iInputManager;
import com.mygdx.game.AbstractIO.iKeyboardInput;
import com.mygdx.game.AbstractIO.iOutputManager;
import com.mygdx.game.AbstractIO.Keyboard;
import com.mygdx.game.AbstractScene.iSceneManager;
import com.mygdx.game.GameEntity.PowerUp;
import com.mygdx.game.GameIO.GameInputManager;
import com.mygdx.game.GameIO.GameOutputManager;
import com.mygdx.game.GameScenes.GameSceneManager;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class GameMaster extends ApplicationAdapter {
    private iInputManager inputManager;
    private iOutputManager outputManager;
    private iSceneManager sceneManager;

    @Override
    public void create() {
        iKeyboardInput keyboardInput = Keyboard.getInstance(); // Inject Keyboard
        // Initialise managers
        inputManager = new GameInputManager(keyboardInput);
        outputManager = new GameOutputManager();
        sceneManager = new GameSceneManager(inputManager, outputManager);

        // Initialize the first scene instead of pre-loading all scenes
        sceneManager.initialize();
    }

    @Override
    public void render() {
        if (sceneManager != null) {
            sceneManager.renderScene(Gdx.graphics.getDeltaTime());
        }
    }

    // Add getter for SceneManager to allow access from other classes
    public iSceneManager getSceneManager() {
        return sceneManager;
    }

    @Override
    public void dispose() {
        // Clean up static resources first
        PowerUp.disposeSharedResources();

        // Then dispose regular resources
        if (inputManager != null)
            inputManager.dispose();
        if (outputManager != null)
            outputManager.dispose();
        if (sceneManager != null)
            sceneManager.dispose();
    }
}