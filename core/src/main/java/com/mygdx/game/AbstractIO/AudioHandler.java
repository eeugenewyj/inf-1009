package com.mygdx.game.AbstractIO;

public abstract class AudioHandler {
    // Music controls
    public abstract void playMusic();
    public abstract void stopMusic();
    public abstract void pauseMusic();
    public abstract boolean isPlayingMusic();
    public abstract void setMusicName(String musicName);
    public abstract void setVolume(float volume);
    public abstract float getVolume();
    public abstract void setLoop(boolean loop);
    public abstract boolean getLoop();

    // Sound effect methods
    public abstract void loadSoundEffect(String key, String filePath);
    public abstract void playSoundEffect(String key);
    public abstract void setSoundEffectVolume(String key, float volume);

    // Cleanup
    public abstract void dispose();
}

