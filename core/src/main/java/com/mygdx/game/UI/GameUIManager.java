package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.graphics.Texture;

/**
 * Manages all UI components for the game scene
 */
public class GameUIManager {
    private Stage stage;
    private Skin skin;
    
    // UI Elements
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
    
    /**
     * Creates a new UI manager
     * 
     * @param stage The stage to add UI elements to
     * @param skin The skin for UI styling
     * @param onPauseAction Action to execute when pause button is pressed
     * @param onRestartAction Action to execute when restart button is pressed
     * @param onHomeAction Action to execute when home button is pressed
     */
    public GameUIManager(Stage stage, Skin skin, Runnable onPauseAction, 
                          Runnable onRestartAction, Runnable onHomeAction) {
        this.stage = stage;
        this.skin = skin;
        
        initializeUI(onPauseAction, onRestartAction, onHomeAction);
    }
    
    /**
     * Initialize all UI elements
     */
    private void initializeUI(Runnable onPauseAction, Runnable onRestartAction, Runnable onHomeAction) {
        // Set up pause button
        pauseButton = new ImageButton(new TextureRegionDrawable(new Texture(Gdx.files.internal("pause.png"))));
        pauseButton.setPosition(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 60);
        pauseButton.setSize(50, 50);
        
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Pause Button Clicked! Opening StopScene...");
                if (onPauseAction != null) {
                    onPauseAction.run();
                }
            }
        });
        
        // Set up score label
        scoreLabel = new Label("Score: 0", skin);
        scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 40);
        scoreLabel.setFontScale(1.5f);
        scoreLabel.setColor(Color.WHITE);
        
        // Set up timer label
        timerLabel = new Label("Time: 20.0", skin);
        timerLabel.setPosition(20, Gdx.graphics.getHeight() - 80);
        timerLabel.setFontScale(1.5f);
        timerLabel.setColor(Color.WHITE);
        
        // Set up power-up status label
        powerUpLabel = new Label("", skin);
        powerUpLabel.setPosition(20, Gdx.graphics.getHeight() - 120);
        powerUpLabel.setFontScale(1.5f);
        powerUpLabel.setColor(Color.YELLOW);
        
        // Create game over elements
        initializeGameOverUI(onRestartAction, onHomeAction);
        
        // Add UI elements to stage
        stage.addActor(pauseButton);
        stage.addActor(scoreLabel);
        stage.addActor(timerLabel);
        stage.addActor(powerUpLabel);
        stage.addActor(gameOverTable);
    }
    
    /**
     * Initialize the game over UI elements
     */
    private void initializeGameOverUI(Runnable onRestartAction, Runnable onHomeAction) {
        gameOverTable = new Table();
        gameOverTable.setFillParent(true);
        gameOverTable.setVisible(false);
        
        // Set up game over elements
        gameOverLabel = new Label("GAME OVER", skin);
        gameOverLabel.setFontScale(2.5f);
        gameOverLabel.setAlignment(Align.center);
        gameOverLabel.setColor(Color.RED);
        
        finalScoreLabel = new Label("Final Score: 0", skin);
        finalScoreLabel.setFontScale(2.0f);
        finalScoreLabel.setAlignment(Align.center);
        finalScoreLabel.setColor(Color.CYAN);
        
        highScoreLabel = new Label("NEW HIGH SCORE!", skin);
        highScoreLabel.setFontScale(1.5f);
        highScoreLabel.setColor(Color.GOLD);
        highScoreLabel.setAlignment(Align.center);
        
        restartButton = new TextButton("Play Again", skin);
        restartButton.getLabel().setColor(Color.WHITE);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onRestartAction != null) {
                    onRestartAction.run();
                }
            }
        });
        
        homeButton = new TextButton("Main Menu", skin);
        homeButton.getLabel().setColor(Color.WHITE);
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onHomeAction != null) {
                    onHomeAction.run();
                }
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
    
    /**
     * Updates the score display
     * 
     * @param score The new score to display
     */
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }
    
    /**
     * Updates the timer display
     * 
     * @param time The time remaining to display
     */
    public void updateTimer(float time) {
        timerLabel.setText(String.format("Time: %.1f", time));
    }
    
    /**
     * Updates the power-up display
     * 
     * @param text The text to display
     */
    public void updatePowerUpLabel(String text) {
        powerUpLabel.setText(text);
    }
    
    /**
     * Shows the game over UI
     * 
     * @param finalScore The final score
     * @param isNewHighScore Whether this is a new high score
     */
    public void showGameOver(int finalScore, boolean isNewHighScore) {
        finalScoreLabel.setText("Final Score: " + finalScore);
        highScoreLabel.setVisible(isNewHighScore);
        gameOverTable.setVisible(true);
        pauseButton.setVisible(false);
        
        // Add high scores button if it's a new high score
        if (isNewHighScore) {
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
                        // This will be handled in the GameScene class
                        if (onHighScoresAction != null) {
                            onHighScoresAction.run();
                        }
                    }
                });

                gameOverTable.add(viewScoresButton).size(200, 50).padTop(20).row();
            }
        }
    }
    
    /**
     * Hides the game over UI
     */
    public void hideGameOver() {
        gameOverTable.setVisible(false);
        pauseButton.setVisible(true);
    }
    
    /**
     * Sets the action to perform when view high scores button is clicked
     */
    private Runnable onHighScoresAction;
    
    public void setOnHighScoresAction(Runnable action) {
        this.onHighScoresAction = action;
    }
    
    /**
     * Shows a math facts popup
     * 
     * @param onCloseAction Action to execute when popup is closed
     */
    public void showMathFactsPopup(Runnable onCloseAction) {
        MathFactsPopup popup = new MathFactsPopup(skin, onCloseAction);
        popup.refreshFact();
        popup.show(stage);
    }
    
    /**
     * Disposes of resources
     */
    public void dispose() {
        // Note: We don't dispose stage or skin here as they are managed by GameScene
    }
}