package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntityManager implements IEntityManager {
    // Protected list to store all entities
    protected List<Entity> entities = new ArrayList<>();

    // Method to update all entities
    public void updateEntities(float deltaTime) {
        for (Entity entity : new ArrayList<>(entities)) {
            if (entity.isActive()) {
                entity.update(deltaTime);
            } else {
                removeEntity(entity);
            }
        }
    }

    // Method to render all entities using a SpriteBatch
    public final void renderEntities(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.draw(batch);
        }
    }

    // Method to add an entity to the list of entities
    public final void addEntity(Entity entity) {
        // Ensure the entity is not already in the list
        if (!entities.contains(entity)) {
            entities.add(entity);
        }
    }

    // Method to remove an entity from the list
    public final void removeEntity(Entity entity) {
        entity.dispose();
        entities.remove(entity);
    }

    // Method to get a copy of the list of entities
    public final List<Entity> getEntities() {
        return new ArrayList<>(entities); // Return a copy to prevent modification
    }

    public abstract void dispose();
}