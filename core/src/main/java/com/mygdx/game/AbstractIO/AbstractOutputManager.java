package com.mygdx.game.AbstractIO;

public abstract class AbstractOutputManager implements IOutputManager{
    protected float volume = 1.0f;
    protected boolean isMuted = false;

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

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



