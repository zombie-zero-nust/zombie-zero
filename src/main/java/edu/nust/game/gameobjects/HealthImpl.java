package edu.nust.game.gameobjects;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.logger.GameLogger;

public class HealthImpl implements Health
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private static final int MAX_HEALTH = 100;

    private int currentHealth;

    public HealthImpl()
    {
        this.currentHealth = MAX_HEALTH;
        logger.debug("Health initialized: {} HP", MAX_HEALTH);
    }

    @Override
    public void takeDamage(int damage)
    {
        currentHealth = Math.max(0, currentHealth - damage);
        logger.debug("Damage taken: {} HP. Health: {}", damage, currentHealth);
    }

    @Override
    public void heal(int amount)
    {
        currentHealth = Math.min(currentHealth + amount, MAX_HEALTH);
        logger.debug("Healed: {} HP. Health: {}", amount, currentHealth);
    }

    @Override
    public boolean isAlive()
    {
        return currentHealth > 0;
    }

    @Override
    public int getCurrentHealth()
    {
        return currentHealth;
    }

    @Override
    public int getMaxHealth()
    {
        return MAX_HEALTH;
    }

    @Override
    public void update(TimeSpan deltaTime)
    {
    }
}


