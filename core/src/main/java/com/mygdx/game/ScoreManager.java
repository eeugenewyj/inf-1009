package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * A simplified score manager that handles score updates and high scores
 */
public class ScoreManager {
    private int playerScore = 0;
    private Label scoreLabel;
    private boolean scoreSaved = false;

    /**
     * Creates a new score manager
     * @param scoreLabel The label to display the score
     */
    public ScoreManager(Label scoreLabel) {
        this.scoreLabel = scoreLabel;
        updateScoreDisplay();
    }

    /**
     * Adds points to the player's score
     * @param points The points to add
     * @param multiplier Score multiplier (e.g., 2 for double points)
     */
    public void addScore(int points, int multiplier) {
        int actualPoints = points * multiplier;
        playerScore += actualPoints;
        updateScoreDisplay();
        
        if (multiplier > 1) {
            System.out.println("Double points bonus! Added " + actualPoints + " points!");
        }
    }

    /**
     * Updates the score display with current score
     */
    private void updateScoreDisplay() {
        scoreLabel.setText("Score: " + playerScore);
    }

    /**
     * Saves the current score to the high scores list
     * @return true if this is a new best score, false otherwise
     */
    public boolean saveScore() {
        if (scoreSaved) return false;
        
        HighScoresManager highScoresManager = HighScoresManager.getInstance();
        boolean isNewBestScore = highScoresManager.addScore(playerScore);
        
        // Debug logs to help troubleshoot
        System.out.println("Game ended with score: " + playerScore);
        System.out.println("Game difficulty: " + GameSettings.getDifficultyName());
        System.out.println("Is new best score: " + isNewBestScore);
        
        scoreSaved = true;
        return isNewBestScore;
    }

    /**
     * Resets the score and score-saved flag
     */
    public void reset() {
        playerScore = 0;
        scoreSaved = false;
        updateScoreDisplay();
    }

    /**
     * Gets the current player score
     */
    public int getPlayerScore() {
        return playerScore;
    }
    
    /**
     * Gets whether the score has been saved
     */
    public boolean isScoreSaved() {
        return scoreSaved;
    }
    
    /**
     * Sets the score saved status
     */
    public void setScoreSaved(boolean saved) {
        this.scoreSaved = saved;
    }
}