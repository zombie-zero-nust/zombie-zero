package edu.nust.game.scenes.levelscene.gameobjects.player;

import edu.nust.engine.logger.GameLogger;

public class Health
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private static final int DEFAULT_HEALTH = 100;

    private int currentHealth;
    private final int maxHealth;

    public Health()
    {
        this.maxHealth = DEFAULT_HEALTH;
        this.currentHealth = DEFAULT_HEALTH;
        logger.debug("Health initialized: {} HP", DEFAULT_HEALTH);
    }

    public Health(int health)
    {
        this.maxHealth = Math.max(1, health);
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
        currentHealth = Math.min(currentHealth + amount, maxHealth);
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
        return maxHealth;
    }

    public double getHealthRatio()
    {
        return Math.clamp((double) currentHealth / maxHealth, 0.0, 1.0);
    }

    public Health setCurrentHealth(int currentHealth)
    {
        this.currentHealth = currentHealth;
        return this;
    }
}
