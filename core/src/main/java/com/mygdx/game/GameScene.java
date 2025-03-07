package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractIO.Audio;
import com.mygdx.game.AbstractIO.IInputManager;
import com.mygdx.game.AbstractIO.IOutputManager;
import com.mygdx.game.AbstractScene.ISceneManager;
import com.mygdx.game.AbstractScene.Scene;

public class GameScene extends Scene {
    private Stage stage; // Handles UI elemements
    private Skin skin; // UI skin for button styling
    private Audio audio;
    private ImageButton pauseButton;
    private GameEntityManager entityManager;
    private GameCollisionManager collisionManager;

    public GameScene(ISceneManager sceneManager, IInputManager inputManager, IOutputManager outputManager) {
        super(sceneManager, inputManager, outputManager,"background2.png");

        // Initialize game components
        entityManager = new GameEntityManager();
        collisionManager = new GameCollisionManager(entityManager);

        // Initialize the stage (used for UI elements)
        // Stage automatically handles input events, so you don't have to manually check
        // for clicks
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set stage to process input

        // Load UI Skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Initialize Audio
        audio = Audio.getInstance();
        audio.loadSoundEffect("tree", "Music/tree.mp3");
        audio.loadSoundEffect("player", "Music/collisioneffect.mp3");

        // Load Pause Button
        pauseButton = new ImageButton(new TextureRegionDrawable(new Texture(Gdx.files.internal("pause.png"))));
        pauseButton.setPosition(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 60);
        pauseButton.setSize(50, 50);

        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Pause Button Clicked! Opening StopScene...");
                audio.pauseMusic();
                sceneManager.setScene("stop");
            }
        });

        // Add pause button to stage
        stage.addActor(pauseButton);

        initializeGame();

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
        entityManager.renderEntities(batch); // Delegates drawing to EntityManager

        batch.end();

        // Update player movement using the new input system
        for (Entity entity : entityManager.getEntities()) {
            if (entity instanceof Player) {
                ((Player) entity).moveUserControlled(delta);
            }
        }

        // Update & Move Entities
        entityManager.updateEntities(delta);

        // Collision Detection
        if (!entityManager.getEntities().isEmpty()) {
            collisionManager.detectCollisions();
        }

        // change this method
        if (inputManager.isActionPressed(Input.Keys.ESCAPE)) {
            sceneManager.setScene("stop");
        }
        // Ensure UI Elements (Buttons) Render Last
        stage.act(delta);
        stage.draw(); // Move this to the end
    }

    private void initializeGame() {
        // Initialize EntityManager and Player
        entityManager = new GameEntityManager();
        collisionManager = new GameCollisionManager(entityManager); // Initialize CollisionManager
        // Spawn different entities
        entityManager.spawnPlayers(5, inputManager);
        entityManager.spawnEnemies(5); // Spawn enemies using EntityManager
        entityManager.spawnTrees(5); // Spawn trees using EntityManager
    }

    @Override
    public void dispose() {
        super.dispose();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();  // Fix: Dispose Skin to prevent memory leak
        if (entityManager != null) entityManager.dispose();
        if (collisionManager != null) collisionManager.dispose();
    }
}
