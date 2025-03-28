package com.mygdx.game.GameEntity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;
import com.mygdx.game.GamePowerups.PowerUpType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PowerUp extends MovableEntity {
    // Constants for rendering
    private static final float POWERUP_SIZE = 55; // Larger size
    private static final float STAR_INNER_RATIO = 0.6f; // Fuller star ratio

    // Static shared instance for all PowerUps - this is the key fix
    private static ShapeRenderer sharedShapeRenderer;
    
    private PowerUpType type;
    private BitmapFont font;
    private GlyphLayout glyphLayout; // Use GlyphLayout for text measurements

    public PowerUp(float x, float y, PowerUpType type) {
        super(x, y, 120); // Slightly faster than balls
        this.type = type;

        // Initialize shared shape renderer if needed
        if (sharedShapeRenderer == null) {
            sharedShapeRenderer = new ShapeRenderer();
        }
        
        font = new BitmapFont();
        font.setColor(Color.WHITE); // White text for ALL power-ups

        // Slightly smaller text size (1.6 instead of 2.0)
        font.getData().setScale(1.6f);

        glyphLayout = new GlyphLayout(); // Initialize the GlyphLayout
    }

    @Override
    public void moveAIControlled() {
        float delta = Gdx.graphics.getDeltaTime();
        setY(getY() - getSpeed() * delta);
    }

    @Override
    public void handleCollision(iCollidable other) {
        if (other instanceof Player) {
            setActive(false);
        }
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
        // Make sure we have a valid shapeRenderer
        if (sharedShapeRenderer == null) {
            sharedShapeRenderer = new ShapeRenderer();
        }
        
        // End SpriteBatch to use ShapeRenderer
        batch.end();

        // Draw star shape
        sharedShapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        sharedShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the power-up star
        drawStar(getX() + POWERUP_SIZE / 2, getY() + POWERUP_SIZE / 2, POWERUP_SIZE / 2, POWERUP_SIZE * STAR_INNER_RATIO / 2, 5,
                type.getColor());

        sharedShapeRenderer.end();

        // Draw outline for better visibility
        sharedShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        sharedShapeRenderer.setColor(Color.BLACK); // Black outline for all stars
        drawStar(getX() + POWERUP_SIZE / 2, getY() + POWERUP_SIZE / 2, POWERUP_SIZE / 2, POWERUP_SIZE * STAR_INNER_RATIO / 2, 5,
                sharedShapeRenderer.getColor());
        sharedShapeRenderer.end();

        // Restart batch for text
        batch.begin();

        // Calculate the dimensions of the text for centering
        glyphLayout.setText(font, type.getSymbol());
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;

        // Draw the symbol text centered in the star
        font.draw(batch, type.getSymbol(),
                getX() + POWERUP_SIZE / 2 - textWidth / 2,
                getY() + POWERUP_SIZE / 2 + textHeight / 2);
    }

    // Helper method to draw a star shape
    private void drawStar(float x, float y, float outerRadius, float innerRadius, int points, Color color) {
        sharedShapeRenderer.setColor(color);

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
                sharedShapeRenderer.triangle(x, y, outerX, outerY, innerX, innerY);
            } else {
                float prevOuterX = x + (float) Math.cos(angle - angleIncrement) * outerRadius;
                float prevOuterY = y + (float) Math.sin(angle - angleIncrement) * outerRadius;

                sharedShapeRenderer.triangle(x, y, prevOuterX, prevOuterY, innerX, innerY);
                sharedShapeRenderer.triangle(x, y, outerX, outerY, innerX, innerY);
            }

            angle += angleIncrement;
        }

        // Connect the last and first points
        float lastInnerX = x + (float) Math.cos(angle - angleIncrement / 2) * innerRadius;
        float lastInnerY = y + (float) Math.sin(angle - angleIncrement / 2) * innerRadius;

        float firstOuterX = x + (float) Math.cos(0) * outerRadius;
        float firstOuterY = y + (float) Math.sin(0) * outerRadius;

        sharedShapeRenderer.triangle(x, y, lastInnerX, lastInnerY, firstOuterX, firstOuterY);
    }

    // Static method to spawn a random power-up (including debuffs)
    public static PowerUp createRandomPowerUp(float x, float y) {
        // Get all power-up types
        PowerUpType[] types = PowerUpType.values();
        
        // Separate buffs and debuffs
        List<PowerUpType> buffs = Arrays.stream(types)
            .filter(t -> !t.isDebuff())
            .collect(Collectors.toList());
            
        List<PowerUpType> debuffs = Arrays.stream(types)
            .filter(PowerUpType::isDebuff)
            .collect(Collectors.toList());
        
        // 50% chance for a debuff
        Random rand = new Random();
        PowerUpType selectedType;
        
        if (rand.nextFloat() < 0.5f) {
            // Select a random debuff
            selectedType = debuffs.get(rand.nextInt(debuffs.size()));
        } else {
            // Select a random buff
            selectedType = buffs.get(rand.nextInt(buffs.size()));
        }
        
        return new PowerUp(x, y, selectedType);
    }

    public PowerUpType getType() {
        return type;
    }

    public boolean isDebuff() {
        return type.isDebuff();
    }

    // Clean up static resources when game is shutdown
    public static void disposeSharedResources() {
        if (sharedShapeRenderer != null) {
            sharedShapeRenderer.dispose();
            sharedShapeRenderer = null;
        }
    }

    @Override
    public void dispose() {
        // Only dispose non-shared resources
        if (font != null) {
            font.dispose();
            font = null;
        }
        
        // Do NOT dispose the shared shape renderer here
    }
}