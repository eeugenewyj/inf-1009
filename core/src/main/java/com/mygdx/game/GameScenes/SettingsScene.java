package com.mygdx.game.GameScenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.AbstractIO.iInputManager;
import com.mygdx.game.AbstractIO.iOutputManager;
import com.mygdx.game.AbstractScene.iSceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class SettingsScene extends Scene {
    private float lastVolume = 0.5f; // Stores the last volume before muting

    private Stage stage;
    private Skin skin;
    private Slider volumeSlider;
    private TextButton backButton;
    private TextButton muteButton;
    private Texture muteTexture, unmuteTexture;
    private boolean isMuted;
    private Table table;
    private float prevVolume = -1f; // Stores the last printed volume

    public SettingsScene(iSceneManager sceneManager, iInputManager inputManager, iOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");

        // Fix: Use ScreenViewport for better UI scaling
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Load mute state
        isMuted = outputManager.isMuted();

        // Initialize UI components
        volumeSlider(sceneManager);
        muteButton(sceneManager);
        backButton(sceneManager);

        // Create a table to organize UI elements
        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Add UI elements to the table
        table.add(volumeSlider).padBottom(20).row();
        table.add(muteButton).padBottom(20).row();
        table.add(backButton).padBottom(20).row();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        System.out.println("Settings menu shown, input processor set");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        stage.act(delta);
        stage.draw();
    }

    private void volumeSlider(iSceneManager sceneManager) {
        volumeSlider = new Slider(0f, 1f, 0.05f, false, skin); // Create slider

        // Get the current volume from SceneManager instead of IOManager
        lastVolume = sceneManager.getBackgroundMusicVolume();
        isMuted = (lastVolume == 0f);

        volumeSlider.setValue(lastVolume); // Set slider to the actual game volume

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                float volume = volumeSlider.getValue();

                if (volume == 0) {
                    isMuted = true;
                } else {
                    isMuted = false;
                    lastVolume = volume; // Store last non-zero volume
                }

                // Update volume in OutputManager
                outputManager.setVolume(volume);
                outputManager.setMuted(isMuted);
                sceneManager.setBackgroundMusicVolume(volume); // Update SceneManager's volume
                updateMuteButton();

                // Print volume change only if it's different from previous volume
                if (volume != prevVolume) {
                    System.out.println("Volume changed to: " + volume);
                    prevVolume = volume; // Update the previous volume value
                }
            }
        });
    }

    private void muteButton(iSceneManager sceneManager) {
        muteButton = new TextButton(isMuted ? "Unmute" : "Mute", skin); // Set initial button text

        muteButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isMuted = !isMuted; // Toggle mute state

                if (isMuted) {
                    // Save last volume before muting
                    lastVolume = volumeSlider.getValue() > 0 ? volumeSlider.getValue() : lastVolume;
                    outputManager.setVolume(0f);
                } else {
                    outputManager.setVolume(lastVolume); // Restore last volume
                }

                // Update mute state
                outputManager.setMuted(isMuted);
                sceneManager.setBackgroundMusicVolume(isMuted ? 0f : lastVolume);
                volumeSlider.setValue(isMuted ? 0f : lastVolume);
                updateMuteButton();
            }
        });
    }

    private void updateMuteButton() {
        muteButton.setText(isMuted ? "Unmute" : "Mute");
    }

    private void backButton(iSceneManager sceneManager) {
        backButton = new TextButton("Back", skin);

        backButton.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Returning to Main Menu...");
                // Fix: Use the SceneType enum for type safety
                sceneManager.setScene(SceneType.HOME);
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        skin.dispose();
        if (muteTexture != null)
            muteTexture.dispose();
        if (unmuteTexture != null)
            unmuteTexture.dispose();
    }
}