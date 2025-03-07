package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IKeyboardInput;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractIO.Keyboard;
import com.mygdx.game.AbstractScene.ISceneManager;



/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMaster extends ApplicationAdapter {
    private IInputManager inputManager;
    private IOutputManager outputManager;
    private ISceneManager sceneManager;

    @Override
    public void create() {
        IKeyboardInput keyboardInput = Keyboard.getInstance();  // Inject Keyboard
        inputManager = new GameInputManager(keyboardInput);
        outputManager = new GameOutputManager();
        sceneManager = new GameSceneManager(inputManager, outputManager);
        sceneManager.initializeScenes();
    }

    @Override
    public void render() {
        if (sceneManager != null) {
            sceneManager.renderScene(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void dispose() {
        if (inputManager != null) inputManager.dispose();
        if (outputManager != null) outputManager.dispose();
        if (sceneManager != null) sceneManager.dispose();
    }
}
