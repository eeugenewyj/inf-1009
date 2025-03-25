package com.mygdx.game;

import com.mygdx.game.AbstractIO.Audio;

/**
 * Manages the game state, including score, timer, and game lifecycle
 */
public class GameStateManager {
    // Game state constants
    private static final float GAME_DURATION = 20f; // Default game duration in seconds
    
    // Game state variables
    private boolean gameActive = true;
    private int playerScore = 0;
    private float gameTimer = 0;
    private boolean scoreSaved = false;
    
    // Reference to GameScene for callbacks
    private GameScene gameScene;
    private Audio audio;
    
    /**
     * Creates a new GameStateManager
     * @param gameScene The game scene this manager will work with
     * @param audio Audio system for sound effects
     */
    public GameStateManager(GameScene gameScene, Audio audio) {
        this.gameScene = gameScene;
        this.audio = audio;
    }
    
    /**
     * Updates the game state
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
        
        // Update timer display through GameScene
        gameScene.updateTimerLabel(String.format("Time: %.1f", timeRemaining));
        
        // Check for game over
        if (timeRemaining <= 0) {
            endGame();
            return false;
        }
        
        return true;
    }
    
    /**
     * Adds points to the player's score
     * @param points Base points to add
     * @param isDoublePoints Whether double points are active
     */
    public void addScore(int points, boolean isDoublePoints) {
        // Apply double points if active
        int actualPoints = isDoublePoints ? points * 2 : points;
        playerScore += actualPoints;
        
        // Update score display through GameScene
        gameScene.updateScoreLabel("Score: " + playerScore);
        
        // Log double points bonus if applicable
        if (isDoublePoints) {
            System.out.println("Double points bonus! Added " + actualPoints + " points!");
        }
    }
    
    /**
     * Extends the game time
     * @param seconds Seconds to add to remaining time
     */
    public void extendGameTime(float seconds) {
        gameTimer = Math.max(0, gameTimer - seconds);
        System.out.println("Game time extended by " + seconds + " seconds!");
    }
    
    /**
     * Reduces the game time
     * @param seconds Seconds to subtract from remaining time
     */
    public void reduceGameTime(float seconds) {
        gameTimer = Math.min(GAME_DURATION - 1, gameTimer + seconds);
        System.out.println("Game time reduced by " + seconds + " seconds!");
    }
    
    /**
     * Pauses the game
     */
    public void pauseGame() {
        audio.pauseMusic();
    }
    
    /**
     * Ends the game
     */
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
            
            // Notify GameScene to show game over UI
            gameScene.showGameOver(playerScore, isNewBestScore);
            
            scoreSaved = true;
        }
    }
    
    /**
     * Restarts the game
     */
    public void restartGame() {
        // Reset state
        gameActive = true;
        gameTimer = 0;
        playerScore = 0;
        scoreSaved = false;
        
        // Reset UI through GameScene
        gameScene.updateScoreLabel("Score: 0");
        gameScene.updateTimerLabel("Time: " + GAME_DURATION);
        
        System.out.println("Game state reset for restart!");
    }
    
    /**
     * Gets the current player score
     * @return The player's score
     */
    public int getPlayerScore() {
        return playerScore;
    }
    
    /**
     * Gets the current game timer value
     * @return Current game timer value in seconds
     */
    public float getGameTimer() {
        return gameTimer;
    }
    
    /**
     * Gets the remaining game time
     * @return Seconds remaining in the game
     */
    public float getRemainingTime() {
        return Math.max(0, GAME_DURATION - gameTimer);
    }
    
    /**
     * Checks if the game is active
     * @return True if game is active, false if game over
     */
    public boolean isGameActive() {
        return gameActive;
    }
    
    /**
     * Sets whether the game is active
     * @param active Whether the game should be active
     */
    public void setGameActive(boolean active) {
        this.gameActive = active;
    }
}