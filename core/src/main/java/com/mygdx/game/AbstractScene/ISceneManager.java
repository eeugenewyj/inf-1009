// core/src/main/java/com/mygdx/game/AbstractScene/ISceneManager.java
package com.mygdx.game.AbstractScene;

import com.mygdx.game.SceneType;

public interface ISceneManager {
    void setScene(String sceneName);
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