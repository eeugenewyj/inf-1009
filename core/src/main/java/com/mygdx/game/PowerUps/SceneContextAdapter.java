package com.mygdx.game.PowerUps;

import com.mygdx.game.iSceneContext;
import com.mygdx.game.AbstractIO.iInputManager;
import com.mygdx.game.Entities.PowerUpEffect;
import com.mygdx.game.Managers.GameEntityManager;
import com.mygdx.game.Managers.GameStateManager;
import com.mygdx.game.UI.GameUIManager;
import com.badlogic.gdx.graphics.Color;

/**
 * Adapter that implements SceneContext to provide access to game components
 */
public class SceneContextAdapter implements iSceneContext {
    private GameEntityManager entityManager;
    private GameUIManager uiManager;
    private iInputManager inputManager;
    private GameStateManager gameStateManager;
    
    /**
     * Creates a new scene context adapter
     * 
     * @param entityManager The entity manager
     * @param uiManager The UI manager
     * @param inputManager The input manager
     * @param gameStateManager The game state manager
     */
    public SceneContextAdapter(
            GameEntityManager entityManager, 
            GameUIManager uiManager, 
            iInputManager inputManager,
            GameStateManager gameStateManager) {
        this.entityManager = entityManager;
        this.uiManager = uiManager;
        this.inputManager = inputManager;
        this.gameStateManager = gameStateManager;
    }
    
    @Override
    public GameEntityManager getEntityManager() {
        return entityManager;
    }
    
    @Override
    public void createPowerUpEffect(PowerUpType powerUpType, float x, float y) {
        PowerUpEffect effect = null;

        switch (powerUpType) {
            case DOUBLE_POINTS:
                effect = PowerUpEffect.createDoublePointsEffect(x, y);
                break;
            case EXTEND_TIME:
                effect = PowerUpEffect.createTimeExtensionEffect(x, y);
                break;
            case REDUCE_TIME:
                effect = PowerUpEffect.createEffect(x, y, "-3 SECONDS!", 
                        Color.RED, 2.0f);
                break;
            case INVERT_CONTROLS:
                effect = PowerUpEffect.createEffect(x, y, "CONTROLS INVERTED!", 
                        Color.PURPLE, 2.0f);
                break;
            case SLOW_PLAYER:
                effect = PowerUpEffect.createEffect(x, y, "SPEED REDUCED!", 
                        Color.ORANGE, 2.0f);
                break;
        }

        if (effect != null) {
            entityManager.addEntity(effect);
        }
    }
    
    @Override
    public void createPowerUpEffect(int powerUpTypeId, float x, float y) {
        // Convert old int type to new enum type for backward compatibility
        PowerUpType powerUpType;
        switch (powerUpTypeId) {
            case 0: // Old TYPE_DOUBLE_POINTS
                powerUpType = PowerUpType.DOUBLE_POINTS;
                break;
            case 1: // Old TYPE_EXTEND_TIME
                powerUpType = PowerUpType.EXTEND_TIME;
                break;
            case 2: // Old TYPE_REDUCE_TIME
                powerUpType = PowerUpType.REDUCE_TIME;
                break;
            case 3: // Old TYPE_INVERT_CONTROLS
                powerUpType = PowerUpType.INVERT_CONTROLS;
                break;
            case 4: // Old TYPE_SLOW_PLAYER
                powerUpType = PowerUpType.SLOW_PLAYER;
                break;
            default:
                System.out.println("Unknown power-up type ID: " + powerUpTypeId);
                return;
        }
        
        createPowerUpEffect(powerUpType, x, y);
    }
    
    @Override
    public void updatePowerUpLabel(String text) {
        uiManager.updatePowerUpLabel(text);
    }
    
    @Override
    public iInputManager getInputManager() {
        return inputManager;
    }
    
    /**
     * Extends the game time
     * 
     * @param seconds Seconds to add
     */
    public void extendGameTime(float seconds) {
        gameStateManager.extendGameTime(seconds);
    }
    
    /**
     * Reduces the game time
     * 
     * @param seconds Seconds to subtract
     */
    public void reduceGameTime(float seconds) {
        gameStateManager.reduceGameTime(seconds);
    }
}