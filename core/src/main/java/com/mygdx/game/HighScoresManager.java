package com.mygdx.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoresManager {
    private static HighScoresManager instance; // Singleton instance
    // Lists to store high scores for easy and hard modes
    private List<Integer> easyHighScores;
    private List<Integer> hardHighScores;

    private static final int MAX_SCORES = 5; // Keep only top 5 scores

    // Private constructor to enforce the singleton pattern
    private HighScoresManager() {
        this.easyHighScores = new ArrayList<>();
        this.hardHighScores = new ArrayList<>();
    }

    // Singleton access method
    public static HighScoresManager getInstance() {
        if (instance == null) {
            instance = new HighScoresManager();
        }
        return instance;
    }

    /**
     * Adds a new score to the high scores list for the current difficulty
     * 
     * @param score The score to add
     * @return true if this is a NEW best score (better than all previous scores)
     */
    public boolean addScore(int score) {
        if (GameSettings.isEasyMode()) {
            return addEasyScore(score);
        } else {
            return addHardScore(score);
        }
    }

    /**
     * Adds a score to the Easy mode high scores
     * 
     * @param score The score to add
     * @return true if it's a new best score
     */
    private boolean addEasyScore(int score) {
        boolean isNewBestScore = false;

        // Check if this is a new best score
        if (easyHighScores.isEmpty() || score > Collections.max(easyHighScores)) {
            isNewBestScore = true;
            System.out.println("NEW BEST EASY SCORE! Score: " + score);
        }

        // Add the score to our list
        easyHighScores.add(score);

        // Sort in descending order
        Collections.sort(easyHighScores, Collections.reverseOrder());

        // Keep only the top MAX_SCORES
        if (easyHighScores.size() > MAX_SCORES) {
            easyHighScores = new ArrayList<>(easyHighScores.subList(0, MAX_SCORES));
        }

        System.out.println("Current easy high scores: " + easyHighScores);

        return isNewBestScore;
    }

    /**
     * Adds a score to the Hard mode high scores
     * 
     * @param score The score to add
     * @return true if it's a new best score
     */
    private boolean addHardScore(int score) {
        boolean isNewBestScore = false;

        // Check if this is a new best score
        if (hardHighScores.isEmpty() || score > Collections.max(hardHighScores)) {
            isNewBestScore = true;
            System.out.println("NEW BEST HARD SCORE! Score: " + score);
        }

        // Add the score to our list
        hardHighScores.add(score);

        // Sort in descending order
        Collections.sort(hardHighScores, Collections.reverseOrder());

        // Keep only the top MAX_SCORES
        if (hardHighScores.size() > MAX_SCORES) {
            hardHighScores = new ArrayList<>(hardHighScores.subList(0, MAX_SCORES));
        }

        System.out.println("Current hard high scores: " + hardHighScores);

        return isNewBestScore;
    }

    /**
     * Returns the list of high scores for the current difficulty
     */
    public List<Integer> getHighScores() {
        return GameSettings.isEasyMode() ? new ArrayList<>(easyHighScores) : new ArrayList<>(hardHighScores);
    }

    /**
     * Returns the list of easy mode high scores
     */
    public List<Integer> getEasyHighScores() {
        return new ArrayList<>(easyHighScores);
    }

    /**
     * Returns the list of hard mode high scores
     */
    public List<Integer> getHardHighScores() {
        return new ArrayList<>(hardHighScores);
    }

    /**
     * Gets the current best score for the selected difficulty, or 0 if no scores
     * exist
     */
    public int getBestScore() {
        List<Integer> scores = GameSettings.isEasyMode() ? easyHighScores : hardHighScores;
        if (scores.isEmpty()) {
            return 0;
        }
        return scores.get(0);
    }

    /**
     * Gets the best easy mode score
     */
    public int getBestEasyScore() {
        if (easyHighScores.isEmpty()) {
            return 0;
        }
        return easyHighScores.get(0);
    }

    /**
     * Gets the best hard mode score
     */
    public int getBestHardScore() {
        if (hardHighScores.isEmpty()) {
            return 0;
        }
        return hardHighScores.get(0);
    }

    /**
     * Clears all high scores for both difficulties
     */
    public void clearAllHighScores() {
        easyHighScores.clear();
        hardHighScores.clear();
    }

    /**
     * Clears high scores for the specified difficulty
     * 
     * @param difficulty The difficulty to clear (GameSettings.DIFFICULTY_EASY or
     *                   DIFFICULTY_HARD)
     */
    public void clearHighScores(int difficulty) {
        if (difficulty == GameSettings.DIFFICULTY_EASY) {
            easyHighScores.clear();
        } else if (difficulty == GameSettings.DIFFICULTY_HARD) {
            hardHighScores.clear();
        }
    }
}