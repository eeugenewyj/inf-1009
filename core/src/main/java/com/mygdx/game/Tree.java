package com.mygdx.game;

import com.mygdx.game.AbstractEntity.StaticObject;
import com.mygdx.game.AbstractEntity.iCollidable;

public class Tree extends StaticObject {
    private float lifetime;
    private boolean isTemporary = false;

    public Tree(float x, float y) {
        super(x, y, 50, 50, "tree.png"); // Tree has a default size and texture
        this.lifetime = -1; // Default: tree does not expire unless set
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

    @Override
    public void handleCollision(iCollidable other) {
        // Trees don't need to print anything since other entities handle the printing
        // This matches the original behavior where trees didn't print messages
    }
}
