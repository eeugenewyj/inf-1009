package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public interface IEntityManager {
    // Abstract method to add an entity to the entity manager
    void addEntity(Entity entity);

    // Abstract method to update all entities
    void updateEntities(float deltaTime);

    // Abstract method to render all entities using a SpriteBatch
    void renderEntities(SpriteBatch batch);

    // Abstract method to retrieve a list of all entities
    List<Entity> getEntities();

    void dispose();
}