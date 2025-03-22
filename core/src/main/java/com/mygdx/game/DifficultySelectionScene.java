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

import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.ISceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class DifficultySelectionScene extends Scene {
    private Stage stage;
    private Skin skin;
    private Label titleLabel;
    private TextButton easyButton;
    private TextButton hardButton;
    private TextButton backButton;
    private Table table;

    public DifficultySelectionScene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");

        // Set up stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        // Load skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        // Create table for layout
        table = new Table();
        table.setFillParent(true);
        
        // Create title label
        titleLabel = new Label("SELECT DIFFICULTY", skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setAlignment(Align.center);
        titleLabel.setColor(Color.GOLD);
        
        // Create buttons for difficulty selection
        easyButton = new TextButton("EASY", skin);
        easyButton.getLabel().setFontScale(1.5f);
        
        hardButton = new TextButton("HARD", skin);
        hardButton.getLabel().setFontScale(1.5f);
        
        backButton = new TextButton("Back", skin);
        
        // Add click listeners
        easyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameSettings.setDifficulty(GameSettings.DIFFICULTY_EASY);
                StopScene.setRestartFlag(true);
                sceneManager.setScene("play");
            }
        });
        
        hardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
        table.add(titleLabel).padBottom(50).row();
        table.add(easyButton).size(200, 60).padBottom(20).row();
        table.add(hardButton).size(200, 60).padBottom(40).row();
        table.add(backButton).size(150, 50).row();
        
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
        
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        
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