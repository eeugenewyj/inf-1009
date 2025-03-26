package com.mygdx.game.AbstractIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class Audio extends AudioHandler {
    private float volume = 0.5f; // Default volume level
    private boolean loop = true; // Flag to indicate whether the music should loop

    private static Audio instance; // Singleton instance of the Audio class
    private Music gameMusic; // Music object for background music
    private String musicName; // Name of the music file
    private final Map<String, Sound> soundEffects = new HashMap<>(); // Map to store sound effects with their keys
    private final Map<String, Float> soundEffectVolumes = new HashMap<>(); // Map to store the volume levels of sound
                                                                           // effects

    // Private constructor to enforce singleton pattern
    private Audio() {
        // Default constructor with a placeholder music file
        this("defaultMusicFileName", 0.1f, true);
    }

    // Private constructor to initialise music with specific parameters
    private Audio(String musicName, float volume, boolean loop) {
        this.musicName = musicName;
        this.volume = volume;
        this.loop = loop;
        this.gameMusic = Gdx.audio.newMusic(Gdx.files.internal(this.musicName));
        this.gameMusic.setVolume(this.volume);
        this.gameMusic.setLooping(this.loop);
    }

    // Singleton access method to get the instance of the Audio class
    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

    // Overloaded singleton access method to initialise with specific parameters
    public static Audio getInstance(String musicName, float volume, boolean loop) {
        if (instance == null) {
            instance = new Audio(musicName, volume, loop);
        } else {
            instance.setMusicName(musicName);
            instance.setVolume(volume);
            instance.setLoop(loop);
        }
        return instance;
    }

    // Music Controls (from AudioHandler)

    // Method to play the background music
    @Override
    public void playMusic() {
        if (!gameMusic.isPlaying()) {
            gameMusic.play();
        }
    }

    // Method to stop the background music
    @Override
    public void stopMusic() {
        gameMusic.stop();
    }

    // Method to pause the background music
    public void pauseMusic() {
        if (gameMusic.isPlaying()) { // Only pause if the music is actually playing
            gameMusic.pause();
            System.out.println("Music Paused: " + musicName);
        } else {
            System.out.println("Music is not playing, cannot pause.");
        }
    }

    // Method to resume the background music
    public void resumeMusic() {
        if (!gameMusic.isPlaying()) { // Only resume if the music is not playing
            gameMusic.play();
            System.out.println("Music Resumed: " + musicName);
        } else {
            System.out.println("Music is already playing.");
        }
    }

    // Method to check if the music is playing
    @Override
    public boolean isPlayingMusic() {
        return gameMusic.isPlaying();
    }

    // Method to set the name of the music file and reload it
    @Override
    public void setMusicName(String musicName) {
        if (this.gameMusic != null) {
            this.gameMusic.dispose();
        }
        this.musicName = musicName;
        this.gameMusic = Gdx.audio.newMusic(Gdx.files.internal(this.musicName));
        this.gameMusic.setVolume(this.volume);
        this.gameMusic.setLooping(this.loop);
    }

    @Override
    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(volume, 1.0f));
        this.gameMusic.setVolume(this.volume);
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public void setLoop(boolean loop) {
        this.loop = loop;
        this.gameMusic.setLooping(this.loop);
    }

    @Override
    public boolean getLoop() {
        return this.loop;
    }

    // Sound Effects (from AudioHandler)

    // Method to load a sound effect with a specific key and file path
    @Override
    public void loadSoundEffect(String key, String filePath) {
        if (!soundEffects.containsKey(key)) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            soundEffects.put(key, sound);
            soundEffectVolumes.put(key, 1.0f); // Default volume
        }
    }

    // Method to play a sound effect by its key
    @Override
    public void playSoundEffect(String key) {
        Sound sound = soundEffects.get(key);
        if (sound != null) {
            float effectVolume = soundEffectVolumes.getOrDefault(key, 1.0f);
            sound.play(effectVolume);
        } else {
            System.out.println("Sound effect not found: " + key);
        }
    }

    // Method to set the volume of a sound effect by its key
    @Override
    public void setSoundEffectVolume(String key, float volume) {
        if (soundEffects.containsKey(key)) {
            soundEffectVolumes.put(key, Math.max(0, Math.min(volume, 1.0f)));
        } else {
            System.out.println("Sound effect not found: " + key);
        }
    }

    @Override
    public void dispose() {
        if (gameMusic != null) {
            gameMusic.dispose();
        }
        for (Sound sound : soundEffects.values()) {
            sound.dispose();
        }
        soundEffects.clear();
    }
}
