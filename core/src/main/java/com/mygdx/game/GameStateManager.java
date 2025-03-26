package com.mygdx.game;

import com.mygdx.game.AbstractIO.Audio;

/**
 * Manages the game state, including score, timer, and game lifecycle.
 * Decoupled from GameScene through the GameStateListener interface.
 */
public class GameStateManager {
    // Game state constants
    private static final float GAME_DURATION = 20f; // Default game duration in seconds

    // Game state variables
    private boolean gameActive = true;
    private int playerScore = 0;
    private float gameTimer = 0;
    private boolean scoreSaved = false;

    // Reference to audio system
    private Audio audio;
    
    // Reference to game state listener (instead of direct GameScene reference)
    private GameStateListener gameStateListener;

    /**
     * Creates a new GameStateManager
     * 
     * @param gameStateListener The listener that will respond to state changes
     * @param audio Audio system for sound effects
     */
    public GameStateManager(GameStateListener gameStateListener, Audio audio) {
        this.gameStateListener = gameStateListener;
        this.audio = audio;
    }

    /**
     * Updates the game state
     * 
     * @param deltaTime Time elapsed since last update
     * @return True if the game is still active, false if game over
     */
    public boolean update(float deltaTime) {
        if (!gameActive) {
            return false;
        }

        // Update game timer
        gameTimer += deltaTime;
        float timeRemaining = GAME_DURATION - gameTimer;

        // Notify listener about timer update
        gameStateListener.onTimerUpdated(timeRemaining);

        // Check for game over
        if (timeRemaining <= 0) {
            endGame();
            return false;
        }

        return true;
    }

    /**
     * Adds points to the player's score
     * 
     * @param points Base points to add
     * @param isDoublePoints Whether double points are active
     */
    public void addScore(int points, boolean isDoublePoints) {
        // Apply double points if active
        int actualPoints = isDoublePoints ? points * 2 : points;
        playerScore += actualPoints;

        // Notify listener about score change
        gameStateListener.onScoreChanged(playerScore);

        // Log double points bonus if applicable
        if (isDoublePoints) {
            System.out.println("Double points bonus! Added " + actualPoints + " points!");
        }
    }

    /**
     * Extends the game time
     * 
     * @param seconds Seconds to add to remaining time
     */
    public void extendGameTime(float seconds) {
        gameTimer = Math.max(0, gameTimer - seconds);
        System.out.println("Game time extended by " + seconds + " seconds!");
        
        // Notify listener about timer update
        gameStateListener.onTimerUpdated(GAME_DURATION - gameTimer);
    }

    /**
     * Reduces the game time
     * 
     * @param seconds Seconds to subtract from remaining time
     */
    public void reduceGameTime(float seconds) {
        gameTimer = Math.min(GAME_DURATION - 1, gameTimer + seconds);
        System.out.println("Game time reduced by " + seconds + " seconds!");
        
        // Notify listener about timer update
        gameStateListener.onTimerUpdated(GAME_DURATION - gameTimer);
    }

    public void pauseGame() {
        audio.pauseMusic();
    }

    public void resumeGame() {
        audio.resumeMusic();
    }

    public void endGame() {
        gameActive = false;

        // Only save score once
        if (!scoreSaved) {
            HighScoresManager highScoresManager = HighScoresManager.getInstance();
            boolean isNewBestScore = highScoresManager.addScore(playerScore);

            // Log game results
            System.out.println("Game ended with score: " + playerScore);
            System.out.println("Game difficulty: " + GameSettings.getDifficultyName());
            System.out.println("Is new best score: " + isNewBestScore);

            // Notify listener about game over
            gameStateListener.onGameOver(playerScore, isNewBestScore);

            scoreSaved = true;
        }
    }

    public void restartGame() {
        // Reset state
        gameActive = true;
        gameTimer = 0;
        playerScore = 0;
        scoreSaved = false;

        // Notify listener about state reset
        gameStateListener.onScoreChanged(0);
        gameStateListener.onTimerUpdated(GAME_DURATION);

        System.out.println("Game state reset for restart!");
    }

    /**
     * Gets the current player score
     * 
     * @return The player's score
     */
    public int getPlayerScore() {
        return playerScore;
    }

    /**
     * Gets the current game timer value
     * 
     * @return Current game timer value in seconds
     */
    public float getGameTimer() {
        return gameTimer;
    }

    /**
     * Gets the remaining game time
     * 
     * @return Seconds remaining in the game
     */
    public float getRemainingTime() {
        return Math.max(0, GAME_DURATION - gameTimer);
    }

    /**
     * Checks if the game is active
     * 
     * @return True if game is active, false if game over
     */
    public boolean isGameActive() {
        return gameActive;
    }

    /**
     * Sets whether the game is active
     * 
     * @param active Whether the game should be active
     */
    public void setGameActive(boolean active) {
        this.gameActive = active;
    }

    // Sets the player's score directly (for state restoration)
    public void setPlayerScore(int score) {
        this.playerScore = score;
        // Notify listener about score change
        gameStateListener.onScoreChanged(score);
    }

    // Sets the game timer directly (for state restoration)
    public void setGameTimer(float timer) {
        this.gameTimer = timer;
        // Notify listener about timer update
        gameStateListener.onTimerUpdated(GAME_DURATION - timer);
    }
}