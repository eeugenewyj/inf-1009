package com.mygdx.game.PowerUps;

import com.mygdx.game.iEntityScoreHandler;
import com.mygdx.game.Managers.GameStateManager;
import com.mygdx.game.Managers.PowerUpManager;

/**
 * Adapter that implements EntityScoreHandler to handle score and power-up events
 */
public class EntityScoreAdapter implements iEntityScoreHandler {
    private GameStateManager gameStateManager;
    private PowerUpManager powerUpManager;
    
    /**
     * Creates a new entity score adapter
     * 
     * @param gameStateManager The game state manager
     * @param powerUpManager The power-up manager
     */
    public EntityScoreAdapter(GameStateManager gameStateManager, PowerUpManager powerUpManager) {
        this.gameStateManager = gameStateManager;
        this.powerUpManager = powerUpManager;
    }
    
    @Override
    public void addScore(int points) {
        gameStateManager.addScore(points, powerUpManager.isDoublePointsActive());
    }
    
    @Override
    public void processPowerUp(PowerUpType powerUpType, float x, float y) {
        powerUpManager.processPowerUp(powerUpType, x, y);
    }
    
    @Override
    public void processPowerUp(int powerUpTypeId, float x, float y) {
        powerUpManager.processPowerUp(powerUpTypeId, x, y);
    }
}