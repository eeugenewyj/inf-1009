// core/src/main/java/com/mygdx/game/AbstractScene/AbstractSceneManager.java
package com.mygdx.game.AbstractScene;

import com.mygdx.game.SceneType;

public abstract class AbstractSceneManager implements ISceneManager {
    protected Scene currentScene;

    /**
     * Sets the active scene by name, disposing the previous scene.
     * Uses a factory pattern to create scenes on demand.
     * 
     * @param sceneName The name identifier of the scene to set
     */
    @Override
    public final void setScene(String sceneName) {
        // For backward compatibility, convert string to enum if possible
        SceneType sceneType = SceneType.fromId(sceneName);
        
        if (sceneType != null) {
            setScene(sceneType);
        } else {
            // Fall back to string-based scene creation
            // Dispose of current scene if one exists
            if (currentScene != null) {
                currentScene.hide();
                currentScene.dispose();
                currentScene = null;
            }
            
            // Create the new scene using the factory method
            Scene newScene = createScene(sceneName);
            
            if (newScene != null) {
                currentScene = newScene;
                currentScene.show();
                System.out.println("Scene changed to: " + sceneName);
            } else {
                System.out.println("Scene not found: " + sceneName);
            }
        }
    }
    
    /**
     * Sets the active scene by type, disposing the previous scene.
     * This is the preferred method for setting scenes as it provides type safety.
     * 
     * @param sceneType The type of scene to set
     */
    public final void setScene(SceneType sceneType) {
        // Dispose of current scene if one exists
        if (currentScene != null) {
            currentScene.hide();
            currentScene.dispose();
            currentScene = null;
        }
        
        // Create the new scene using the factory method
        Scene newScene = createScene(sceneType.getId());
        
        if (newScene != null) {
            currentScene = newScene;
            currentScene.show();
            System.out.println("Scene changed to: " + sceneType.name());
        } else {
            System.out.println("Failed to create scene: " + sceneType.name());
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

    /**
     * Factory method to create a scene by name.
     * Must be implemented by concrete scene managers.
     * 
     * @param sceneName The name identifier of the scene to create
     * @return A new Scene instance, or null if the name is not recognized
     */
    protected abstract Scene createScene(String sceneName);
    
    /**
     * Factory method to create a scene by type.
     * By default, delegates to string-based factory method.
     * Can be overridden for additional type-specific logic.
     * 
     * @param sceneType The type of scene to create
     * @return A new Scene instance, or null if the type is not recognized
     */
    protected Scene createScene(SceneType sceneType) {
        return createScene(sceneType.getId());
    }

    /**
     * Initialize the initial scene (typically the main menu)
     * This replaces the previous initializeScenes method
     */
    public abstract void initialize();

    @Override
    public void dispose() {
        if (currentScene != null) {
            currentScene.dispose();
            currentScene = null;
        }
    }
}