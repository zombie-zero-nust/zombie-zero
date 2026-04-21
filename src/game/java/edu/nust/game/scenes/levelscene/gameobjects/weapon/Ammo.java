package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.math.TimeSpan;

public class Ammo
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    // TODO: Change after development
    private static final int MAX_AMMO = 50;
    private static final double RELOAD_TIME = 2.0;

    private int currentAmmo;
    private boolean isReloading;
    private double reloadTimer;

    public Ammo()
    {
        this.currentAmmo = MAX_AMMO;
        this.isReloading = false;
        this.reloadTimer = 0.0;
        logger.debug("Ammo initialized: {} bullets", MAX_AMMO);
    }

    public void decreaseAmmo()
    {
        if (currentAmmo > 0)
        {
            currentAmmo--;
            if (currentAmmo == 0) startReload();
        }
    }

    public void increaseAmmo(int amount)
    {
        if (amount > 0) currentAmmo = Math.min(currentAmmo + amount, MAX_AMMO);
    }

    public void startReload()
    {
        if (!isReloading)
        {
            isReloading = true;
            reloadTimer = RELOAD_TIME;
            logger.info("Reload started");
        }
    }

    public boolean isReloading()
    {
        return isReloading;
    }

    public boolean hasAmmo()
    {
        return currentAmmo > 0;
    }

    public int getCurrentAmmo()
    {
        return currentAmmo;
    }

    public int getMaxAmmo() { return MAX_AMMO; }

    public void setCurrentAmmo(int amount)
    {
        currentAmmo = Math.clamp(amount, 0, MAX_AMMO);
        // Direct dev set should take effect immediately.
        isReloading = false;
        reloadTimer = 0.0;
    }

    public double getReloadTimeRemaining()
    {
        return reloadTimer;
    }

    public void update(TimeSpan deltaTime)
    {
        if (isReloading)
        {
            reloadTimer -= deltaTime.asSeconds();

            if (reloadTimer <= 0.0)
            {
                isReloading = false;
                reloadTimer = 0.0;
                refillAmmo();
                logger.info("Reload complete");
            }
        }
    }

    public void refillAmmo()
    {
        currentAmmo = MAX_AMMO;
    }
}

