package com.mygdx.game;

import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Enemy extends MovableEntity {
    private Texture texture;
    private final Random random = new Random();

    public Enemy(float x, float y, float speed) {
        super(x, y, speed);
        this.texture = new Texture(Gdx.files.internal("enemy.png"));

        // Start moving in a random direction
        directionX = random.nextBoolean() ? 1 : -1;
        directionY = random.nextBoolean() ? 1 : -1;
    }

    @Override
    public void draw() {
        // Required override, but not used
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    @Override
    public void update(float deltaTime) {
        moveAIControlled();
    }

    @Override
    public void moveUserControlled(float deltaTime) {
        // Enemy should not be controlled by the player, so leave this empty
    }

    @Override
    public void moveAIControlled() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        move(deltaTime, directionX, directionY); // Use common movement logic
    }

    public void bounce(Entity other) {
        this.x = this.previousX;
        this.y = this.previousY;

        if (Math.abs(this.getPreviousX() - other.getX()) < Math.abs(this.getPreviousY() - other.getY())) {
            this.reverseYDirection();
        } else {
            this.reverseXDirection();
        }
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void handleCollision(iCollidable other) {
        if (other instanceof Player) {
           System.out.println("Enemy collided with a player!");
        } else if (other instanceof Tree) {
            System.out.println("Enemy collided with a tree!");
        }
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}

