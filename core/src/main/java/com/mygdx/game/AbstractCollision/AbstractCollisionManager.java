package com.mygdx.game.AbstractCollision;

import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.IEntityManager;

public abstract class AbstractCollisionManager implements ICollisionManager {
    protected final IEntityManager entityManager;

    public AbstractCollisionManager(IEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public final void detectCollisions() {
        for (Entity entity : entityManager.getEntities()) {
            handleCollision(entity);
        }
    }

    protected abstract void handleCollision(Entity entity);

    public abstract void dispose();
}