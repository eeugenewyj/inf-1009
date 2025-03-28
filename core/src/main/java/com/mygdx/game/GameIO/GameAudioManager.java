package com.mygdx.game.GameIO;

import com.mygdx.game.AbstractIO.Audio;

public class GameAudioManager {
    private Audio audio; // Instance of the Audio class for handling audio operations

    public GameAudioManager() {
        audio = Audio.getInstance(); // Get the singleton instance of the Audio class
        initializeAudio();
    }

    // Initialises audio resources
    private void initializeAudio() {
        // Load sound effects with their respective keys and file paths
        audio.loadSoundEffect("spikes", "Music/spikes.mp3");
        audio.loadSoundEffect("player", "Music/collisioneffect.mp3");
        audio.loadSoundEffect("powerup", "Music/powerup.mp3");
        audio.loadSoundEffect("debuff", "Music/debuff.mp3");

        audio.setSoundEffectVolume("powerup", 0.3f); // Reduce to 30% volume
        audio.setSoundEffectVolume("debuff", 0.3f); // Reduce to 30% volume
    }

    public void playSpikesCollisionSound() {
        audio.playSoundEffect("spikes");
    }

    public void playPlayerCollectionSound() {
        audio.playSoundEffect("player");
    }

    public void playPowerUpSound() {
        audio.playSoundEffect("powerup");
    }

    public void playDebuffSound() {
        audio.playSoundEffect("debuff");
    }

    public void pauseMusic() {
        audio.pauseMusic();
    }

    public void resumeMusic() {
        audio.resumeMusic();
    }

    public Audio getAudio() {
        return audio;
    }
}