// core/src/main/java/com/mygdx/game/GameSceneManager.java
package com.mygdx.game;

import com.mygdx.game.AbstractIO.Audio;
import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.AbstractSceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class GameSceneManager extends AbstractSceneManager {
    private Audio backgroundMusic;
    private final IInputManager inputManager;
    private final IOutputManager outputManager;
    private final SceneFactory sceneFactory;

    public GameSceneManager(IInputManager inputManager, IOutputManager outputManager) {
        this.inputManager = inputManager;
        this.outputManager = outputManager;
        this.sceneFactory = new SceneFactory(this, inputManager, outputManager);
        backgroundMusic = Audio.getInstance("Music/MainScreenMusic.mp3", 0.5f, true);
    }

    /**
     * Factory method that creates scenes on demand by delegating to SceneFactory.
     * Each call returns a fresh instance of the requested scene.
     */
    @Override
    protected Scene createScene(String sceneName) {
        return sceneFactory.createScene(sceneName);
    }
    
    /**
     * Factory method that creates scenes by type by delegating to SceneFactory.
     * This provides type safety and is the preferred method.
     */
    @Override
    protected Scene createScene(SceneType sceneType) {
        return sceneFactory.createScene(sceneType);
    }

    /**
     * Initialize the game with the first scene (home/main menu)
     */
    @Override
    public void initialize() {
        // Start with the home scene using the enum for type safety
        setScene(SceneType.HOME);
    }

    @Override
    public void playBackgroundMusic() {
        backgroundMusic.playMusic();
    }

    @Override
    public float getBackgroundMusicVolume() {
        return backgroundMusic.getVolume();
    }

    @Override
    public void setBackgroundMusicVolume(float volume) {
        backgroundMusic.setVolume(volume);
        backgroundMusic.setSoundEffectVolume("player", volume);
        backgroundMusic.setSoundEffectVolume("tree", volume);
    }

    @Override
    public void stopBackgroundMusic() {
        backgroundMusic.stopMusic();
    }

    @Override
    public void dispose() {
        super.dispose(); // Dispose current scene
        backgroundMusic.dispose();
    }
}