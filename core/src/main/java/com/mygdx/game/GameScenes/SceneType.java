package com.mygdx.game.GameScenes;

/**
 * Enum representing the different types of scenes in the game.
 * This provides type safety and autocompletion when switching scenes.
 */
public enum SceneType {
    HOME("home"),
    PLAY("play"),
    STOP("stop"),
    SETTINGS("settings"),
    HIGHSCORES("highscores"),
    DIFFICULTY("difficulty");

    private final String id;

    SceneType(String id) {
        this.id = id;
    }

    /**
     * Gets the string identifier of the scene type.
     * Used for backward compatibility with existing code.
     */
    public String getId() {
        return id;
    }

    /**
     * Finds a SceneType by its string ID.
     * 
     * @param id The string identifier to look up
     * @return The matching SceneType, or null if not found
     */
    public static SceneType fromId(String id) {
        for (SceneType type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }
}