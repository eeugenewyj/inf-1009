package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Helper class to create a TextureRegionDrawable from a Texture
 * This was previously defined as a class, but it already exists in libGDX,
 * so we'll convert it to a utility class with static methods.
 */
public class TextureRegionHelper {
    
    /**
     * Creates a new TextureRegionDrawable from a Texture
     * @param texture The texture to wrap
     * @return A TextureRegionDrawable for use with Scene2D
     */
    public static com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable createDrawable(Texture texture) {
        return new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(texture));
    }
}