package edu.nust.game.gameobjects.interfaces;

public interface Damageable {
    void takeDamage(int damage);
    void setHealth(Health health);
    Health getHealth();
    boolean isDead();
}
