package edu.nust.game.scenes.levelscene.gameobjects.player;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.math.TimeSpan;

public class HealthImplementation implements Health
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private static final int DEFAULT_HEALTH = 100;

    private int currentHealth;

    public HealthImplementation()
    {
        this.currentHealth = DEFAULT_HEALTH;
        logger.debug("Health initialized: {} HP", DEFAULT_HEALTH);
    }

    public HealthImplementation(int health)
    {
        this.currentHealth = health;
        logger.debug("Health initialized: {} HP", health);
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
        currentHealth = Math.min(currentHealth + amount, DEFAULT_HEALTH);
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
        return DEFAULT_HEALTH;
    }

    @Override
    public void update(TimeSpan deltaTime)
    {
    }
}


