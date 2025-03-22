package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractIO.Audio;
import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.ISceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class GameScene extends Scene {
    private Stage stage; // Handles UI elemements
    private Skin skin; // UI skin for button styling
    private Audio audio;
    private ImageButton pauseButton;
    private GameEntityManager entityManager;
    private GameCollisionManager collisionManager;
    
    // Added for score system
    private int playerScore = 0;
    private Label scoreLabel;
    
    // Added for timer
    private float gameTimer = 0;
    private static final float GAME_DURATION = 20f; // 20 seconds
    private Label timerLabel;
    private boolean gameActive = true;
    
    // Game over elements
    private Table gameOverTable; // Container for game over UI elements
    private Label gameOverLabel;
    private Label finalScoreLabel;
    private Label highScoreLabel; // New label for high score notification
    private TextButton restartButton;
    private TextButton homeButton;
    
    // Flag to track if this is a new game or resume
    private boolean isFirstLoad = true;
    
    // Flag to track if the current game score has been saved
    private boolean scoreSaved = false;
    
    // Power-up related fields
    private boolean doublePointsActive = false;
    private float doublePointsTimer = 0;
    private static final float DOUBLE_POINTS_DURATION = 3f; // 3 seconds
    private Label powerUpLabel; // For displaying active power-ups
    
    // Debuff timers and states
    private boolean invertControlsActive = false;
    private float invertControlsTimer = 0;
    private static final float INVERT_CONTROLS_DURATION = 5f; // 5 seconds

    private boolean slowPlayerActive = false;
    private float slowPlayerTimer = 0;
    private static final float SLOW_PLAYER_DURATION = 4f; // 4 seconds
    private float originalPlayerSpeed = 200f; // Store original speed to restore later

    public GameScene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");

        // Initialize game components
        entityManager = new GameEntityManager(this);
        collisionManager = new GameCollisionManager(entityManager, this);

        // Initialize the stage (used for UI elements)
        // Stage automatically handles input events, so you don't have to manually check
        // for clicks
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set stage to process input

        // Load UI Skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Initialize Audio
        audio = Audio.getInstance();
        audio.loadSoundEffect("tree", "Music/tree.mp3");
        audio.loadSoundEffect("player", "Music/collisioneffect.mp3");
        // Add power-up sound effect (create this file or use an existing one)
        audio.loadSoundEffect("powerup", "Music/collisioneffect.mp3"); // Using existing sound as fallback
        // Add debuff sound effect (you can create this file or use an existing one)
        audio.loadSoundEffect("debuff", "Music/collisioneffect.mp3"); // Using existing sound as fallback

        // Load Pause Button
        pauseButton = new ImageButton(new TextureRegionDrawable(new Texture(Gdx.files.internal("pause.png"))));
        pauseButton.setPosition(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 60);
        pauseButton.setSize(50, 50);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Pause Button Clicked! Opening StopScene...");
                audio.pauseMusic();
                sceneManager.setScene("stop");
            }
        });
        
        // Set up score label
        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 40);
        scoreLabel.setFontScale(1.5f);
        scoreLabel.setColor(Color.WHITE); // White for better visibility
        
        // Set up timer label
        timerLabel = new Label("Time: 20.0", skin);
        timerLabel.setPosition(20, Gdx.graphics.getHeight() - 80);
        timerLabel.setFontScale(1.5f);
        timerLabel.setColor(Color.WHITE); // White for better visibility
        
        // Set up power-up status label
        powerUpLabel = new Label("", skin);
        powerUpLabel.setPosition(20, Gdx.graphics.getHeight() - 120);
        powerUpLabel.setFontScale(1.5f);
        powerUpLabel.setColor(Color.YELLOW); // Yellow for better visibility
        
        // Create game over table for centered layout
        gameOverTable = new Table();
        gameOverTable.setFillParent(true);
        gameOverTable.setVisible(false);
        
        // Set up game over elements
        gameOverLabel = new Label("GAME OVER", skin);
        gameOverLabel.setFontScale(2.5f);
        gameOverLabel.setAlignment(Align.center);
        gameOverLabel.setColor(Color.RED); // Bright red for game over text
        
        finalScoreLabel = new Label("Final Score: 0", skin);
        finalScoreLabel.setFontScale(2.0f);
        finalScoreLabel.setAlignment(Align.center);
        finalScoreLabel.setColor(Color.CYAN); // Bright cyan for better visibility
        
        // New high score label
        highScoreLabel = new Label("NEW HIGH SCORE!", skin);
        highScoreLabel.setFontScale(1.5f);
        highScoreLabel.setColor(Color.GOLD); // Brighter gold color for high score
        highScoreLabel.setAlignment(Align.center);
        
        restartButton = new TextButton("Play Again", skin);
        restartButton.getLabel().setColor(Color.WHITE); // Ensure text is white
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartGame();
            }
        });
        
        homeButton = new TextButton("Main Menu", skin);
        homeButton.getLabel().setColor(Color.WHITE); // Ensure text is white
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene("home");
            }
        });
        
        // Add elements to the game over table
        gameOverTable.add(gameOverLabel).padBottom(30).row();
        gameOverTable.add(finalScoreLabel).padBottom(20).row();
        gameOverTable.add(highScoreLabel).padBottom(30).row();
        gameOverTable.add(restartButton).size(200, 50).padBottom(20).row();
        gameOverTable.add(homeButton).size(200, 50).row();
        
        // Initially hide the high score label
        highScoreLabel.setVisible(false);

        // Add UI elements to stage
        stage.addActor(pauseButton);
        stage.addActor(scoreLabel);
        stage.addActor(timerLabel);
        stage.addActor(powerUpLabel);
        stage.addActor(gameOverTable);

        initializeGame();
    }

    @Override
    public void show() {
        super.show();
        
        // Check if we should restart the game (flag set from StopScene)
        if (StopScene.shouldRestartGame()) {
            System.out.println("Restart flag detected - restarting game");
            restartGame();
        }
        
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        if (gameActive) {
            entityManager.renderEntities(batch); // Delegates drawing to EntityManager
        }
        
        batch.end();

        if (gameActive) {
            // Update game timer
            gameTimer += delta;
            float timeRemaining = GAME_DURATION - gameTimer;
            
            if (timeRemaining <= 0) {
                // Game over
                endGame();
            } else {
                // Update timer display
                timerLabel.setText(String.format("Time: %.1f", timeRemaining));
                
                // Handle power-up timers
                if (doublePointsActive) {
                    doublePointsTimer += delta;
                    updatePowerUpLabel();
                    
                    if (doublePointsTimer >= DOUBLE_POINTS_DURATION) {
                        doublePointsActive = false;
                        updatePowerUpLabel();
                        System.out.println("Double Points expired!");
                    }
                }
                
                // Handle invert controls timer
                if (invertControlsActive) {
                    invertControlsTimer += delta;
                    updatePowerUpLabel();
                    
                    if (invertControlsTimer >= INVERT_CONTROLS_DURATION) {
                        invertControlsActive = false;
                        // Reset invert flag on all players
                        for (Entity entity : entityManager.getEntities()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                player.setInvertControls(false);
                            }
                        }
                        updatePowerUpLabel();
                        System.out.println("Controls back to normal!");
                    }
                }

                // Handle slow player timer
                if (slowPlayerActive) {
                    slowPlayerTimer += delta;
                    updatePowerUpLabel();
                    
                    if (slowPlayerTimer >= SLOW_PLAYER_DURATION) {
                        slowPlayerActive = false;
                        // Reset speed on all players
                        for (Entity entity : entityManager.getEntities()) {
                            if (entity instanceof Player) {
                                Player player = (Player) entity;
                                player.setSpeed(originalPlayerSpeed);
                            }
                        }
                        updatePowerUpLabel();
                        System.out.println("Player speed back to normal!");
                    }
                }
                
                // Update player movement using the new input system
                for (Entity entity : entityManager.getEntities()) {
                    if (entity instanceof Player) {
                        ((Player) entity).moveUserControlled(delta);
                    }
                }

                // Update & Move Entities
                entityManager.updateEntities(delta);

                // Collision Detection
                if (!entityManager.getEntities().isEmpty()) {
                    collisionManager.detectCollisions();
                }

                // Handle escape key
                if (inputManager.isActionPressed(Input.Keys.ESCAPE)) {
                    sceneManager.setScene("stop");
                }
            }
        }

        // Ensure UI Elements (Buttons) Render Last
        stage.act(delta);
        stage.draw();
    }

    private void initializeGame() {
        // Initialize EntityManager with reference to this GameScene for score updates
        entityManager = new GameEntityManager(this);
        collisionManager = new GameCollisionManager(entityManager, this); // Initialize CollisionManager with reference to GameScene
        
        // Reset score saved flag
        scoreSaved = false;
        
        // Spawn different entities
        entityManager.spawnPlayers(1, inputManager);
        entityManager.spawnTrees(5); // Spawn trees using EntityManager
        entityManager.spawnBallsRow(); // Spawn balls using EntityManager
    }
    
    /**
     * Adds points to the player's score
     * @param points The points to add
     */
    public void addScore(int points) {
        // Double the points if the power-up is active
        int actualPoints = doublePointsActive ? points * 2 : points;
        playerScore += actualPoints;
        scoreLabel.setText("Score: " + playerScore);
        
        // Show a visual indicator of the points gained
        if (doublePointsActive) {
            System.out.println("Double points bonus! Added " + actualPoints + " points!");
        }
    }
    
    /**
     * Activates double points for a fixed duration
     */
    public void activateDoublePoints() {
        doublePointsActive = true;
        doublePointsTimer = 0;
        updatePowerUpLabel();
        System.out.println("Double Points activated!");
    }

    /**
     * Extends the game time by the specified amount
     * @param seconds The number of seconds to add
     */
    public void extendGameTime(float seconds) {
        gameTimer = Math.max(0, gameTimer - seconds); // Subtract from timer to extend time
        updatePowerUpLabel();
        System.out.println("Game time extended by " + seconds + " seconds!");
    }
    
    /**
     * Reduces the game time by the specified amount
     * @param seconds The number of seconds to subtract
     */
    public void reduceGameTime(float seconds) {
        gameTimer = Math.min(GAME_DURATION - 1, gameTimer + seconds); // Add to timer to reduce time, ensure at least 1 second remains
        updatePowerUpLabel();
        System.out.println("Game time reduced by " + seconds + " seconds!");
    }

    /**
     * Activates inverted controls for a fixed duration
     */
    public void activateInvertControls() {
        invertControlsActive = true;
        invertControlsTimer = 0;
        
        // Set invert flag on all players
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.setInvertControls(true);
            }
        }
        
        updatePowerUpLabel();
        System.out.println("Controls inverted!");
    }

    /**
     * Activates slow player movement for a fixed duration
     */
    public void activateSlowPlayer() {
        slowPlayerActive = true;
        slowPlayerTimer = 0;
        
        // Slow down all players
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                originalPlayerSpeed = player.getSpeed(); // Save original speed
                player.setSpeed(originalPlayerSpeed * 0.5f); // Reduce to 50%
            }
        }
        
        updatePowerUpLabel();
        System.out.println("Player slowed!");
    }

    /**
     * Updates the power-up label to show active effects
     */
    private void updatePowerUpLabel() {
        StringBuilder labelText = new StringBuilder();
        
        if (doublePointsActive) {
            labelText.append("DOUBLE POINTS! ");
            float remaining = DOUBLE_POINTS_DURATION - doublePointsTimer;
            labelText.append(String.format("(%.1fs)", remaining));
        }
        
        if (invertControlsActive) {
            if (labelText.length() > 0) {
                labelText.append(" | ");
            }
            labelText.append("INVERTED CONTROLS! ");
            float remaining = INVERT_CONTROLS_DURATION - invertControlsTimer;
            labelText.append(String.format("(%.1fs)", remaining));
        }
        
        if (slowPlayerActive) {
            if (labelText.length() > 0) {
                labelText.append(" | ");
            }
            labelText.append("SLOWED! ");
            float remaining = SLOW_PLAYER_DURATION - slowPlayerTimer;
            labelText.append(String.format("(%.1fs)", remaining));
        }
        
        powerUpLabel.setText(labelText.toString());
    }
    
    /**
     * Ends the game and shows the game over screen
     */
    private void endGame() {
        gameActive = false;
        
        // Clear power-up status
        doublePointsActive = false;
        doublePointsTimer = 0;
        invertControlsActive = false;
        slowPlayerActive = false;
        powerUpLabel.setText(""); // Clear the power-up label text
        
        // Reset any player modifications
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.setInvertControls(false);
                player.setSpeed(originalPlayerSpeed);
            }
        }
        
        // Save the score to high scores if not already done
        if (!scoreSaved) {
            HighScoresManager highScoresManager = HighScoresManager.getInstance();
            
            // Get current best score before adding new score
            int previousBest = highScoresManager.getBestScore();
            
            // Add score to high scores list
            boolean isNewBestScore = highScoresManager.addScore(playerScore);
            
            // Debug logs to help troubleshoot
            System.out.println("Game ended with score: " + playerScore);
            System.out.println("Previous best score: " + previousBest);
            System.out.println("Is new best score: " + isNewBestScore);
            
            // Only show NEW HIGH SCORE label if it's a truly new best score
            highScoreLabel.setVisible(isNewBestScore);
            
            scoreSaved = true;
        }
        
        // Update game over UI
        finalScoreLabel.setText("Final Score: " + playerScore);
        gameOverTable.setVisible(true);
        
        // Hide game UI
        pauseButton.setVisible(false);
    }
    
    /**
     * Restarts the game - public so it can be called from StopScene
     */
    public void restartGame() {
        // Reset game state
        gameActive = true;
        gameTimer = 0;
        playerScore = 0;
        scoreSaved = false;
        doublePointsActive = false;
        doublePointsTimer = 0;
        invertControlsActive = false;
        invertControlsTimer = 0;
        slowPlayerActive = false;
        slowPlayerTimer = 0;
        updatePowerUpLabel();
        
        // Reset UI
        scoreLabel.setText("Score: 0");
        timerLabel.setText("Time: " + GAME_DURATION);
        
        // Hide game over UI
        gameOverTable.setVisible(false);
        highScoreLabel.setVisible(false);
        
        // Show game UI
        pauseButton.setVisible(true);
        
        // Reset entities
        if (entityManager != null) {
            entityManager.dispose();
        }
        
        initializeGame();
        
        System.out.println("Game restarted!");
    }

    @Override
    public void dispose() {
        super.dispose();
        if (stage != null)
            stage.dispose();
        if (skin != null)
            skin.dispose(); // Fix: Dispose Skin to prevent memory leak
        if (entityManager != null)
            entityManager.dispose();
        if (collisionManager != null)
            collisionManager.dispose();
    }
    
    /**
     * Get the current player score
     * @return The player's score
     */
    public int getPlayerScore() {
        return playerScore;
    }
}