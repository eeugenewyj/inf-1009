package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.AbstractEntity.MovableEntity;
import com.mygdx.game.AbstractEntity.iCollidable;

import java.util.Random;

public class Ball extends MovableEntity {
    private int value; // Numeric value of the balloon
    private String displayText; // Text displayed on the balloon
    private boolean usesMathOperation; // Whether this ball displays a math operation
    private String operation; // The math operation (+, -, ×)
    private int operand1, operand2; // The numbers in the operation

    private boolean collected = false; // Whether the balloon has been collected
    private boolean isFalling = false; // Whether the balloon is currently falling
    private Texture balloonTexture; // Texture for the balloon

    // Constants for ball dimensions and spacing
    private static final int NUM_BALLS = 8;
    private static final float GAP_RATIO = 0.1f;
    private static final float SCREEN_WIDTH = Gdx.graphics.getWidth();
    private static final float BALL_WIDTH = SCREEN_WIDTH / (NUM_BALLS + (NUM_BALLS - 1) * GAP_RATIO);
    private static final float BALL_RADIUS = BALL_WIDTH / 2;

    private BitmapFont font; // Font for displaying text on the balloon
    private static final Random random = new Random(); // Random number generator for balloon

    // Different balloon colors for visual variety
    private static final Color[] BALLOON_COLORS = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.PINK, Color.CYAN, Color.ORANGE, Color.PURPLE
    };
    private Color balloonColor; // Color of the balloon

    public Ball(float x, float y) {
        super(x, y, 100); // Falling speed

        // Determine if this ball should use math operation based on difficulty
        usesMathOperation = GameSettings.isHardMode();

        if (usesMathOperation) {
            generateMathOperation(); // Generate a math operation for the balloon
        } else {
            // Generate a simple number for easy mode
            this.value = random.nextInt(9) + 1; // Random number 1-9
            this.displayText = String.valueOf(value);
        }

        // Choose a random color for this balloon
        balloonColor = BALLOON_COLORS[random.nextInt(BALLOON_COLORS.length)];

        // Load balloon texture
        balloonTexture = new Texture(Gdx.files.internal("balloon.png"));

        // Initialise the font for displaying text
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(1.1f); // Slightly larger font for better visibility
    }

    /**
     * Generates a random math operation
     */
    private void generateMathOperation() {
        // Generate random numbers for the operation
        operand1 = random.nextInt(9) + 1; // 1-9
        operand2 = random.nextInt(9) + 1; // 1-9

        // Choose a random operation
        String[] operations = { "+", "-", "*" };
        operation = operations[random.nextInt(operations.length)];

        // Calculate the result
        switch (operation) {
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

    @Override
    public void moveAIControlled() {
        // Store current position before moving
        this.previousX = this.x;
        this.previousY = this.y;

        // Move the balloon downwards
        float delta = Gdx.graphics.getDeltaTime();
        y -= speed * delta;

        // Add a small horizontal wobble for balloon effect
        x += Math.sin(y * 0.05) * 0.5f;
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
        // Draw the balloon texture with the selected color
        batch.setColor(balloonColor);
        batch.draw(balloonTexture, x, y, BALL_WIDTH, BALL_WIDTH * 1.2f); // Slightly taller for balloon shape
        batch.setColor(Color.WHITE); // Reset color

        // Center the text for proper display
        float textX, textY;

        // Simple number requires less space than math operation
        if (usesMathOperation) {
            textX = x + BALL_RADIUS - 15; // Wider expression needs more offset
        } else {
            textX = x + BALL_RADIUS - 8; // Single digit needs less offset
        }

        textY = y + BALL_RADIUS + 5;

        // Choose text color based on balloon color for better contrast
        if (balloonColor.equals(Color.BLUE) ||
                balloonColor.equals(Color.PURPLE) ||
                balloonColor.equals(Color.RED) ||
                balloonColor.equals(Color.GREEN)) {
            // Use white text on dark balloons
            font.setColor(Color.WHITE);
        } else {
            // Use black text on light balloons
            font.setColor(Color.BLACK);
        }

        // Add a slight outline effect for better readability
        // First draw the text slightly offset in each direction with black/white
        Color outlineColor = font.getColor().equals(Color.WHITE) ? Color.BLACK : Color.WHITE;

        // Save original color
        Color originalColor = new Color(font.getColor());

        // Draw outline
        font.setColor(outlineColor);
        // Small offsets for outline effect
        font.draw(batch, displayText, textX - 1, textY - 1);
        font.draw(batch, displayText, textX + 1, textY - 1);
        font.draw(batch, displayText, textX - 1, textY + 1);
        font.draw(batch, displayText, textX + 1, textY + 1);

        // Restore original color and draw the main text
        font.setColor(originalColor);
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
        if (balloonTexture != null) {
            balloonTexture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
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