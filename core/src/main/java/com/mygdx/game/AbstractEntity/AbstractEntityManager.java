package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntityManager implements IEntityManager {
    protected List<Entity> entities = new ArrayList<>();

    public void updateEntities(float deltaTime) {
        for (Entity entity : new ArrayList<>(entities)) {
            if (entity.isActive()) {
                entity.update(deltaTime);
            } else {
                removeEntity(entity);
            }
        }
    }

    public final void renderEntities(SpriteBatch batch) {
        for (Entity entity : entities) {
            entity.draw(batch);
        }
    }

    public final void addEntity(Entity entity) {
        if (!entities.contains(entity)) {
            entities.add(entity);
        }
    }

    public final void removeEntity(Entity entity) {
        entity.dispose();
        entities.remove(entity);
    }

    public final List<Entity> getEntities() {
        return new ArrayList<>(entities); // Return a copy to prevent modification
    }

    public abstract void dispose();
}