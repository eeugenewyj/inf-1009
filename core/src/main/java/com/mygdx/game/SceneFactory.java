package com.mygdx.game;
import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.ISceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class SceneFactory {
    public static Scene createScene(String sceneName, ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        switch (sceneName.toLowerCase()) {
            case "home":
                return new MainMenuScene(sceneManager, inputManager, outputManager);
            case "play":
                return new GameScene(sceneManager, inputManager, outputManager);
            case "stop":
                return new StopScene(sceneManager, inputManager, outputManager);
            case "settings":
                return new SettingsScene(sceneManager, inputManager, outputManager);
            case "highscores":
                return new HighScoresScene(sceneManager, inputManager, outputManager);
            default:
                throw new IllegalArgumentException("Unknown scene: " + sceneName);
        }
    }
}