package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.ISceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class MainMenuScene extends Scene {
    private final Stage stage;
    private final Skin skin;
    private final TextButton startButton;
    private final TextButton highScoresButton; // New high scores button
    private final TextButton settingsButton;
    private final TextButton exitButton;
    private boolean isMuted = false;
    private ImageButton muteButton;
    private Label titleLabel;
    private Label subtitleLabel;
    private Table titleTable; // For layout

    public MainMenuScene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");

        // Fix: Use ScreenViewport for better UI scaling
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Fix: Properly initialize UI Skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create a table for the title layout to ensure proper positioning
        titleTable = new Table();
        titleTable.setFillParent(true); // Make it fill the stage

        // Create title label with large font and attractive style
        titleLabel = new Label("NUMBERS RUSH", skin);
        titleLabel.setFontScale(3.5f); // Make it prominent
        titleLabel.setColor(Color.GOLD); // Gold color for attractive look

        // Optional: Create subtitle label with additional info
        subtitleLabel = new Label("Collect balls to score points!", skin);
        subtitleLabel.setFontScale(1.5f); // Increased font size for better visibility
        subtitleLabel.setColor(Color.CYAN); // Bright cyan color for high contrast

        // Add labels to the table with proper spacing
        titleTable.top().padTop(50); // Position at top with padding
        titleTable.add(titleLabel).padBottom(15).row(); // Add main title with spacing below
        titleTable.add(subtitleLabel).row(); // Add subtitle below main title

        // Add the table to the stage before adding buttons
        stage.addActor(titleTable);

        // Use centralized background music
        sceneManager.playBackgroundMusic();

        // Fix: Create buttons using the skin
        startButton = new TextButton("Start Game", skin);
        highScoresButton = new TextButton("High Scores", skin); // New high scores button
        settingsButton = new TextButton("Settings", skin);
        exitButton = new TextButton("Exit", skin);

        // Fix: Set button positions with lower placement to avoid subtitle overlap
        float centerX = Gdx.graphics.getWidth() / 2f - 75;
        float centerY = Gdx.graphics.getHeight() / 2f - 50; // Move buttons 50 pixels lower

        startButton.setPosition(centerX, centerY + 75);
        highScoresButton.setPosition(centerX, centerY + 25); // Position between Start and Settings
        settingsButton.setPosition(centerX, centerY - 25);
        exitButton.setPosition(centerX, centerY - 75);

        startButton.setSize(150, 50);
        highScoresButton.setSize(150, 50);
        settingsButton.setSize(150, 50);
        exitButton.setSize(150, 50);

        // Fix: Add button listeners for scene switching
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Start Button Clicked! Opening difficulty selection...");
                sceneManager.setScene("difficulty"); // Show difficulty selection instead of going directly to play
            }
        });

        highScoresButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("High Scores Button Clicked! Opening high scores...");
                sceneManager.setScene("highscores");
            }
        });

        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Opening Settings...");
                sceneManager.setScene("settings"); // Go to settings scene
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Exit Button Clicked! Exiting Game...");
                Gdx.app.exit();
            }
        });

        // Fix: Add buttons to the stage
        stage.addActor(startButton);
        stage.addActor(highScoresButton); // Add the high scores button
        stage.addActor(settingsButton);
        stage.addActor(exitButton);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);  // Fix: Ensure input processor is reset when scene is shown
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();  // Fix: Properly dispose of UI skin
    }
}