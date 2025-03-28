package com.mygdx.game.GameEntity;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.AbstractEntity.StaticObject;
import com.mygdx.game.AbstractEntity.iCollidable;

public class Spikes extends StaticObject {
    private float lifetime;
    private boolean isTemporary = false;

    public Spikes(float x, float y) {
        super(x, y, 50, 50, "spike.png"); // Spikes has a default size and texture
        this.lifetime = -1; // Default: spikes does not expire unless set
    }

    @Override
    public void handleCollision(iCollidable other) {
        // Spikes don't need to print anything since other entities handle the printing
        // This matches the original behavior where spikes didn't print messages
    }

    @Override
    public Rectangle getBoundingBox() {
        // Return a slightly smaller bounding box for better collision feel
        // This creates a 5-pixel "buffer" around the spikes
        return new Rectangle(getX() + 5, getY() + 5, getWidth() - 10, getHeight() - 10);
    }

    public void setLifetime(float time) {
        this.lifetime = time;
        this.isTemporary = true;
    }

    public void updateLifeTime(float deltaTime) {
        if (isTemporary && lifetime > 0) {
            lifetime -= deltaTime;
        }
    }

    public boolean isExpired() {
        return isTemporary && lifetime <= 0;
    }

    public float getLifeTime() {
        return lifetime;
    }

}