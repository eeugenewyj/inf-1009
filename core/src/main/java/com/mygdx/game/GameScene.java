package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
    private boolean gameActive = true;
    private Label timerLabel;

    // Game over elements
    private Table gameOverTable; // Container for game over UI elements
    private Label gameOverLabel;
    private Label finalScoreLabel;
    private Label highScoreLabel; // New label for high score notification
    private TextButton restartButton;
    private TextButton homeButton;

    // Flag to track if this is a new game or resume
    private boolean isFirstLoad = true;

    // Power-up related fields
    private Label powerUpLabel; // For displaying active power-ups

    // Manager instances
    private PowerUpManager powerUpManager;
    private GameStateManager gameStateManager;

    public GameScene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");

        // Initialize the stage (used for UI elements)
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set stage to process input

        // Load UI Skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Initialize Audio
        audio = Audio.getInstance();
        audio.loadSoundEffect("tree", "Music/tree.mp3");
        audio.loadSoundEffect("player", "Music/collisioneffect.mp3");
        audio.loadSoundEffect("powerup", "Music/powerup.mp3");
        audio.loadSoundEffect("debuff", "Music/debuff.mp3");

        audio.setSoundEffectVolume("powerup", 0.3f);
        audio.setSoundEffectVolume("debuff", 0.3f);

        // Initialize game components
        entityManager = new GameEntityManager(this);
        collisionManager = new GameCollisionManager(entityManager, this);

        // Initialize managers (order matters - GameStateManager first, then
        // PowerUpManager)
        gameStateManager = new GameStateManager(this, audio);
        powerUpManager = new PowerUpManager(this, gameStateManager);

        // Initialize UI elements
        initializeUI();
        initializeGame();
    }

    @Override
    public void show() {
        super.show();

        // First check if we should restart the game (flag set from StopScene)
        if (StopScene.shouldRestartGame()) {
            System.out.println("Restart flag detected - restarting game");
            GameStatePreserver.getInstance().clearPreservedState();
            restartGame();
        }
        // If there's a preserved state and we're not restarting, restore it
        else if (GameStatePreserver.getInstance().hasPreservedState()) {
            System.out.println("Resuming previous game state");
            // Don't initialize a new game, restore the previous state

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

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameActive) {
            entityManager.renderEntities(batch); // Delegates drawing to EntityManager
        }

        batch.end();

        if (gameActive) {
            // Update game state
            boolean stillActive = gameStateManager.update(delta);

            if (stillActive) {
                // Update power-ups
                powerUpManager.update(delta);

                // Update player movement
                for (Entity entity : entityManager.getEntities()) {
                    if (entity instanceof Player) {
                        ((Player) entity).moveUserControlled(delta);
                    }
                }

                // Update entities
                entityManager.updateEntities(delta);

                // Collision Detection
                if (!entityManager.getEntities().isEmpty()) {
                    collisionManager.detectCollisions();
                }

                // Handle escape key
                if (inputManager.isActionPressed(Input.Keys.ESCAPE)) {
                    gameStateManager.pauseGame();
                    sceneManager.setScene("stop");
                }
            } else {
                // Game is no longer active (probably game over)
                endGame();
            }
        }

        // Ensure UI Elements (Buttons) Render Last
        stage.act(delta);
        stage.draw();
    }

    private void initializeUI() {
        // Load Pause Button
        pauseButton = new ImageButton(new TextureRegionDrawable(new Texture(Gdx.files.internal("pause.png"))));
        pauseButton.setPosition(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 60);
        pauseButton.setSize(50, 50);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Pause Button Clicked! Opening StopScene...");

                // Save the current state of ALL game objects
                GameStatePreserver.getInstance().preserveGameState(
                        GameScene.this, gameStateManager, powerUpManager);

                gameStateManager.pauseGame();
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
        initializeGameOverUI();

        // Add UI elements to stage
        stage.addActor(pauseButton);
        stage.addActor(scoreLabel);
        stage.addActor(timerLabel);
        stage.addActor(powerUpLabel);
        stage.addActor(gameOverTable);
    }

    private void initializeGameOverUI() {
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
    }

    private void initializeGame() {
        // Initialize EntityManager with reference to this GameScene for score updates
        entityManager = new GameEntityManager(this);
        collisionManager = new GameCollisionManager(entityManager, this);

        // Reset game state
        gameActive = true;

        // Spawn different entities
        entityManager.spawnPlayers(1, inputManager);
        entityManager.spawnSpikes(4);
        entityManager.spawnBalloonsRow();
    }

    /**
     * Updates the power-up label
     * 
     * @param text The text to display
     */
    public void updatePowerUpLabel(String text) {
        powerUpLabel.setText(text);
    }

    /**
     * Updates the timer label
     * 
     * @param text The text to display
     */
    public void updateTimerLabel(String text) {
        timerLabel.setText(text);
    }

    /**
     * Updates the score label
     * 
     * @param text The text to display
     */
    public void updateScoreLabel(String text) {
        scoreLabel.setText(text);
    }

    /**
     * Adds points to the player's score
     * 
     * @param points The points to add
     */
    public void addScore(int points) {
        gameStateManager.addScore(points, powerUpManager.isDoublePointsActive());
    }

    /**
     * Processes a power-up effect
     * 
     * @param powerUpType The type of power-up
     * @param x           X position for effect
     * @param y           Y position for effect
     */
    public void processPowerUp(int powerUpType, float x, float y) {
        powerUpManager.processPowerUp(powerUpType, x, y);
    }

    /**
     * Extends the game time by the specified amount
     * 
     * @param seconds The number of seconds to add
     */
    public void extendGameTime(float seconds) {
        gameStateManager.extendGameTime(seconds);
    }

    /**
     * Reduces the game time by the specified amount
     * 
     * @param seconds The number of seconds to subtract
     */
    public void reduceGameTime(float seconds) {
        gameStateManager.reduceGameTime(seconds);
    }

    /**
     * Ends the game and shows the game over screen
     */
    private void endGame() {
        gameActive = false;

        // Reset power-ups
        powerUpManager.resetPowerUps();

        // Clear power-up status
        powerUpLabel.setText("");

        // Hide game UI
        pauseButton.setVisible(false);

        // Create a math facts popup
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
     * Shows the game over screen
     * 
     * @param finalScore     The final score
     * @param isNewBestScore Whether this is a new high score
     */
    public void showGameOver(int finalScore, boolean isNewBestScore) {
        finalScoreLabel.setText("Final Score: " + finalScore);
        highScoreLabel.setVisible(isNewBestScore);

        // Add high scores button if it's a new high score
        if (isNewBestScore) {
            // Check if the button already exists to avoid duplicates
            boolean hasViewScoresButton = false;
            for (com.badlogic.gdx.scenes.scene2d.Actor actor : gameOverTable.getChildren()) {
                if (actor instanceof TextButton
                        && ((TextButton) actor).getText().toString().equals("View High Scores")) {
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
                        sceneManager.setScene("highscores");
                    }
                });

                gameOverTable.add(viewScoresButton).size(200, 50).padTop(20).row();
            }
        }
    }

    /**
     * Restarts the game - public so it can be called from StopScene
     */
    public void restartGame() {
        // Reset game state
        gameActive = true;
        gameStateManager.restartGame();

        // Reset power-ups
        powerUpManager.resetPowerUps();

        // Reset UI
        powerUpLabel.setText("");

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

    /**
     * Gets the entity manager
     * 
     * @return The entity manager
     */
    public GameEntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Get the current player score
     * 
     * @return The player's score
     */
    public int getPlayerScore() {
        return gameStateManager.getPlayerScore();
    }

    /**
     * Get the skin for UI elements
     * 
     * @return The UI skin
     */
    public Skin getSkin() {
        return skin;
    }

    // Add a new getter for input manager
    public IInputManager getInputManager() {
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
}