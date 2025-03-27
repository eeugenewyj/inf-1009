package com.mygdx.game.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.AbstractIO.Audio;
import com.mygdx.game.AbstractIO.iInputManager;
import com.mygdx.game.AbstractIO.iOutputManager;
import com.mygdx.game.AbstractScene.iSceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class StopScene extends Scene {
    private Stage stage;
    private Skin skin;
    private TextButton resumeButton;
    private TextButton restartButton;
    private TextButton homeButton;
    private Audio audio;

    // Use instance variable instead of static variable
    private boolean restartFlag = false;

    public StopScene(iSceneManager sceneManager, iInputManager inputManager, iOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");

        // Initialize stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load UI Skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        audio = Audio.getInstance(); // Get the singleton instance of Audio

        // Create buttons using the skin
        resumeButton = new TextButton("Resume Game", skin);
        restartButton = new TextButton("Restart Game", skin);
        homeButton = new TextButton("Home", skin);

        // Set button positions
        float centerX = Gdx.graphics.getWidth() / 2f - 75;
        float centerY = Gdx.graphics.getHeight() / 2f;

        resumeButton.setPosition(centerX, centerY + 50);
        restartButton.setPosition(centerX, centerY);
        homeButton.setPosition(centerX, centerY - 50);

        resumeButton.setSize(150, 50);
        restartButton.setSize(150, 50);
        homeButton.setSize(150, 50);

        // Add button listeners for scene switching
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Resume Button Clicked! Returning to game...");
                restartFlag = false; // Explicitly ensure we're not restarting
                audio.resumeMusic(); // Resume the music
                sceneManager.setScene("play"); // Switch back to the game scene
            }
        });

        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Restart Game Button Clicked! Setting restart flag...");
                restartFlag = true; // Set flag to restart on next show
                audio.stopMusic(); // Stop the current music
                audio.playMusic(); // Restart the music
                sceneManager.setScene("play"); // Switch to play scene directly
            }
        });

        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Back to Main Menu Button Clicked! Returning to Main Menu...");
                audio.stopMusic(); // Stop the current music
                audio.playMusic(); // Restart the music
                sceneManager.setScene("home"); // Switch back to the home scene directly
            }
        });

        // Add button to the stage
        stage.addActor(resumeButton);
        stage.addActor(restartButton);
        stage.addActor(homeButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        System.out.println("Stop Scene shown");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    // Instance method to check restart flag instead of static method
    public boolean shouldRestartGame() {
        boolean shouldRestart = restartFlag;
        if (restartFlag) {
            restartFlag = false; // Reset flag after it's checked
        }
        return shouldRestart;
    }

    // Instance method to set restart flag instead of static method
    public void setRestartFlag(boolean value) {
        this.restartFlag = value;
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        skin.dispose();
    }
}