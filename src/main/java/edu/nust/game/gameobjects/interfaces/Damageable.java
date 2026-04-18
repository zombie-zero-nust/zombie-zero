package edu.nust.game.gameobjects.interfaces;

import edu.nust.game.gameobjects.CollisionSystem.HitBox;

public interface Damageable {
    void takeDamage(int damage);
    void setHealth(Health health);
    Health getHealth();
    HitBox getHitbox();
    boolean isDead();
}
