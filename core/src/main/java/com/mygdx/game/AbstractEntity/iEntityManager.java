package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public interface iEntityManager {
    List<Entity> getEntities();

    // Existing methods
    void addEntity(Entity entity);

    void updateEntities(float deltaTime);

    void renderEntities(SpriteBatch batch);

    // New methods required by Player
    boolean wouldCollideWithSpikes(float x, float y, float width, float height);

    void dispose();

}
