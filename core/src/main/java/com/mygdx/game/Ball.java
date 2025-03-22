package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;

import java.util.Random;

public class Ball extends MovableEntity {
    private int value;                  // Numeric value
    private String displayText;         // Text to display (number or math expression)
    private boolean usesMathOperation;  // Whether this ball displays a math operation
    private String operation;           // The math operation (+, -, ×)
    private int operand1, operand2;     // The numbers in the operation
    
    private boolean collected = false;
    private boolean isFalling = false;

    private static final int NUM_BALLS = 8;
    private static final float GAP_RATIO = 0.1f;
    private static final float SCREEN_WIDTH = Gdx.graphics.getWidth();
    private static final float BALL_WIDTH = SCREEN_WIDTH / (NUM_BALLS + (NUM_BALLS - 1) * GAP_RATIO);
    private static final float BALL_RADIUS = BALL_WIDTH / 2;

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private static final Random random = new Random();

    public Ball(float x, float y) {
        super(x, y, 100); // Falling speed
        
        // Determine if this ball should use math operation based on difficulty
        usesMathOperation = GameSettings.isHardMode();
        
        if (usesMathOperation) {
            generateMathOperation();
        } else {
            // Simple number for easy mode
            this.value = random.nextInt(9) + 1; // Random number 1-9
            this.displayText = String.valueOf(value);
        }
        
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
    }

    /**
     * Generates a random math operation
     */
    private void generateMathOperation() {
        // Generate random numbers for the operation
        operand1 = random.nextInt(9) + 1;  // 1-9
        operand2 = random.nextInt(9) + 1;  // 1-9
        
        // Choose a random operation
        String[] operations = {"+", "-", "*"};
        operation = operations[random.nextInt(operations.length)];
        
        // Calculate the result
        switch(operation) {
            case "+": 
                value = operand1 + operand2; 
                break;
            case "-": 
                // Ensure result is positive
                if (operand1 < operand2) {
                    int temp = operand1;
                    operand1 = operand2;
                    operand2 = temp;
                }
                value = operand1 - operand2; 
                break;
            case "*": 
                value = operand1 * operand2; 
                break;
        }
        
        // Set the display text
        displayText = operand1 + operation + operand2;
    }

    public int getValue() {
        return value;
    }

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
        // This method can be empty if you are only using `draw(SpriteBatch batch)`
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.end(); // End the sprite batch before using ShapeRenderer

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(x + BALL_RADIUS, y + BALL_RADIUS, BALL_RADIUS);
        shapeRenderer.end();

        batch.begin(); // Restart batch for font rendering

        // Center the text for proper display
        float textX, textY;
        
        // Simple number requires less space than math operation
        if (usesMathOperation) {
            textX = x + BALL_RADIUS - 15;  // Wider expression needs more offset
        } else {
            textX = x + BALL_RADIUS - 10;  // Single digit needs less offset
        }
        
        textY = y + BALL_RADIUS + 10;
        
        // Draw the text (number or math expression)
        font.draw(batch, displayText, textX, textY);
    }

    @Override
    public void handleCollision(iCollidable other) {
        if (other instanceof Player) {
            collected = true;
            setActive(false);
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }

    public static float getBallRadius() {
        return BALL_RADIUS;
    }

    public static float getBallWidth() {
        return BALL_WIDTH;
    }
    
    public String getDisplayText() {
        return displayText;
    }
    
    public boolean usesMathOperation() {
        return usesMathOperation;
    }
}