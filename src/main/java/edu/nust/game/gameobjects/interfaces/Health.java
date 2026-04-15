package edu.nust.game.gameobjects.interfaces;

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

