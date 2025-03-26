package com.mygdx.game.AbstractCollision;

import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.IEntityManager;

public abstract class AbstractCollisionManager implements ICollisionManager {
    protected final IEntityManager entityManager;

    public AbstractCollisionManager(IEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Method to detect collisions for all entities managed by the entity manager
    public final void detectCollisions() {
        for (Entity entity : entityManager.getEntities()) {
            handleCollision(entity);
        }
    }

    // Abstract method to handle collision for a single entity
    protected abstract void handleCollision(Entity entity);

    // Abstract method to dispose of resources
    public abstract void dispose();
}