package com.mygdx.game.util;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Singleton asset manager for efficient resource loading and disposal
 */
public class AssetManager {
    private static AssetManager instance;
    private final com.badlogic.gdx.assets.AssetManager manager;
    private final Map<String, Object> cachedAssets;

    private AssetManager() {
        manager = new com.badlogic.gdx.assets.AssetManager();
        cachedAssets = new HashMap<>();
    }

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }

    /**
     * Loads a texture, reusing cached instances if available
     * 
     * @param path The file path to the texture
     * @return The loaded texture
     */
    public Texture getTexture(String path) {
        String key = "texture:" + path;
        if (cachedAssets.containsKey(key)) {
            return (Texture) cachedAssets.get(key);
        }

        if (!manager.isLoaded(path, Texture.class)) {
            manager.load(path, Texture.class);
            manager.finishLoadingAsset(path);
        }

        Texture texture = manager.get(path, Texture.class);
        cachedAssets.put(key, texture);
        return texture;
    }

    /**
     * Loads a sound effect, reusing cached instances if available
     * 
     * @param path The file path to the sound
     * @return The loaded sound
     */
    public Sound getSound(String path) {
        String key = "sound:" + path;
        if (cachedAssets.containsKey(key)) {
            return (Sound) cachedAssets.get(key);
        }

        if (!manager.isLoaded(path, Sound.class)) {
            manager.load(path, Sound.class);
            manager.finishLoadingAsset(path);
        }

        Sound sound = manager.get(path, Sound.class);
        cachedAssets.put(key, sound);
        return sound;
    }

    /**
     * Loads music, reusing cached instances if available
     * 
     * @param path The file path to the music
     * @return The loaded music
     */
    public Music getMusic(String path) {
        String key = "music:" + path;
        if (cachedAssets.containsKey(key)) {
            return (Music) cachedAssets.get(key);
        }

        if (!manager.isLoaded(path, Music.class)) {
            manager.load(path, Music.class);
            manager.finishLoadingAsset(path);
        }

        Music music = manager.get(path, Music.class);
        cachedAssets.put(key, music);
        return music;
    }

    /**
     * Loads a UI skin, reusing cached instances if available
     * 
     * @param path The file path to the skin
     * @return The loaded skin
     */
    public Skin getSkin(String path) {
        String key = "skin:" + path;
        if (cachedAssets.containsKey(key)) {
            return (Skin) cachedAssets.get(key);
        }

        if (!manager.isLoaded(path, Skin.class)) {
            manager.load(path, Skin.class);
            manager.finishLoadingAsset(path);
        }

        Skin skin = manager.get(path, Skin.class);
        cachedAssets.put(key, skin);
        return skin;
    }

    /**
     * Loads a bitmap font, reusing cached instances if available
     * 
     * @param path The file path to the font
     * @return The loaded font
     */
    public BitmapFont getFont(String path) {
        String key = "font:" + path;
        if (cachedAssets.containsKey(key)) {
            return (BitmapFont) cachedAssets.get(key);
        }

        if (!manager.isLoaded(path, BitmapFont.class)) {
            manager.load(path, BitmapFont.class);
            manager.finishLoadingAsset(path);
        }

        BitmapFont font = manager.get(path, BitmapFont.class);
        cachedAssets.put(key, font);
        return font;
    }

    /**
     * Unloads a specific asset
     * 
     * @param path The file path of the asset to unload
     */
    public void unloadAsset(String path) {
        String[] prefixes = { "texture:", "sound:", "music:", "skin:", "font:" };

        for (String prefix : prefixes) {
            String key = prefix + path;
            if (cachedAssets.containsKey(key)) {
                cachedAssets.remove(key);
                break;
            }
        }

        if (manager.isLoaded(path)) {
            manager.unload(path);
        }
    }

    /**
     * Unloads all assets containing a given prefix in their path
     * Useful for unloading all assets from a specific scene
     * 
     * @param pathPrefix The prefix to match against asset paths
     */
    public void unloadAssetsWithPrefix(String pathPrefix) {
        // Create a copy of the keys to avoid concurrent modification
        String[] keys = cachedAssets.keySet().toArray(new String[0]);

        for (String key : keys) {
            if (key.contains(pathPrefix)) {
                // Extract the actual path by removing the type prefix
                String path = key.substring(key.indexOf(':') + 1);
                cachedAssets.remove(key);

                if (manager.isLoaded(path)) {
                    manager.unload(path);
                }
            }
        }
    }

    /**
     * Completely disposes the asset manager and all loaded assets
     */
    public void dispose() {
        manager.dispose();
        cachedAssets.clear();
    }
}