package edu.nust.game.scenes.levelscene.gameobjects.player;

import edu.nust.engine.logger.GameLogger;

public class Health
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private static final int DEFAULT_HEALTH = 100;

    private int currentHealth;

    public Health()
    {
        this.currentHealth = DEFAULT_HEALTH;
        logger.debug("Health initialized: {} HP", DEFAULT_HEALTH);
    }

    public Health(int health)
    {
        this.currentHealth = health;
        logger.debug("Health initialized: {} HP", health);
    }

    public void takeDamage(int damage)
    {
        currentHealth = Math.max(0, currentHealth - damage);
        logger.debug("Damage taken: {} HP. Health: {}", damage, currentHealth);
    }

    public void heal(int amount)
    {
        currentHealth = Math.min(currentHealth + amount, DEFAULT_HEALTH);
        logger.debug("Healed: {} HP. Health: {}", amount, currentHealth);
    }

    public boolean isAlive()
    {
        return currentHealth > 0;
    }

    public int getCurrentHealth()
    {
        return currentHealth;
    }

    public int getMaxHealth()
    {
        return DEFAULT_HEALTH;
    }
}


