package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.Gdx;

import java.util.Random;

/**
 * A popup window that displays random math facts
 */
public class MathFactsPopup extends Window {
    private static final Random random = new Random();
    private Label factLabel;
    private Runnable onCloseAction;
    private static int lastFactIndex = -1; // Keep track of the last shown fact
    
    // Array of math facts
    private static final String[] MATH_FACTS = {
        "Multiplying by 9 has a finger trick!\n" +
        "Hold out both hands. To multiply 9 × 4, bend your 4th finger.\n" +
        "Count the fingers to the left (3), and right (6).\n" +
        "Result: 36. Works for 9 × 1 through 9 × 10!",
        
        "All multiples of 9 have digits that add to 9\n" +
        "Examples: 9 → 9, 18 → 1+8=9, 27 → 2+7=9, 81 → 8+1=9.\n" +
        "And if you keep reducing: 126 → 1+2+6 = 9.",
        
        "The Number 1089 Trick\n" +
        "Pick a 3-digit number where the first and last digits differ by at least 2 (e.g., 532).\n" +
        "Reverse it (235), subtract the smaller from the larger: 532 - 235 = 297.\n" +
        "Reverse the result: 792.\n" +
        "Add them: 297 + 792 = 1089 – always!",
        
        "Multiplying by 11 (Up to 2-digit numbers)\n" +
        "Take a 2-digit number, say 23.\n" +
        "Add the digits: 2 + 3 = 5.\n" +
        "Put the result in the middle: 253 → 11 × 23 = 253.\n" +
        "Works when the sum is less than 10 (no carry).",
        
        "Zero is the only number that's neither positive nor negative.\n" +
        "It's literally in its own category: neutral.",
        
        "Adding all odd numbers always gives a perfect square\n" +
        "1 = 1²\n" +
        "1 + 3 = 4 = 2²\n" +
        "1 + 3 + 5 = 9 = 3²\n" +
        "1 + 3 + 5 + 7 = 16 = 4²",
        
        "Multiplying two numbers can be done using lines (Japanese Method)\n" +
        "It's a visual technique: draw lines to represent digits and count intersections.\n" +
        "Great for visual learners—and it works!",
        
        "There's a pattern to the 5 times table\n" +
        "5, 10, 15, 20, 25...\n" +
        "Even-numbered results end in 0.\n" +
        "Odd-numbered results end in 5.",
        
        "Infinity isn't a number\n" +
        "You can't reach infinity, add to it, or subtract from it in normal math—because it's a concept, not a number.\n" +
        "Yet, weirdly, ∞ + 1 = ∞.",
        
        "A number divisible by 3 has digits that sum to a multiple of 3\n" +
        "Example: 123 → 1+2+3 = 6 → divisible by 3\n" +
        "Works for any size number!"
    };

    /**
     * Creates a new math facts popup
     * @param skin The UI skin to use
     * @param onCloseAction Action to perform when the popup is closed
     */
    public MathFactsPopup(Skin skin, Runnable onCloseAction) {
        super("MATH FACT", skin);
        this.onCloseAction = onCloseAction;
        
        // Set window properties
        setModal(true);
        setMovable(false);
        setResizable(false);
        
        // Create fact label with random fact
        String randomFact = getRandomFact();
        factLabel = new Label(randomFact, skin);
        factLabel.setFontScale(1.2f);
        factLabel.setAlignment(Align.center);
        factLabel.setColor(Color.BLACK); // Changed text color to black for better contrast
        factLabel.setWrap(true); // Enable text wrapping
        
        // Create a scroll pane for the fact text
        Table factTable = new Table();
        // Don't set background - it's causing the crash
        factTable.add(factLabel).width(400).pad(10);
        
        ScrollPane scrollPane = new ScrollPane(factTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        
        // Create a close button
        TextButton closeButton = new TextButton("Continue", skin);
        closeButton.getLabel().setColor(Color.BLUE); // Changed button text color
        
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                close();
            }
        });
        
        // Add elements to the window
        add(scrollPane).width(400).height(250).pad(10).row();
        add(closeButton).size(150, 40).pad(10).row();
        
        // Set size and position
        setSize(500, 350);
        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2
        );
        
        // Set window color to WHITE for better visibility (this should work fine)
        setColor(1, 1, 1, 1); // White with full opacity
        
        // Make the title more prominent
        getTitleLabel().setColor(Color.BLUE);
        getTitleLabel().setFontScale(1.5f);
        
        // Set background color directly if possible
        setBackground(skin.newDrawable("window", 1, 1, 1, 1)); // White color
    }
    
    /**
     * Gets a random math fact from the collection, avoiding repeats when possible
     */
    private String getRandomFact() {
        // If we only have one fact, just return it
        if (MATH_FACTS.length <= 1) {
            return MATH_FACTS[0];
        }
        
        // Generate a new random index that's different from the last one
        int newIndex;
        do {
            newIndex = random.nextInt(MATH_FACTS.length);
        } while (newIndex == lastFactIndex && MATH_FACTS.length > 1);
        
        // Store this index as the last shown fact
        lastFactIndex = newIndex;
        return MATH_FACTS[newIndex];
    }
    
    /**
     * Refreshes the displayed fact with a new random one
     */
    public void refreshFact() {
        factLabel.setText(getRandomFact());
    }
    
    /**
     * Shows this popup on the given stage
     * @param stage The stage to show the popup on
     */
    public void show(Stage stage) {
        stage.addActor(this);
        
        // Ensure this popup is on top
        toFront();
        
        // Set keyboard focus to this popup
        stage.setKeyboardFocus(this);
    }
    
    /**
     * Closes the popup and executes the onClose action if provided
     */
    public void close() {
        remove();
        if (onCloseAction != null) {
            onCloseAction.run();
        }
    }
}