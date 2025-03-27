package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.AbstractIO.Audio;
import com.mygdx.game.AbstractIO.iInputManager;
import com.mygdx.game.AbstractIO.iOutputManager;
import com.mygdx.game.AbstractScene.iSceneManager;
import com.mygdx.game.AbstractScene.Scene;

/**
 * The main game scene, coordinating all game components
 */
public class GameScene extends Scene {
    // Components
    private GameUIManager uiManager;
    private GameLoopHandler gameLoop;
    private GameStateAdapter stateAdapter;
    private SceneContextAdapter contextAdapter;
    private EntityScoreAdapter scoreAdapter;
    
    // Managers
    private Stage stage;
    private Skin skin;
    private Audio audio;
    private GameEntityManager entityManager;
    private GameCollisionManager collisionManager;
    private GameStateManager gameStateManager;
    private PowerUpManager powerUpManager;
    
    /**
     * Creates a new game scene
     * 
     * @param sceneManager The scene manager
     * @param inputManager The input manager
     * @param outputManager The output manager
     */
    public GameScene(iSceneManager sceneManager, iInputManager inputManager, iOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");
        
        // Initialize the stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        // Load UI Skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        // Initialize Audio
        audio = Audio.getInstance();
        audio.loadSoundEffect("spikes", "Music/spikes.mp3");
        audio.loadSoundEffect("player", "Music/collisioneffect.mp3");
        audio.loadSoundEffect("powerup", "Music/powerup.mp3");
        audio.loadSoundEffect("debuff", "Music/debuff.mp3");
        
        audio.setSoundEffectVolume("powerup", 0.3f);
        audio.setSoundEffectVolume("debuff", 0.3f);
        
        // Initialize managers and components
        initializeComponents();
    }
    
    /**
     * Initializes all game components
     */
    private void initializeComponents() {
        // Initialize UI Manager with callbacks
        uiManager = new GameUIManager(
            stage, 
            skin,
            // Pause action
            () -> {
                // Save the current state
                GameStatePreserver.getInstance().preserveGameState(
                    this, gameStateManager, powerUpManager);
                gameStateManager.pauseGame();
                sceneManager.setScene("stop");
            },
            // Restart action
            () -> {
                restartGame();
            },
            // Home action
            () -> {
                sceneManager.setScene("home");
            }
        );
        
        
        // Set high scores action
        uiManager.setOnHighScoresAction(() -> {
            sceneManager.setScene("highscores");
        });
        
        // Initialize entity management
        entityManager = new GameEntityManager();
        
        // Initialize game state management
        gameStateManager = new GameStateManager(null, audio); // Will set listener below
        
        // Initialize adapters
        stateAdapter = new GameStateAdapter(
            uiManager,
            null, // Will set game loop below
            () -> {
                endGame();
            }
        );
        
        // Set the listener in game state manager
        gameStateManager.setGameStateListener(stateAdapter);
        
        // Initialize context adapter
        contextAdapter = new SceneContextAdapter(
            entityManager,
            uiManager,
            inputManager,
            gameStateManager
        );
        
        // Initialize power-up manager
        powerUpManager = new PowerUpManager(contextAdapter, gameStateManager);
        
        // Initialize score adapter
        scoreAdapter = new EntityScoreAdapter(gameStateManager, powerUpManager);
        
        // Initialize collision manager
        collisionManager = new GameCollisionManager(entityManager, scoreAdapter);
        
        // Initialize game loop
        gameLoop = new GameLoopHandler(
            entityManager,
            collisionManager,
            gameStateManager,
            powerUpManager,
            inputManager
        );
        
        // Set the game loop in state adapter
        stateAdapter.setGameLoop(gameLoop);
        
        // Initialize game
        initializeGame();
    }
    
