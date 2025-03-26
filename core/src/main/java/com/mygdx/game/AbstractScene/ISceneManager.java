package com.mygdx.game.AbstractScene;

import com.mygdx.game.SceneType;

public interface ISceneManager {
    // Abstract method to set the current scene by name
    void setScene(String sceneName);

    // Abstract method to set the current scene by type
    void setScene(SceneType sceneType);

    Scene getCurrentScene();

    void playBackgroundMusic();

    void stopBackgroundMusic();

    void setBackgroundMusicVolume(float volume);

    float getBackgroundMusicVolume();

    void initialize();

    void renderScene(float deltaTime);

    void dispose();
}