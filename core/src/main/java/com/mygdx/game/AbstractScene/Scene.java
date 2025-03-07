package com.mygdx.game.AbstractScene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;

public abstract class Scene implements Screen {
    protected final ISceneManager sceneManager;
    protected final IInputManager inputManager;
    protected final IOutputManager outputManager;
    protected final SpriteBatch batch;
    protected final Texture backgroundTexture;

    public Scene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager, String texturePath) {
        this.sceneManager = sceneManager;
        this.inputManager = inputManager;    // Assign input manager
        this.outputManager = outputManager;  // Assign output manager
        this.batch = new SpriteBatch();
        this.backgroundTexture = new Texture(Gdx.files.internal(texturePath));
    }

    @Override
    public void render(float delta) {
        if (batch != null) {
            batch.begin();
            batch.draw(backgroundTexture, 0, 0);
            batch.end();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
    }

    // Other Screen interface methods (optional to override)
    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
}

