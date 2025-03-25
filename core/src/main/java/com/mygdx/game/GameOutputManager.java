package com.mygdx.game;

import com.mygdx.game.AbstractIO.AbstractOutputManager;

public class GameOutputManager extends AbstractOutputManager {
    private float volume = 1.0f;
    private boolean isMuted = false;

    @Override
    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    @Override
    public boolean isMuted() {
        return isMuted;
    }

    @Override
    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(volume, 1.0f));
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void dispose() {

    }
}