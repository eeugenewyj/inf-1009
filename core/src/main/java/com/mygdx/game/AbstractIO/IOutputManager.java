package com.mygdx.game.AbstractIO;

public interface IOutputManager {
    void setVolume(float volume);
    float getVolume();
    void setMuted(boolean muted);
    boolean isMuted();
    void dispose();
}
