package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;

import java.util.Random;

public class PowerUp extends MovableEntity {
    // Power-up types
    public static final int TYPE_DOUBLE_POINTS = 0;
    public static final int TYPE_EXTEND_TIME = 1;
    
    private int type;
    private String symbol; // Symbol to display inside the star
    
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout glyphLayout; // Use GlyphLayout for text measurements
    private Color powerUpColor;
    
    // Constants for rendering
    private static final float POWERUP_SIZE = 40;
    private static final float STAR_INNER_RATIO = 0.4f; // Inner star size relative to outer
    
    public PowerUp(float x, float y, int type) {
        super(x, y, 120); // Slightly faster than balls
        this.type = type;
        
        // Set symbol and color based on type
        switch(type) {
            case TYPE_DOUBLE_POINTS:
                symbol = "x2";
                powerUpColor = Color.GOLD;
                break;
            case TYPE_EXTEND_TIME:
                symbol = "+5s";
                powerUpColor = Color.CYAN;
                break;
            default:
                symbol = "?";
                powerUpColor = Color.WHITE;
        }
        
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        glyphLayout = new GlyphLayout(); // Initialize the GlyphLayout
    }
    
    public int getType() {
        return type;
    }
    
    @Override
    public void moveAIControlled() {
        float delta = Gdx.graphics.getDeltaTime();
        y -= speed * delta;
    }
    
    @Override
    public void update(float deltaTime) {
        moveAIControlled();
    }
    
    @Override
    public void draw() {
        // Empty implementation as we're using the SpriteBatch version
    }
    
    @Override
    public void draw(SpriteBatch batch) {
        // End SpriteBatch to use ShapeRenderer
        batch.end();
        
        // Draw star shape
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Draw the power-up star
        drawStar(x + POWERUP_SIZE/2, y + POWERUP_SIZE/2, POWERUP_SIZE/2, POWERUP_SIZE*STAR_INNER_RATIO/2, 5, powerUpColor);
        
        shapeRenderer.end();
        
        // Restart batch for text
        batch.begin();
        
        // Calculate the dimensions of the text for centering
        glyphLayout.setText(font, symbol);
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;
        
        // Draw the symbol text centered in the star
        font.draw(batch, symbol, 
                  x + POWERUP_SIZE/2 - textWidth/2, 
                  y + POWERUP_SIZE/2 + textHeight/2);
    }
    
    // Helper method to draw a star shape
    private void drawStar(float x, float y, float outerRadius, float innerRadius, int points, Color color) {
        shapeRenderer.setColor(color);
        
        float angle = 0;
        float angleIncrement = (float) (2 * Math.PI / points);
        
        for (int i = 0; i < points; i++) {
            // Outer point
            float outerX = x + (float) Math.cos(angle) * outerRadius;
            float outerY = y + (float) Math.sin(angle) * outerRadius;
            
            // Inner point
            float innerAngle = angle + angleIncrement / 2;
            float innerX = x + (float) Math.cos(innerAngle) * innerRadius;
            float innerY = y + (float) Math.sin(innerAngle) * innerRadius;
            
            if (i == 0) {
                shapeRenderer.triangle(x, y, outerX, outerY, innerX, innerY);
            } else {
                float prevOuterX = x + (float) Math.cos(angle - angleIncrement) * outerRadius;
                float prevOuterY = y + (float) Math.sin(angle - angleIncrement) * outerRadius;
                
                shapeRenderer.triangle(x, y, prevOuterX, prevOuterY, innerX, innerY);
                shapeRenderer.triangle(x, y, outerX, outerY, innerX, innerY);
            }
            
            angle += angleIncrement;
        }
        
        // Connect the last and first points
        float lastInnerX = x + (float) Math.cos(angle - angleIncrement / 2) * innerRadius;
        float lastInnerY = y + (float) Math.sin(angle - angleIncrement / 2) * innerRadius;
        
        float firstOuterX = x + (float) Math.cos(0) * outerRadius;
        float firstOuterY = y + (float) Math.sin(0) * outerRadius;
        
        shapeRenderer.triangle(x, y, lastInnerX, lastInnerY, firstOuterX, firstOuterY);
    }
    
    @Override
    public void handleCollision(iCollidable other) {
        if (other instanceof Player) {
            setActive(false);
        }
    }
    
    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
    
    // Static method to spawn a random power-up
    public static PowerUp createRandomPowerUp(float x, float y) {
        int type = new Random().nextInt(2); // 0 or 1 for our two power-up types
        return new PowerUp(x, y, type);
    }
}