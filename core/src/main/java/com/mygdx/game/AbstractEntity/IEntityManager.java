package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Spikes;

import java.util.List;

public interface iEntityManager {
    // Existing methods
    void addEntity(Entity entity);
    void updateEntities(float deltaTime);
    void renderEntities(SpriteBatch batch);
    List<Entity> getEntities();
    void dispose();
    
    // New methods required by Player
    boolean wouldCollideWithSpikes(float x, float y, float width, float height);
}