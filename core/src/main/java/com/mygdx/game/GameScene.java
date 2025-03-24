package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    
    // Managers for score and power-ups
    private ScoreManager scoreManager;
    private PowerUpManager powerUpManager;
    
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
    
    // Power-up label
    private Label powerUpLabel;

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
        audio.loadSoundEffect("powerup", "Music/powerup.mp3");
        // Add debuff sound effect 
        audio.loadSoundEffect("debuff", "Music/debuff.mp3"); 

        audio.setSoundEffectVolume("powerup", 0.3f); // Reduce to 30% volume
        audio.setSoundEffectVolume("debuff", 0.3f); // Reduce to 30% volume

        // Load Pause Button
        Texture pauseTexture = new Texture(Gdx.files.internal("pause.png"));
        pauseButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(pauseTexture)));
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
                // Directly restart the game without showing a math fact
                restartGame();
            }
        });
        
        homeButton = new TextButton("Main Menu", skin);
        homeButton.getLabel().setColor(Color.WHITE); // Ensure text is white
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Directly go to home without showing a math fact
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

        // Initialize score and power-up managers
        scoreManager = new ScoreManager(scoreLabel);
        powerUpManager = new PowerUpManager(powerUpLabel, entityManager, this);

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
                
                // Update power-ups
                powerUpManager.update(delta);
                
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
        scoreManager.setScoreSaved(false);
        
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
        // Use PowerUpManager for double points check and ScoreManager for score addition
        int multiplier = powerUpManager.isDoublePointsActive() ? 2 : 1;
        scoreManager.addScore(points, multiplier);
    }
    
    /**
     * Activates double points for a fixed duration
     */
    public void activateDoublePoints() {
        powerUpManager.activateDoublePoints();
    }

    /**
     * Extends the game time by the specified amount
     * @param seconds The number of seconds to add
     */
    public void extendGameTime(float seconds) {
        gameTimer = Math.max(0, gameTimer - seconds); // Subtract from timer to extend time
        System.out.println("Game time extended by " + seconds + " seconds!");
    }
    
    /**
     * Reduces the game time by the specified amount
     * @param seconds The number of seconds to subtract
     */
    public void reduceGameTime(float seconds) {
        gameTimer = Math.min(GAME_DURATION - 1, gameTimer + seconds); // Add to timer to reduce time, ensure at least 1 second remains
        System.out.println("Game time reduced by " + seconds + " seconds!");
    }

    /**
     * Activates inverted controls for a fixed duration
     */
    public void activateInvertControls() {
        powerUpManager.activateInvertControls();
    }

    /**
     * Activates slow player movement for a fixed duration
     */
    public void activateSlowPlayer() {
        powerUpManager.activateSlowPlayer();
    }

    /**
     * Ends the game and shows a math fact before the game over screen
     */
    private void endGame() {
        gameActive = false;
        
        // Clear power-up status
        powerUpManager.reset();
        
        // Save the score to high scores if not already done
        boolean isNewBestScore = scoreManager.saveScore();
        
        // Debug logs to help troubleshoot
        System.out.println("Game ended with score: " + scoreManager.getPlayerScore());
        System.out.println("Game difficulty: " + GameSettings.getDifficultyName());
        System.out.println("Is new best score: " + isNewBestScore);
        
        // Setup game over UI (but don't show it yet)
        finalScoreLabel.setText("Final Score: " + scoreManager.getPlayerScore());
        highScoreLabel.setVisible(isNewBestScore);
        
        // If it's a new high score, add another button to go directly to high scores
        if (isNewBestScore) {
            // Check if the button already exists to avoid duplicates
            boolean hasViewScoresButton = false;
            for (com.badlogic.gdx.scenes.scene2d.Actor actor : gameOverTable.getChildren()) {
                if (actor instanceof TextButton && ((TextButton)actor).getText().toString().equals("View High Scores")) {
                    hasViewScoresButton = true;
                    break;
                }
            }
            
            if (!hasViewScoresButton) {
                TextButton viewScoresButton = new TextButton("View High Scores", skin);
                viewScoresButton.getLabel().setColor(Color.GOLD);
                
                viewScoresButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        // Directly go to high scores without showing a math fact
                        sceneManager.setScene("highscores");
                    }
                });
                
                // Add to the game over table after the other buttons
                gameOverTable.add(viewScoresButton).size(200, 50).padTop(20).row();
            }
        }
        
        // Hide game UI
        pauseButton.setVisible(false);
        
        // Create a new MathFactsPopup with a fresh random fact
        MathFactsPopup popup = new MathFactsPopup(skin, () -> {
            // This will run after the popup is closed
            gameOverTable.setVisible(true);
        });
        
        // Explicitly refresh the fact to get a new random one
        popup.refreshFact();
        
        // Show the popup
        popup.show(stage);
    }
    
    /**
     * Restarts the game - public so it can be called from StopScene
     */
    public void restartGame() {
        // Reset game state
        gameActive = true;
        gameTimer = 0;
        scoreManager.reset();
        powerUpManager.reset();
        
        // Reset UI
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
        return scoreManager.getPlayerScore();
    }
}