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
    private Label gameOverLabel;
    private Label finalScoreLabel;
    private TextButton restartButton;
    private TextButton homeButton;
    
    // Flag to track if this is a new game or resume
    private boolean isFirstLoad = true;

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
        
        // Set up timer label
        timerLabel = new Label("Time: 20.0", skin);
        timerLabel.setPosition(20, Gdx.graphics.getHeight() - 80);
        timerLabel.setFontScale(1.5f);
        
        // Set up game over elements (initially hidden)
        gameOverLabel = new Label("GAME OVER", skin);
        gameOverLabel.setFontScale(2.5f);
        gameOverLabel.setPosition(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f + 50);
        gameOverLabel.setAlignment(Align.center);
        gameOverLabel.setVisible(false);
        
        finalScoreLabel = new Label("Final Score: 0", skin);
        finalScoreLabel.setFontScale(2f);
        finalScoreLabel.setPosition(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
        finalScoreLabel.setAlignment(Align.center);
        finalScoreLabel.setVisible(false);
        
        restartButton = new TextButton("Play Again", skin);
        restartButton.setSize(200, 50);
        restartButton.setPosition(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 70);
        restartButton.setVisible(false);
        
        homeButton = new TextButton("Main Menu", skin);
        homeButton.setSize(200, 50);
        homeButton.setPosition(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 140);
        homeButton.setVisible(false);
        
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartGame();
            }
        });
        
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene("home");
            }
        });

        // Add UI elements to stage
        stage.addActor(pauseButton);
        stage.addActor(scoreLabel);
        stage.addActor(timerLabel);
        stage.addActor(gameOverLabel);
        stage.addActor(finalScoreLabel);
        stage.addActor(restartButton);
        stage.addActor(homeButton);

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
        playerScore += points;
        scoreLabel.setText("Score: " + playerScore);
    }
    
    /**
     * Ends the game and shows the game over screen
     */
    private void endGame() {
        gameActive = false;
        
        // Update game over UI
        gameOverLabel.setVisible(true);
        finalScoreLabel.setText("Final Score: " + playerScore);
        finalScoreLabel.setVisible(true);
        restartButton.setVisible(true);
        homeButton.setVisible(true);
        
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
        
        // Reset UI
        scoreLabel.setText("Score: 0");
        timerLabel.setText("Time: " + GAME_DURATION);
        
        // Hide game over UI
        gameOverLabel.setVisible(false);
        finalScoreLabel.setVisible(false);
        restartButton.setVisible(false);
        homeButton.setVisible(false);
        
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