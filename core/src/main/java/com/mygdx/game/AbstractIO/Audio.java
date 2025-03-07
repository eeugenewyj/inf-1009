package com.mygdx.game.AbstractIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;
import java.util.Map;

public class Audio extends AudioHandler {
    private static Audio instance; // Singleton instance
    private Music gameMusic;
    private String musicName;
    private final Map<String, Sound> soundEffects = new HashMap<>();
    private final Map<String, Float> soundEffectVolumes = new HashMap<>();
    private float volume = 0.5f;
    private boolean loop = true;

    // Private constructor to enforce singleton pattern
    private Audio() {
        //this("Music/MainScreenMusic.mp3", 0.1f, true);
        this("defaultMusicFileName", 0.1f, true);
    }

    private Audio(String musicName, float volume, boolean loop) {
        this.musicName = musicName;
        this.volume = volume;
        this.loop = loop;
        this.gameMusic = Gdx.audio.newMusic(Gdx.files.internal(this.musicName));
        this.gameMusic.setVolume(this.volume);
        this.gameMusic.setLooping(this.loop);
    }

    // Singleton Access Method
    public static Audio getInstance() {
        if (instance == null) {
            instance = new Audio();
        }
        return instance;
    }

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
    @Override
    public void playMusic() {
        if (!gameMusic.isPlaying()) {
            gameMusic.play();
        }
    }

    @Override
    public void stopMusic() {
        gameMusic.stop();
    }

    public void pauseMusic() {
        if (gameMusic.isPlaying()) { // Only pause if the music is actually playing
            gameMusic.pause();
            System.out.println("Music Paused: " + musicName);
        } else {
            System.out.println("Music is not playing, cannot pause.");
        }
    }

    public void resumeMusic() {
        if (!gameMusic.isPlaying()) { // Only resume if the music is not playing
            gameMusic.play();
            System.out.println("Music Resumed: " + musicName);
        } else {
            System.out.println("Music is already playing.");
        }
    }

    @Override
    public boolean isPlayingMusic() {
        return gameMusic.isPlaying();
    }

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
    @Override
    public void loadSoundEffect(String key, String filePath) {
        if (!soundEffects.containsKey(key)) {
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(filePath));
            soundEffects.put(key, sound);
            soundEffectVolumes.put(key, 1.0f); // Default volume
        }
    }

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

