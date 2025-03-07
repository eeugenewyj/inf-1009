package com.mygdx.game.AbstractScene;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSceneManager implements ISceneManager {
    private final Map<String, Scene> scenes = new HashMap<>();
    private Scene currentScene;

    public final void addScene(String name, Scene scene) {
        if (!scenes.containsKey(name)) {
            scenes.put(name, scene);
        }
    }

    @Override
    public final void setScene(String name) {
        if (scenes.containsKey(name)) {
            if (currentScene != null) {
                currentScene.hide();
            }
            currentScene = scenes.get(name);
            currentScene.show();
        } else {
            System.out.println("Scene not found: " + name);
        }
    }

    @Override
    public Scene getCurrentScene() {
        return currentScene;
    }

    public final void renderScene(float deltaTime) {
        if (currentScene != null) {
            currentScene.render(deltaTime);
        }
    }

    public abstract void initializeScenes();

    public abstract void dispose();
}

