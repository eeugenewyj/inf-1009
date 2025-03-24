package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.AbstractScene.ISceneManager;

/**
 * Manages the overall game state and transitions
 */
public class GameStateManager {
    // Game state constants
    public static final int STATE_PLAYING = 0;
    public static final int STATE_GAME_OVER = 1;
    public static final int STATE_PAUSED = 2;
    
    private int currentState = STATE_PLAYING;
    private boolean isFirstLoad = true;
    private boolean gameActive = true;
    
    private ISceneManager sceneManager;
    private TimerManager timerManager;
    private ScoreManager scoreManager;
    private PowerUpManager powerUpManager;
    private GameUIManager uiManager;
    private GameEntityManager entityManager;
    private GameCollisionManager collisionManager;
    
    public GameStateManager(ISceneManager sceneManager, TimerManager timerManager, 
                           ScoreManager scoreManager, PowerUpManager powerUpManager,
                           GameUIManager uiManager, GameEntityManager entityManager,
                           GameCollisionManager collisionManager) {
        this.sceneManager = sceneManager;
        this.timerManager = timerManager;
        this.scoreManager = scoreManager;
        this.powerUpManager = powerUpManager;
        this.uiManager = uiManager;
        this.entityManager = entityManager;
        this.collisionManager = collisionManager;
    }

    /**
     * Updates the game state
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {
        if (!gameActive) return;
        
        // Update timer and check for game over
        if (timerManager.update(delta)) {
            endGame();
            return;
        }
        
        // Update power-ups
        powerUpManager.update(delta);
        
        // Update entity movement using the new input system
        entityManager.updateEntities(delta);
        
        // Collision Detection
        if (!entityManager.getEntities().isEmpty()) {
            collisionManager.detectCollisions();
        }
    }

    /**
     * Ends the game and shows game over UI
     */
    public void endGame() {
        gameActive = false;
        currentState = STATE_GAME_OVER;
        
        // Clear power-up status
        powerUpManager.reset();
        
        // Save the score to high scores
        boolean isNewBestScore = scoreManager.saveScore();
        
        // Setup game over UI
        uiManager.showGameOver(scoreManager.getPlayerScore(), isNewBestScore);
    }

    /**
     * Restarts the game
     */
    public void restartGame() {
        // Reset all managers
        gameActive = true;
        currentState = STATE_PLAYING;
        timerManager.reset();
        scoreManager.reset();
        powerUpManager.reset();
        uiManager.hideGameOver();
        
        // Reset entities
        if (entityManager != null) {
            entityManager.dispose();
        }
        
        // Initialize game entities
        initializeGame();
        
        System.out.println("Game restarted!");
    }

    /**
     * Initializes game entities
     */
    private void initializeGame() {
        // Reset score saved flag
        scoreManager.setScoreSaved(false);
        
        // Clear any existing entities
        entityManager.dispose();
        
        // Spawn different entities directly using entity manager's methods
        // This ensures we're using the original spawning logic
        entityManager.spawnPlayers(1, null);
        entityManager.spawnTrees(5);
        entityManager.spawnBallsRow();
    }

    /**
     * Pauses the game
     */
    public void pauseGame() {
        gameActive = false;
        currentState = STATE_PAUSED;
    }

    /**
     * Resumes the game
     */
    public void resumeGame() {
        gameActive = true;
        currentState = STATE_PLAYING;
    }

    /**
     * Gets whether the game is active
     */
    public boolean isGameActive() {
        return gameActive;
    }

    /**
     * Gets the current game state
     */
    public int getCurrentState() {
        return currentState;
    }
}