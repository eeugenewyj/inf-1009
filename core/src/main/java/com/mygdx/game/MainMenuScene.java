package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.ISceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class MainMenuScene extends Scene {
    private final Stage stage;
    private final Skin skin;
    private final TextButton startButton;
    private final TextButton settingsButton;
    private final TextButton exitButton;
    private boolean isMuted = false;
    private ImageButton muteButton;

    public MainMenuScene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");

        // Fix: Use ScreenViewport for better UI scaling
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Fix: Properly initialize UI Skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Use centralized background music
        sceneManager.playBackgroundMusic();

        // Fix: Create buttons using the skin
        startButton = new TextButton("Start Game", skin);
        settingsButton = new TextButton("Settings", skin);
        exitButton = new TextButton("Exit", skin);

        // Fix: Set button positions
        float centerX = Gdx.graphics.getWidth() / 2f - 75;
        float centerY = Gdx.graphics.getHeight() / 2f;

        startButton.setPosition(centerX, centerY + 50);
        settingsButton.setPosition(centerX, centerY);
        exitButton.setPosition(centerX, centerY - 50);

        startButton.setSize(150, 50);
        settingsButton.setSize(150, 50);
        exitButton.setSize(150, 50);

        // Fix: Add button listeners for scene switching
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Start Button Clicked! Switching to game scene...");
                sceneManager.setScene("play");
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

