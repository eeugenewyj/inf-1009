// core/src/main/java/com/mygdx/game/SceneFactory.java
package com.mygdx.game;

import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.ISceneManager;
import com.mygdx.game.AbstractScene.Scene;

/**
 * A factory class responsible for creating Scene instances.
 * This class centralizes scene creation logic and separates it from the
 * SceneManager.
 */
public class SceneFactory {
    private final ISceneManager sceneManager;
    private final IInputManager inputManager;
    private final IOutputManager outputManager;

    /**
     * Creates a new SceneFactory
     * 
     * @param sceneManager  Manager that will handle the created scenes
     * @param inputManager  Input manager to pass to created scenes
     * @param outputManager Output manager to pass to created scenes
     */
    public SceneFactory(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        this.sceneManager = sceneManager;
        this.inputManager = inputManager;
        this.outputManager = outputManager;
    }

    /**
     * Creates a scene based on its name identifier.
     * 
     * @param sceneName The identifier of the scene to create
     * @return A new Scene instance, or null if the scene name is not recognized
     */
    public Scene createScene(String sceneName) {
        // Convert string ID to enum
        SceneType sceneType = SceneType.fromId(sceneName);

        if (sceneType == null) {
            System.out.println("Unknown scene: " + sceneName);
            return null;
        }

        return createScene(sceneType);
    }

    /**
     * Creates a scene based on its enum type.
     * This is the preferred method for creating scenes as it provides type safety.
     * 
     * @param sceneType The type of scene to create
     * @return A new Scene instance
     */
    public Scene createScene(SceneType sceneType) {
        switch (sceneType) {
            case HOME:
                return new MainMenuScene(sceneManager, inputManager, outputManager);

            case PLAY:
                return new GameScene(sceneManager, inputManager, outputManager);

            case STOP:
                return new StopScene(sceneManager, inputManager, outputManager);

            case SETTINGS:
                return new SettingsScene(sceneManager, inputManager, outputManager);

            case HIGHSCORES:
                return new HighScoresScene(sceneManager, inputManager, outputManager);

            case DIFFICULTY:
                return new DifficultySelectionScene(sceneManager, inputManager, outputManager);

            default:
                System.out.println("Unhandled scene type: " + sceneType);
                return null;
        }
    }
}