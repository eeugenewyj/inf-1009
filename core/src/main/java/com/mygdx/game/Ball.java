package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;

import java.util.Random;

public class Ball extends MovableEntity {
    private int value;
    private boolean collected = false;
    private boolean isFalling = false;

    private static final int NUM_BALLS = 8;
    private static final float GAP_RATIO = 0.1f;
    private static final float SCREEN_WIDTH = Gdx.graphics.getWidth();
    private static final float BALL_WIDTH = SCREEN_WIDTH / (NUM_BALLS + (NUM_BALLS - 1) * GAP_RATIO);
    private static final float BALL_RADIUS = BALL_WIDTH / 2;

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    public Ball(float x, float y) {
        super(x, y, 100); // Falling speed
        this.value = new Random().nextInt(9) + 1; // Random number 1-9
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
    }

    public int getValue() {
        return value;
    }

    public void moveAIControlled() {
        float delta = Gdx.graphics.getDeltaTime();
        y -= speed * delta;
    }

    @Override
    public void update(float deltaTime) {
        moveAIControlled();
    }

    @Override
    public void draw() {
        // This method can be empty if you are only using `draw(SpriteBatch batch)`
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.end(); // End the sprite batch before using ShapeRenderer

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(x + BALL_RADIUS, y + BALL_RADIUS, BALL_RADIUS);
        shapeRenderer.end();

        batch.begin(); // Restart batch for font rendering

        font.draw(batch, String.valueOf(value), x + BALL_RADIUS - 10, y + BALL_RADIUS + 10); // Center number
    }

    @Override
    public void handleCollision(iCollidable other) {
        if (other instanceof Player) {
            collected = true;
            setActive(false);
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }

    public static float getBallRadius() {
        return BALL_RADIUS;
    }

    public static float getBallWidth() {
        return BALL_WIDTH;
    }
}
