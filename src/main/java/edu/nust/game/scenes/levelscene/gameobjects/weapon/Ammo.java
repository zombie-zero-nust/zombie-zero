package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.math.TimeSpan;

public interface Ammo
{
    void decreaseAmmo();

    void increaseAmmo(int amount);

    void startReload();

    boolean isReloading();

    boolean hasAmmo();

    int getCurrentAmmo();

    double getReloadTimeRemaining();

    void update(TimeSpan deltaTime);

    void refillAmmo();
}

