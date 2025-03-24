package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.AbstractIO.Audio;
import com.mygdx.game.AbstractScene.ISceneManager;

/**
 * Manages the game's UI elements
 */
public class GameUIManager {
    private Stage stage;
    private Skin skin;
    private ISceneManager sceneManager;
    
    // Game UI elements
    private ImageButton pauseButton;
    private Label scoreLabel;
    private Label timerLabel;
    private Label powerUpLabel;
    
    // Game over elements
    private Table gameOverTable;
    private Label gameOverLabel;
    private Label finalScoreLabel;
    private Label highScoreLabel;
    private TextButton restartButton;
    private TextButton homeButton;
    
    private Audio audio;
    
    // Resources to dispose
    private Texture pauseTexture;

    public GameUIManager(Stage stage, Skin skin, ISceneManager sceneManager) {
        this.stage = stage;
        this.skin = skin;
        this.sceneManager = sceneManager;
        this.audio = Audio.getInstance();
        
        initializeUI();
    }

    /**
     * Initializes all UI elements
     */
    private void initializeUI() {
        // Create pause button
        pauseTexture = new Texture(Gdx.files.internal("pause.png"));
        // Use our helper class to create the drawable
        TextureRegionDrawable pauseDrawable = TextureRegionHelper.createDrawable(pauseTexture);
        pauseButton = new ImageButton(pauseDrawable);
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
        
        // Create score label
        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 40);
        scoreLabel.setFontScale(1.5f);
        scoreLabel.setColor(Color.WHITE); // White for better visibility
        
        // Create timer label
        timerLabel = new Label("Time: 20.0", skin);
        timerLabel.setPosition(20, Gdx.graphics.getHeight() - 80);
        timerLabel.setFontScale(1.5f);
        timerLabel.setColor(Color.WHITE); // White for better visibility
        
        // Create power-up status label
        powerUpLabel = new Label("", skin);
        powerUpLabel.setPosition(20, Gdx.graphics.getHeight() - 120);
        powerUpLabel.setFontScale(1.5f);
        powerUpLabel.setColor(Color.YELLOW); // Yellow for better visibility
        
        // Create game over table for centered layout
        gameOverTable = new Table();
        gameOverTable.setFillParent(true);
        gameOverTable.setVisible(false);
        
        // Create game over elements
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
        
        homeButton = new TextButton("Main Menu", skin);
        homeButton.getLabel().setColor(Color.WHITE); // Ensure text is white
        
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
    }

    /**
     * Sets up restart button click handler
     * @param restartAction Action to perform on restart
     */
    public void setRestartAction(Runnable restartAction) {
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartAction.run();
            }
        });
    }

    /**
     * Sets up home button click handler
     */
    public void setHomeAction() {
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene("home");
            }
        });
    }

    /**
     * Shows the game over UI
     * @param score Final score
     * @param isNewHighScore Whether this is a new high score
     */
    public void showGameOver(int score, boolean isNewHighScore) {
        // Hide game UI
        pauseButton.setVisible(false);
        
        // Update score display
        finalScoreLabel.setText("Final Score: " + score);
        highScoreLabel.setVisible(isNewHighScore);
        
        // If it's a new high score, add another button to go directly to high scores
        if (isNewHighScore) {
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
                        sceneManager.setScene("highscores");
                    }
                });
                
                // Add to the game over table after the other buttons
                gameOverTable.add(viewScoresButton).size(200, 50).padTop(20).row();
            }
        }
        
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
     * Hides the game over UI
     */
    public void hideGameOver() {
        gameOverTable.setVisible(false);
        highScoreLabel.setVisible(false);
        pauseButton.setVisible(true);
    }

    /**
     * Gets the score label
     */
    public Label getScoreLabel() {
        return scoreLabel;
    }

    /**
     * Gets the timer label
     */
    public Label getTimerLabel() {
        return timerLabel;
    }

    /**
     * Gets the power-up label
     */
    public Label getPowerUpLabel() {
        return powerUpLabel;
    }
    
    /**
     * Disposes of resources used by this manager
     */
    public void dispose() {
        if (pauseTexture != null) {
            pauseTexture.dispose();
        }
    }
}