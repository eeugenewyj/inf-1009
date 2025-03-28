package com.mygdx.game.GameState;

public class GameSettings {
    // Difficulty constants
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;

    // Current difficulty setting
    private static int currentDifficulty = DIFFICULTY_EASY;

    /**
     * Sets the game difficulty
     * 
     * @param difficulty The difficulty level (use constants DIFFICULTY_EASY or
     *                   DIFFICULTY_HARD)
     */
    public static void setDifficulty(int difficulty) {
        if (difficulty == DIFFICULTY_EASY || difficulty == DIFFICULTY_HARD) {
            currentDifficulty = difficulty;
            System.out.println("Game difficulty set to: " +
                    (difficulty == DIFFICULTY_EASY ? "Easy" : "Hard"));
        } else {
            System.out.println("Invalid difficulty setting");
        }
    }

    // Gets the current difficulty setting
    public static int getDifficulty() {
        return currentDifficulty;
    }

    /**
     * Checks if the game is set to Easy difficulty
     * 
     * @return true if the game is in Easy mode
     */
    public static boolean isEasyMode() {
        return currentDifficulty == DIFFICULTY_EASY;
    }

    /**
     * Checks if the game is set to Hard difficulty
     * 
     * @return true if the game is in Hard mode
     */
    public static boolean isHardMode() {
        return currentDifficulty == DIFFICULTY_HARD;
    }

    /**
     * Gets the difficulty name as string
     * 
     * @return "Easy" or "Hard" based on current setting
     */
    public static String getDifficultyName() {
        return currentDifficulty == DIFFICULTY_EASY ? "Easy" : "Hard";
    }
}