package com.mygdx.game.AbstractScene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;

public abstract class Scene implements Screen {
    // Fixed dimensions for the game - this should match your window size
    protected static final float WORLD_WIDTH = 800;
    protected static final float WORLD_HEIGHT = 600;

    // References
    protected final ISceneManager sceneManager;
    protected final IInputManager inputManager;
    protected final IOutputManager outputManager;
    // SpriteBatch for rendering 2D graphics
    protected final SpriteBatch batch;
    // Background texture for the scene
    protected final Texture backgroundTexture;
    // Stage for managing UI elements and actors
    protected Stage stage;

    // Constructor to initialise the scene with manages and a background texture
    public Scene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager,
            String texturePath) {
        this.sceneManager = sceneManager;
        this.inputManager = inputManager;
        this.outputManager = outputManager;
        this.batch = new SpriteBatch();
        this.backgroundTexture = new Texture(Gdx.files.internal(texturePath));

        // Create a stage with a FitViewport that maintains the aspect ratio
        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
    }

    @Override
    public void render(float delta) {
        if (batch != null) {
            batch.begin();
            // Draw background to fit the viewport size
            batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            batch.end();
        }

        // Update and draw the stage
        if (stage != null) {
            stage.act(delta); // Update the stage with the delta time
            stage.draw(); // Render the stage
        }
    }

    // Method to handle window resizing
    @Override
    public void resize(int width, int height) {
        // Update the viewport to handle resizing
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void show() {
        if (stage != null) {
            Gdx.input.setInputProcessor(stage); // Set the stage as the input processor
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (stage != null) {
            stage.dispose();
        }
    }
}