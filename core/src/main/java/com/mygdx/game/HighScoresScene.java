package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
    private Table mainTable;
    private Label titleLabel;
    
    // Easy mode elements
    private Label easyTitleLabel;
    private Label[] easyScoreLabels;
    
    // Hard mode elements
    private Label hardTitleLabel;
    private Label[] hardScoreLabels;
    
    private TextButton backButton;
    
    private static final int MAX_SCORES = 5;

    public HighScoresScene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager, "background2.png");
        
        // Set up the stage
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        // Create a table for layout
        mainTable = new Table();
        mainTable.setFillParent(true);
        
        // Create title
        titleLabel = new Label("HIGH SCORES", skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setAlignment(Align.center);
        titleLabel.setColor(Color.GOLD);
        
        // Create mode title labels
        easyTitleLabel = new Label("EASY MODE", skin);
        easyTitleLabel.setFontScale(1.5f);
        easyTitleLabel.setAlignment(Align.center);
        easyTitleLabel.setColor(Color.CYAN);
        
        hardTitleLabel = new Label("HARD MODE", skin);
        hardTitleLabel.setFontScale(1.5f);
        hardTitleLabel.setAlignment(Align.center);
        hardTitleLabel.setColor(Color.ORANGE);
        
        // Create score labels arrays
        easyScoreLabels = new Label[MAX_SCORES];
        hardScoreLabels = new Label[MAX_SCORES];
        
        // Initialize score labels with larger font and brighter colors
        for (int i = 0; i < MAX_SCORES; i++) {
            easyScoreLabels[i] = new Label("", skin);
            easyScoreLabels[i].setFontScale(1.6f); // Larger font
            easyScoreLabels[i].setAlignment(Align.center);
            easyScoreLabels[i].setColor(Color.CYAN); // Cyan color to match the "EASY MODE" header
            
            hardScoreLabels[i] = new Label("", skin);
            hardScoreLabels[i].setFontScale(1.6f); // Larger font
            hardScoreLabels[i].setAlignment(Align.center);
            hardScoreLabels[i].setColor(Color.ORANGE); // Orange color to match the "HARD MODE" header
        }
        
        // Create back button
        backButton = new TextButton("Back", skin);
        backButton.getLabel().setColor(Color.WHITE);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.setScene("home");
            }
        });
        
        // Create a table for two columns
        Table scoreTable = new Table();
        
        // Add mode titles to score table
        scoreTable.add(easyTitleLabel).padRight(50);
        scoreTable.add(hardTitleLabel).padLeft(50).row();
        
        // Add score labels to table in two columns
        for (int i = 0; i < MAX_SCORES; i++) {
            scoreTable.add(easyScoreLabels[i]).padRight(50).padTop(15).align(Align.center);
            scoreTable.add(hardScoreLabels[i]).padLeft(50).padTop(15).align(Align.center).row();
        }
        
        // Add elements to main table
        mainTable.add(titleLabel).colspan(2).pad(30).row();
        mainTable.add(scoreTable).colspan(2).pad(20).row();
        mainTable.add(backButton).colspan(2).size(150, 50).pad(30).row();
        
        // Add table to stage
        stage.addActor(mainTable);
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        // Get high scores from manager
        HighScoresManager highScoresManager = HighScoresManager.getInstance();
        
        // Explicitly get both score lists
        List<Integer> easyScores = highScoresManager.getEasyHighScores();
        List<Integer> hardScores = highScoresManager.getHardHighScores();
        
        System.out.println("Easy scores: " + easyScores);
        System.out.println("Hard scores: " + hardScores);
        
        // Clear all labels first
        for (int i = 0; i < MAX_SCORES; i++) {
            easyScoreLabels[i].setText("");
            hardScoreLabels[i].setText("");
        }
        
        // Fill in available easy scores
        for (int i = 0; i < easyScores.size(); i++) {
            easyScoreLabels[i].setText((i + 1) + ". " + easyScores.get(i));
            System.out.println("Setting easy score " + i + " to: " + easyScores.get(i));
        }
        
        // Fill in available hard scores
        for (int i = 0; i < hardScores.size(); i++) {
            hardScoreLabels[i].setText((i + 1) + ". " + hardScores.get(i));
            System.out.println("Setting hard score " + i + " to: " + hardScores.get(i));
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