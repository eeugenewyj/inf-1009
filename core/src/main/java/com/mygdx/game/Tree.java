package com.mygdx.game;

import com.mygdx.game.AbstractEntity.StaticObject;
import com.mygdx.game.AbstractEntity.iCollidable;

public class Tree extends StaticObject {
    public Tree(float x, float y) {
        super(x, y, 50, 50, "tree.png"); // Tree has a default size and texture
    }

    @Override
    public void handleCollision(iCollidable other) {
        // Trees don't need to print anything since other entities handle the printing
        // This matches the original behavior where trees didn't print messages
    }
}
