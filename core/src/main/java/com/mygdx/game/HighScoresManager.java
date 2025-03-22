package com.mygdx.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages high scores for the game
 */
public class HighScoresManager {
    private static HighScoresManager instance;
    private List<Integer> highScores;
    private static final int MAX_SCORES = 5;  // Keep only top 5 scores
    
    private HighScoresManager() {
        this.highScores = new ArrayList<>();
    }
    
    /**
     * Singleton access method
     */
    public static HighScoresManager getInstance() {
        if (instance == null) {
            instance = new HighScoresManager();
        }
        return instance;
    }
    
    /**
     * Adds a new score to the high scores list
     * @param score The score to add
     * @return true if this is a NEW best score (better than all previous scores)
     */
    public boolean addScore(int score) {
        boolean isNewBestScore = false;
        
        // Check if this is a new best score (better than ALL previous scores)
        if (highScores.isEmpty() || score > Collections.max(highScores)) {
            isNewBestScore = true;
            System.out.println("NEW BEST SCORE! Score: " + score);
        } else {
            System.out.println("Not a new best score. Score: " + score + ", Current best: " + Collections.max(highScores));
        }
        
        // Add the score to our list
        highScores.add(score);
        
        // Sort in descending order
        Collections.sort(highScores, Collections.reverseOrder());
        
        // Keep only the top MAX_SCORES
        if (highScores.size() > MAX_SCORES) {
            highScores = new ArrayList<>(highScores.subList(0, MAX_SCORES));
        }
         
        System.out.println("Current high scores: " + highScores);
        
        return isNewBestScore;
    }
    
    /**
     * Returns the list of high scores in descending order
     */
    public List<Integer> getHighScores() {
        return new ArrayList<>(highScores);  // Return a copy to prevent modification
    }
    
    /**
     * Gets the current best score, or 0 if no scores exist
     */
    public int getBestScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(0);
    }
    
    /**
     * Clears all high scores
     */
    public void clearHighScores() {
        highScores.clear();
    }
}