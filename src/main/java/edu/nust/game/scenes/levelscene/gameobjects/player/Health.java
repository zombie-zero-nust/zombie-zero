package edu.nust.game.scenes.levelscene.gameobjects.player;

import edu.nust.engine.math.TimeSpan;

public interface Health
{
    void takeDamage(int damage);

    void heal(int amount);

    boolean isAlive();

    int getCurrentHealth();

    int getMaxHealth();

    void update(TimeSpan deltaTime);
}