    @Override
    public void show() {
        super.show();
        
        // Get the StopScene instance from the scene manager
        StopScene stopScene = null;
        if (sceneManager instanceof GameSceneManager) {
            stopScene = ((GameSceneManager) sceneManager).getStopScene();
        }
        
        // Check if we should restart the game using the instance method
        if (stopScene != null && stopScene.shouldRestartGame()) {
            System.out.println("Restart flag detected - restarting game");
            GameStatePreserver.getInstance().clearPreservedState();
            restartGame();
        }
        // If there's a preserved state and we're not restarting, restore it
        else if (GameStatePreserver.getInstance().hasPreservedState()) {
            System.out.println("Resuming previous game state");
            
            // First, clear any leftover timers or effects
            if (powerUpManager != null) {
                powerUpManager.resetPowerUps();
            }
            
            // Then restore the preserved state
            GameStatePreserver.getInstance().restoreGameState(this, gameStateManager, powerUpManager);
            
            // Resume audio
            audio.resumeMusic();
        }
        
        Gdx.input.setInputProcessor(stage);
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
    
        // Draw background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Render entities if game is active - ONLY HERE, nowhere else
        if (gameLoop.isGameActive()) {
            entityManager.renderEntities(batch);
        }
        
        batch.end();
        
        // Update game logic if game is active
        if (gameLoop.isGameActive()) {
            boolean continueGame = gameLoop.update(delta);
            
            if (!continueGame) {
                // Check if this was due to escape key (pause)
                if (gameStateManager.isGameActive()) {
                    // Was escape key, open pause menu
                    GameStatePreserver.getInstance().preserveGameState(
                        this, gameStateManager, powerUpManager);
                    gameStateManager.pauseGame();
                    sceneManager.setScene("stop");
                }
            }
        }
        
        // Draw UI
        stage.act(delta);
        stage.draw();
    }
    
    /**
     * Initializes a new game
     */
    private void initializeGame() {
        // Set the entity manager for collision callback (to avoid circular dependency)
        entityManager.setScoreHandler(scoreAdapter);
        collisionManager.setScoreHandler(scoreAdapter);
        
        // Initialize the game
        gameLoop.initializeGame();
    }
    
    /**
     * Ends the game and shows the game over screen
     */
    private void endGame() {
        // Reset power-ups
        powerUpManager.resetPowerUps();
        
        // Clear power-up status
        uiManager.updatePowerUpLabel("");
        
        // Show the math facts popup, then game over screen
        stateAdapter.showGameOverWithMathFact(
            gameStateManager.getPlayerScore(), 
            HighScoresManager.getInstance().isNewBestScore(gameStateManager.getPlayerScore())
        );
    }
    
    /**
     * Restarts the game
     */
    public void restartGame() {
        // Reset the game loop and state
        gameLoop.restartGame();
        
        // Reset UI elements
        uiManager.updatePowerUpLabel("");
        uiManager.hideGameOver();
        
        System.out.println("Game restarted!");
    }
    
    /**
     * Gets the current player score
     * 
     * @return The player's score
     */
    public int getPlayerScore() {
        return gameStateManager.getPlayerScore();
    }
    
    /**
     * Gets the entity manager (used by GameStatePreserver)
     * 
     * @return The entity manager
     */
    public GameEntityManager getEntityManager() {
        return entityManager;
    }
    
    /**
     * Gets the skin for UI elements
     * 
     * @return The UI skin
     */
    public Skin getSkin() {
        return skin;
    }

    public iInputManager getInputManager() {
        return inputManager;
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (stage != null)
            stage.dispose();
        if (skin != null)
            skin.dispose();
        if (entityManager != null)
            entityManager.dispose();
        if (collisionManager != null)
            collisionManager.dispose();
    }
    
    /**
     * Method needed to support state restoration
     * @param seconds Seconds to extend
     */
    public void extendGameTime(float seconds) {
        gameStateManager.extendGameTime(seconds);
    }
    
    /**
     * Method needed to support state restoration
     * @param seconds Seconds to reduce
     */
    public void reduceGameTime(float seconds) {
        gameStateManager.reduceGameTime(seconds);
    }
}