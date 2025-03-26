package com.mygdx.game;

/**
 * Singleton class to preserve game state between scene transitions
 */
public class GameStatePreserver {
    private static GameStatePreserver instance; // Singleton instance of the GameStatePreserver

    // Game state variables
    private boolean hasPreservedState = false; // Flag to indicate if a state has been preserved
    private int playerScore = 0;
    private float gameTimer = 0;
    private boolean doublePointsActive = false; // Whether the double points power-up is active
    private float doublePointsTimer = 0; // Timer for the double points power-up
    private boolean invertControlsActive = false; // Whether the invert controls power-up is active
    private float invertControlsTimer = 0; // Timer for the invert controls power-up
    private boolean slowPlayerActive = false; // Whether the slow player power-up is active
    private float slowPlayerTimer = 0; // Timer for the slow player power-up

    // Additional states could be added as needed

    private GameStatePreserver() {
        // Private constructor for singleton
    }

    public static GameStatePreserver getInstance() {
        if (instance == null) {
            instance = new GameStatePreserver(); // Create a new instance if it doesn't exist
        }
        return instance;
    }

    // Stores the current game state
    public void preserveGameState(GameScene gameScene, GameStateManager stateManager, PowerUpManager powerUpManager) {
        this.playerScore = stateManager.getPlayerScore();
        this.gameTimer = stateManager.getGameTimer();
        this.doublePointsActive = powerUpManager.isDoublePointsActive();
        // Store other state variables as needed

        System.out.println("Game state preserved: Score=" + playerScore + ", Timer=" + gameTimer);
        this.hasPreservedState = true;
    }

    // Determines if there's a preserved statae to restore
    public boolean hasPreservedState() {
        return hasPreservedState;
    }

    // Clears the preserved state (e.g., when starting a new game)
    public void clearPreservedState() {
        hasPreservedState = false;
        playerScore = 0;
        gameTimer = 0;
        doublePointsActive = false;
        doublePointsTimer = 0;
        invertControlsActive = false;
        invertControlsTimer = 0;
        slowPlayerActive = false;
        slowPlayerTimer = 0;

        System.out.println("Game state cleared");
    }

    // Restores the preserved state to the given objects
    public void restoreGameState(GameScene gameScene, GameStateManager stateManager, PowerUpManager powerUpManager) {
        if (!hasPreservedState) {
            System.out.println("No state to restore");
            return;
        }

        // Restore state to the managers
        stateManager.setPlayerScore(playerScore);
        stateManager.setGameTimer(gameTimer);
        if (doublePointsActive) {
            powerUpManager.activateDoublePoints();
        }
        // Restore other state variables

        System.out.println("Game state restored: Score=" + playerScore + ", Timer=" + gameTimer);
    }

    // Getters for state variables
    public int getPlayerScore() {
        return playerScore;
    }

    public float getGameTimer() {
        return gameTimer;
    }

    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }
}