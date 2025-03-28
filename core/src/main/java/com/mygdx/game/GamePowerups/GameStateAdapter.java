package com.mygdx.game.GamePowerups;

import com.mygdx.game.GameScenes.GameLoopHandler;
import com.mygdx.game.GameState.iGameStateListener;
import com.mygdx.game.GameUI.GameUIManager;

/**
 * Adapter that implements GameStateListener to handle game state changes
 */
public class GameStateAdapter implements iGameStateListener {
    private GameUIManager uiManager;
    private GameLoopHandler gameLoop;
    private Runnable onGameOverAction;

    /**
     * Creates a new game state adapter
     * 
     * @param uiManager        The UI manager to update
     * @param gameLoop         The game loop handler
     * @param onGameOverAction Action to execute when game is over
     */
    public GameStateAdapter(GameUIManager uiManager, GameLoopHandler gameLoop, Runnable onGameOverAction) {
        this.uiManager = uiManager;
        this.gameLoop = gameLoop;
        this.onGameOverAction = onGameOverAction;
    }

    @Override
    public void onScoreChanged(int newScore) {
        uiManager.updateScore(newScore);
    }

    @Override
    public void onTimerUpdated(float remainingTime) {
        uiManager.updateTimer(remainingTime);
    }

    @Override
    public void onGameOver(int finalScore, boolean isNewHighScore) {
        gameLoop.setGameActive(false);

        if (onGameOverAction != null) {
            onGameOverAction.run();
        }
    }

    @Override
    public void showGameOver(int finalScore, boolean isNewHighScore) {
        uiManager.showGameOver(finalScore, isNewHighScore);
    }

    /**
     * Shows the math facts popup and then the game over screen
     * 
     * @param finalScore     The final score
     * @param isNewHighScore Whether this is a new high score
     */
    public void showGameOverWithMathFact(int finalScore, boolean isNewHighScore) {
        // First show the math facts popup
        uiManager.showMathFactsPopup(() -> {
            // Then show the game over screen once popup is closed
            showGameOver(finalScore, isNewHighScore);
        });
    }

    public void setGameLoop(GameLoopHandler gameLoop) {
        this.gameLoop = gameLoop;
    }
}