package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.AbstractEntity.Entity;
import com.mygdx.game.AbstractEntity.iCollidable;

/**
 * Visual effect that appears when a power-up is collected
 */
public class PowerUpEffect extends Entity {
    private String text;
    private float lifeTime;
    private float maxLifeTime;
    private Color color;
    private BitmapFont font;
    private float alpha = 1.0f;
    
    public PowerUpEffect(float x, float y, String text, Color color, float duration) {
        super(x, y);
        this.text = text;
        this.color = new Color(color);
        this.maxLifeTime = duration;
        this.lifeTime = 0;
        
        this.font = new BitmapFont();
        this.font.setColor(this.color);
        this.font.getData().setScale(1.5f);
    }
    
    @Override
    public void update(float deltaTime) {
        lifeTime += deltaTime;
        
        // Move upward slowly
        y += 30 * deltaTime;
        
        // Fade out as it reaches end of life
        alpha = 1.0f - (lifeTime / maxLifeTime);
        
        if (lifeTime >= maxLifeTime) {
            setActive(false);
        }
    }
    
    @Override
    public void draw() {
        // Not used
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        // Set color with calculated alpha
        font.setColor(color.r, color.g, color.b, alpha);
        
        // Draw text
        font.draw(batch, text, x, y);
    }
    
    @Override
    public void handleCollision(iCollidable other) {
        // No collision for effects
    }
    
    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
    }
    
    /**
     * Creates a double points effect
     */
    public static PowerUpEffect createDoublePointsEffect(float x, float y) {
        return new PowerUpEffect(x, y, "DOUBLE POINTS!", Color.GOLD, 2.0f);
    }
    
    /**
     * Creates a time extension effect
     */
    public static PowerUpEffect createTimeExtensionEffect(float x, float y) {
        return new PowerUpEffect(x, y, "+5 SECONDS!", Color.CYAN, 2.0f);
    }
}