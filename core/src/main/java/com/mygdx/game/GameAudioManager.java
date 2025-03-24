package com.mygdx.game;

import com.mygdx.game.AbstractIO.Audio;

/**
 * Manages game audio
 */
public class GameAudioManager {
    private Audio audio;
    
    public GameAudioManager() {
        audio = Audio.getInstance();
        initializeAudio();
    }
    
    /**
     * Initializes audio resources
     */
    private void initializeAudio() {
        audio.loadSoundEffect("tree", "Music/tree.mp3");
        audio.loadSoundEffect("player", "Music/collisioneffect.mp3");
        audio.loadSoundEffect("powerup", "Music/powerup.mp3");
        audio.loadSoundEffect("debuff", "Music/debuff.mp3");

        audio.setSoundEffectVolume("powerup", 0.3f); // Reduce to 30% volume
        audio.setSoundEffectVolume("debuff", 0.3f); // Reduce to 30% volume
    }
    
    /**
     * Plays the tree collision sound
     */
    public void playTreeCollisionSound() {
        audio.playSoundEffect("tree");
    }
    
    /**
     * Plays the player collection sound
     */
    public void playPlayerCollectionSound() {
        audio.playSoundEffect("player");
    }
    
    /**
     * Plays the power-up collection sound
     */
    public void playPowerUpSound() {
        audio.playSoundEffect("powerup");
    }
    
    /**
     * Plays the debuff collection sound
     */
    public void playDebuffSound() {
        audio.playSoundEffect("debuff");
    }
    
    /**
     * Pauses the background music
     */
    public void pauseMusic() {
        audio.pauseMusic();
    }
    
    /**
     * Resumes the background music
     */
    public void resumeMusic() {
        audio.resumeMusic();
    }
    
    /**
     * Gets the audio instance
     */
    public Audio getAudio() {
        return audio;
    }
}