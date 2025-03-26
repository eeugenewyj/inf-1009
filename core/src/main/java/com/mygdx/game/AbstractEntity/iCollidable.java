package com.mygdx.game.AbstractEntity;

import com.badlogic.gdx.math.Rectangle;

public interface iCollidable {

    // Abstract method to handle collision with another collidable entity
    void handleCollision(iCollidable other);

    // Abstract method to get the bounding box of the entity for collision detection
    Rectangle getBoundingBox();
}
