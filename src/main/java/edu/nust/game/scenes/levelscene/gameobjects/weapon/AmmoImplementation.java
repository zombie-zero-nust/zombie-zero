package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.math.TimeSpan;

public class AmmoImplementation implements Ammo
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private static final int MAX_AMMO = 20;
    private static final double RELOAD_TIME = 10.0;

    private int currentAmmo;
    private boolean isReloading;
    private double reloadTimer;

    public AmmoImplementation()
    {
        this.currentAmmo = MAX_AMMO;
        this.isReloading = false;
        this.reloadTimer = 0.0;
        logger.debug("Ammo initialized: {} bullets", MAX_AMMO);
    }

    @Override
    public void decreaseAmmo()
    {
        if (currentAmmo > 0)
        {
            currentAmmo--;
            if (currentAmmo == 0)
                startReload();
        }
    }

    @Override
    public void increaseAmmo(int amount)
    {
        if (amount > 0)
            currentAmmo = Math.min(currentAmmo + amount, MAX_AMMO);
    }

    @Override
    public void startReload()
    {
        if (!isReloading)
        {
            isReloading = true;
            reloadTimer = RELOAD_TIME;
            logger.info("Reload started");
        }
    }

    @Override
    public boolean isReloading()
    {
        return isReloading;
    }

    @Override
    public boolean hasAmmo()
    {
        return currentAmmo > 0;
    }

    @Override
    public int getCurrentAmmo()
    {
        return currentAmmo;
    }

    @Override
    public double getReloadTimeRemaining()
    {
        return reloadTimer;
    }

    @Override
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

    @Override
    public void refillAmmo()
    {
        currentAmmo = MAX_AMMO;
    }
}

