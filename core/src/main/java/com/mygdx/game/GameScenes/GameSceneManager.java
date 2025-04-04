package com.mygdx.game.GameScenes;

import com.mygdx.game.AbstractIO.Audio;
import com.mygdx.game.AbstractIO.iInputManager;
import com.mygdx.game.AbstractIO.iOutputManager;
import com.mygdx.game.AbstractScene.AbstractSceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class GameSceneManager extends AbstractSceneManager {
    private Audio backgroundMusic;
    private final iInputManager inputManager;
    private final iOutputManager outputManager;
    private final SceneFactory sceneFactory;
    
    // Keep track of the StopScene instance
    private StopScene stopScene;

    public GameSceneManager(iInputManager inputManager, iOutputManager outputManager) {
        this.inputManager = inputManager;
        this.outputManager = outputManager;
        this.sceneFactory = new SceneFactory(this, inputManager, outputManager);
        backgroundMusic = Audio.getInstance("Music/MainScreenMusic.mp3", 0.5f, true);
    }

    /**
     * Factory method that creates scenes on demand by delegating to SceneFactory.
     * Each call returns a fresh instance of the requested scene.
     * Now keeps a reference to the StopScene instance.
     */
    @Override
    protected Scene createScene(String sceneName) {
        Scene scene = sceneFactory.createScene(sceneName);
        
        // Store reference to StopScene if that's what we created
        if (scene instanceof StopScene) {
            stopScene = (StopScene) scene;
        }
        
        return scene;
    }

    /**
     * Factory method that creates scenes by type by delegating to SceneFactory.
     * This provides type safety and is the preferred method.
     */
    @Override
    protected Scene createScene(SceneType sceneType) {
        Scene scene = sceneFactory.createScene(sceneType);
        
        // Store reference to StopScene if that's what we created
        if (scene instanceof StopScene) {
            stopScene = (StopScene) scene;
        }
        
        return scene;
    }

    /**
     * Getter for the StopScene instance
     * @return The StopScene instance, or null if not yet created
     */
    public StopScene getStopScene() {
        return stopScene;
    }

    // Rest of the class remains unchanged
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