package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;

public interface DamageableObj {
    void takeDamage(int damage);
    void setHealth(Health health);
    Health getHealth();
    boolean isDead();
}
