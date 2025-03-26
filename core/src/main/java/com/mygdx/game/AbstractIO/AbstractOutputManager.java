package com.mygdx.game.AbstractIO;

public abstract class AbstractOutputManager implements IOutputManager {
    protected float volume = 1.0f; // Volume level for output
    protected boolean isMuted = false; // Flag to indicate whether the output is muted

    // Method to mute or unmute the output
    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    // Method to set the volume level, ensuring it stays within the range [0.0 -
    // 1.0]
    public void setVolume(float newVolume) {
        volume = Math.max(0, Math.min(newVolume, 1.0f));
    }

    public float getVolume() {
        return volume;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public abstract void dispose();
}
