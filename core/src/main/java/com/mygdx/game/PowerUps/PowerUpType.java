package com.mygdx.game.PowerUps;

import com.badlogic.gdx.graphics.Color;

/**
 * Enum representing different types of power-ups in the game.
 * This replaces the hardcoded constants in the PowerUp class for better extensibility.
 */
public enum PowerUpType {
    // Buffs
    DOUBLE_POINTS("2X", Color.GOLD, false, 3f),
    EXTEND_TIME("+5", Color.CYAN, false, 0f), // 0 duration means instant effect
    
    // Debuffs
    REDUCE_TIME("-3", Color.RED, true, 0f), // 0 duration means instant effect
    INVERT_CONTROLS("INV", Color.PURPLE, true, 5f),
    SLOW_PLAYER("SLOW", Color.ORANGE, true, 4f);
    
    private final String symbol;
    private final Color color;
    private final boolean isDebuff;
    private final float duration; // Duration in seconds, 0 for instant effects
    
    /**
     * Constructor for PowerUpType
     * 
     * @param symbol The text symbol displayed on the power-up
     * @param color The color of the power-up
     * @param isDebuff Whether this is a negative effect
     * @param duration How long the effect lasts (0 for instant effects)
     */
    PowerUpType(String symbol, Color color, boolean isDebuff, float duration) {
        this.symbol = symbol;
        this.color = color;
        this.isDebuff = isDebuff;
        this.duration = duration;
    }
    
    /**
     * Gets the symbol to display on the power-up
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * Gets the color of the power-up
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Checks if this is a negative effect
     */
    public boolean isDebuff() {
        return isDebuff;
    }
    
    /**
     * Gets the duration of the effect in seconds
     * @return Duration in seconds, 0 for instant effects
     */
    public float getDuration() {
        return duration;
    }
}