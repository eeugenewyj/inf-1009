package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

import com.mygdx.game.AbstractIO.iInputManager;
import com.mygdx.game.AbstractIO.iOutputManager;
import com.mygdx.game.AbstractScene.iSceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class DifficultySelectionScene extends Scene {
    private Stage stage; // Stage for managing UI elements
    private Skin skin; // Skin for styling UI components
    private Label titleLabel; // Label for the scene title
    private TextButton easyButton; // Button for selecting easy difficulty
    private TextButton hardButton; // Button for selecting hard difficulty
    private TextButton backButton; // Button for returning to the previous scene
    private Table table; // Table for organising UI components

    public DifficultySelectionScene(iSceneManager sceneManager, iInputManager inputManager,
            iOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");

        // Set up stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set the stage as the input processor

        // Load skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Create table for layout
        table = new Table();
        table.setFillParent(true); // Table fills the entire stage

        // Create title label
        titleLabel = new Label("SELECT DIFFICULTY", skin);
        titleLabel.setFontScale(2.0f); // Scale the font size
        titleLabel.setAlignment(Align.center); // Center-align the text
        titleLabel.setColor(Color.GOLD); // Set the text color to gold

        // Create buttons for difficulty selection
        easyButton = new TextButton("EASY", skin);
        easyButton.getLabel().setFontScale(1.5f); // Scale the font size for the button label

        hardButton = new TextButton("HARD", skin);
        hardButton.getLabel().setFontScale(1.5f); // Scale the font size for the button label

        backButton = new TextButton("Back", skin); // Button for going back

        // Add click listeners
        easyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Set the difficulty to easy and switch to the play scene
                GameSettings.setDifficulty(GameSettings.DIFFICULTY_EASY);
                StopScene.setRestartFlag(true);
                sceneManager.setScene("play");
            }
        });

        hardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Set the difficulty to hard and switch to the play scene
                GameSettings.setDifficulty(GameSettings.DIFFICULTY_HARD);
                StopScene.setRestartFlag(true);
                sceneManager.setScene("play");
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene("home");
            }
        });

        // Add elements to table with spacing
        table.add(titleLabel).padBottom(50).row(); // Add the title label with padding
        table.add(easyButton).size(200, 60).padBottom(20).row(); // Add the easy button with size and padding
        table.add(hardButton).size(200, 60).padBottom(40).row(); // Add the hard button with size and padding
        table.add(backButton).size(150, 50).row(); // Add the back button with size

        // Add table to stage
        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Draw the background texture
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        skin.dispose();
    }
}