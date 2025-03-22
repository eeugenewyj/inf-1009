package com.mygdx.game;

import com.mygdx.game.AbstractIO.Audio;
import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.AbstractSceneManager;

public class GameSceneManager extends AbstractSceneManager {
    private Audio backgroundMusic;
    private final IInputManager inputManager;
    private final IOutputManager outputManager;

    public GameSceneManager(IInputManager inputManager, IOutputManager outputManager) {
        this.inputManager = inputManager;
        this.outputManager = outputManager;
        backgroundMusic = Audio.getInstance("Music/MainScreenMusic.mp3", 0.5f, true);
    }

    public void initializeScenes() {
        addScene("home", SceneFactory.createScene("home", this, inputManager, outputManager));
        addScene("play", SceneFactory.createScene("play", this, inputManager, outputManager));
        addScene("stop", SceneFactory.createScene("stop", this, inputManager, outputManager));
        addScene("settings", SceneFactory.createScene("settings", this, inputManager, outputManager));
        addScene("highscores", SceneFactory.createScene("highscores", this, inputManager, outputManager));
        
        // Add the new difficulty selection scene
        addScene("difficulty", SceneFactory.createScene("difficulty", this, inputManager, outputManager));
        
        setScene("home");
    }

    public void playBackgroundMusic() {
        backgroundMusic.playMusic();
    }

    public float getBackgroundMusicVolume() {
        return backgroundMusic.getVolume();
    }

    public void setBackgroundMusicVolume(float volume) {
        backgroundMusic.setVolume(volume);
        backgroundMusic.setSoundEffectVolume("player", volume);
        backgroundMusic.setSoundEffectVolume("tree", volume);
    }

    public void stopBackgroundMusic() {
        backgroundMusic.stopMusic();
    }

    @Override
    public void dispose() {
        backgroundMusic.dispose();
    }
}