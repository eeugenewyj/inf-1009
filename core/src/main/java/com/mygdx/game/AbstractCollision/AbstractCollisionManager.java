package com.mygdx.game.AbstractCollision;

import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.iEntityManager;

public abstract class AbstractCollisionManager implements iCollisionManager {
    // Changed from protected to private for better encapsulation
    private final iEntityManager entityManager;

    // Constructor to initialise the collision manager with an entity manager
    public AbstractCollisionManager(iEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Method to detect collisions for all entities managed by the entity manager
    public final void detectCollisions() {
        for (Entity entity : entityManager.getEntities()) {
            handleCollision(entity);
        }
    }

    // Protected getter method to allow subclasses to access the entity manager
    protected iEntityManager getEntityManager() {
        return entityManager;
    }

    // Abstract method to handle collision for a single entity
    protected abstract void handleCollision(Entity entity);

    public abstract void dispose();
}