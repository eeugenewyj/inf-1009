package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Manages game time-related functionality
 */
public class TimerManager {
    private float gameTimer = 0;
    private static final float GAME_DURATION = 20f; // 20 seconds
    private Label timerLabel;
    private boolean gameActive = true;

    public TimerManager(Label timerLabel) {
        this.timerLabel = timerLabel;
        updateTimerDisplay();
    }

    /**
     * Updates the game timer
     * @param delta Time elapsed since last frame
     * @return true if time is up, false otherwise
     */
    public boolean update(float delta) {
        if (!gameActive) return false;
        
        gameTimer += delta;
        float timeRemaining = GAME_DURATION - gameTimer;
        
        if (timeRemaining <= 0) {
            return true; // Time's up
        } else {
            updateTimerDisplay();
            return false;
        }
    }

    /**
     * Updates the timer display with current remaining time
     */
    private void updateTimerDisplay() {
        float timeRemaining = GAME_DURATION - gameTimer;
        timerLabel.setText(String.format("Time: %.1f", timeRemaining));
    }

    /**
     * Extends the game time by the specified amount
     * @param seconds The number of seconds to add
     */
    public void extendGameTime(float seconds) {
        gameTimer = Math.max(0, gameTimer - seconds); // Subtract from timer to extend time
        updateTimerDisplay();
        System.out.println("Game time extended by " + seconds + " seconds!");
    }
    
    /**
     * Reduces the game time by the specified amount
     * @param seconds The number of seconds to subtract
     */
    public void reduceGameTime(float seconds) {
        gameTimer = Math.min(GAME_DURATION - 1, gameTimer + seconds); // Add to timer to reduce time, ensure at least 1 second remains
        updateTimerDisplay();
        System.out.println("Game time reduced by " + seconds + " seconds!");
    }

    /**
     * Resets the timer
     */
    public void reset() {
        gameTimer = 0;
        gameActive = true;
        updateTimerDisplay();
    }

    /**
     * Sets whether the timer is active
     */
    public void setActive(boolean active) {
        this.gameActive = active;
    }

    /**
     * Gets the current game timer value
     */
    public float getGameTimer() {
        return gameTimer;
    }
}