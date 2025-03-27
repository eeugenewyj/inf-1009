package com.mygdx.game;

import com.mygdx.game.AbstractIO.IInputManager;

/**
 * Adapter that implements SceneContext to provide access to game components
 */
public class SceneContextAdapter implements SceneContext {
    private GameEntityManager entityManager;
    private GameUIManager uiManager;
    private IInputManager inputManager;
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
            IInputManager inputManager,
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
    public void createPowerUpEffect(int powerUpType, float x, float y) {
        PowerUpEffect effect = null;

        switch (powerUpType) {
            case PowerUp.TYPE_DOUBLE_POINTS:
                effect = PowerUpEffect.createDoublePointsEffect(x, y);
                break;
            case PowerUp.TYPE_EXTEND_TIME:
                effect = PowerUpEffect.createTimeExtensionEffect(x, y);
                break;
            case PowerUp.TYPE_REDUCE_TIME:
                effect = PowerUpEffect.createEffect(x, y, "-3 SECONDS!", 
                        com.badlogic.gdx.graphics.Color.RED, 2.0f);
                break;
            case PowerUp.TYPE_INVERT_CONTROLS:
                effect = PowerUpEffect.createEffect(x, y, "CONTROLS INVERTED!", 
                        com.badlogic.gdx.graphics.Color.PURPLE, 2.0f);
                break;
            case PowerUp.TYPE_SLOW_PLAYER:
                effect = PowerUpEffect.createEffect(x, y, "SPEED REDUCED!", 
                        com.badlogic.gdx.graphics.Color.ORANGE, 2.0f);
                break;
        }

        if (effect != null) {
            entityManager.addEntity(effect);
        }
    }
    
    @Override
    public void updatePowerUpLabel(String text) {
        uiManager.updatePowerUpLabel(text);
    }
    
    @Override
    public IInputManager getInputManager() {
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