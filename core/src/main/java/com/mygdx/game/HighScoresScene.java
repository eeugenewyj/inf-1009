package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.ISceneManager;
import com.mygdx.game.AbstractScene.Scene;

import java.util.List;

public class HighScoresScene extends Scene {
    private Stage stage;
    private Skin skin;
    private Table table;
    private Label titleLabel;
    private Label[] scoreLabels;
    private TextButton backButton;
    
    private static final int MAX_SCORES = 5;

    public HighScoresScene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");
        
        // Set up the stage
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        // Create a table for layout
        table = new Table();
        table.setFillParent(true);
        
        // Create title with improved visibility
        titleLabel = new Label("HIGH SCORES", skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setAlignment(Align.center);
        titleLabel.setColor(Color.YELLOW); // Bright yellow for the title
        
        // Create score labels with improved visibility
        scoreLabels = new Label[MAX_SCORES];
        for (int i = 0; i < MAX_SCORES; i++) {
            scoreLabels[i] = new Label("", skin);
            scoreLabels[i].setFontScale(1.5f);
            scoreLabels[i].setAlignment(Align.center);
            scoreLabels[i].setColor(Color.WHITE); // White for better visibility
        }
        
        // Create back button
        backButton = new TextButton("Back", skin);
        backButton.getLabel().setColor(Color.WHITE); // Ensure button text is white
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene("home");
            }
        });
        
        // Add elements to the table
        table.add(titleLabel).pad(50).row();
        
        // Add score labels
        for (int i = 0; i < MAX_SCORES; i++) {
            table.add(scoreLabels[i]).pad(10).row();
        }
        
        // Add back button
        table.add(backButton).pad(30).width(150).height(50).row();
        
        // Add table to stage
        stage.addActor(table);
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        // Update score labels with current high scores
        List<Integer> highScores = HighScoresManager.getInstance().getHighScores();
        
        // Clear all labels first
        for (int i = 0; i < MAX_SCORES; i++) {
            scoreLabels[i].setText("");
        }
        
        // Fill in available scores
        for (int i = 0; i < highScores.size(); i++) {
            scoreLabels[i].setText((i + 1) + ". " + highScores.get(i));
        }
    }
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        
        // Draw background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        
        // Update and draw stage
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