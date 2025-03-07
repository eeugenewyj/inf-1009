package com.mygdx.game.AbstractScene;

public interface ISceneManager {
    void setScene(String sceneName);
    Scene getCurrentScene();
    void playBackgroundMusic();
    void stopBackgroundMusic();
    void setBackgroundMusicVolume(float volume);
    float getBackgroundMusicVolume();
    void initializeScenes();
    void renderScene(float deltaTime);
    void dispose();
}

