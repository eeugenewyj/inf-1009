package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;
import com.mygdx.game.AbstractIO.IInputManager;

public class Player extends MovableEntity {
    private Texture texture;
    private IInputManager inputManager; // Injected Input Manager

    public Player(float x, float y, float speed, IInputManager inputManager) {
        super(x, y, speed);
        this.texture = new Texture(Gdx.files.internal("player.png"));
        this.inputManager = inputManager; // Inject dependency
    }

    @Override
    public void draw() {

    }

    public void draw(SpriteBatch batch) { // Draw player image
        batch.draw(texture, x, y, width, height);
    }

    @Override
    public void update(float deltaTime) {
        moveUserControlled(deltaTime);
    }

    @Override
    public void moveUserControlled(float deltaTime) {
        float horizontal = inputManager.getMoveX(); // Uses IOManager for movement input
        float vertical = inputManager.getMoveY();

        move(deltaTime, horizontal, vertical); // Reuse movement logic
    }

    @Override
    public void moveAIControlled() {
        // Not used for Player
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, 50, 50); // Adjust width & height if needed
    }

    public void setPosition(float x, float y) { // Fix: Add this method
        this.x = x;
        this.y = y;
    }

    @Override
    public void handleCollision(iCollidable other) {
        if (other instanceof Tree) {
            System.out.println("Player collided with a tree!");
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
