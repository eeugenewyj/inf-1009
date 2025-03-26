package com.mygdx.game;

/**
 * Interface for components that need to respond to game state changes.
 * This breaks the circular dependency between GameStateManager and GameScene.
 */
public interface GameStateListener {
    /**
     * Called when the score changes
     * 
     * @param newScore The updated score
     */
    void onScoreChanged(int newScore);
    
    /**
     * Called when the game timer updates
     * 
     * @param remainingTime The remaining time in seconds
     */
    void onTimerUpdated(float remainingTime);
    
    /**
     * Called when the game ends
     * 
     * @param finalScore The final score achieved
     * @param isNewHighScore Whether this is a new high score
     */
    void onGameOver(int finalScore, boolean isNewHighScore);
    
    /**
     * Shows the game over screen
     * 
     * @param finalScore The final score
     * @param isNewHighScore Whether this is a new high score
     */
    void showGameOver(int finalScore, boolean isNewHighScore);
}