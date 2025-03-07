package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public interface IEntityManager {
    void addEntity(Entity entity);
    void updateEntities(float deltaTime);
    void renderEntities(SpriteBatch batch);
    List<Entity> getEntities();
    void dispose();
}
