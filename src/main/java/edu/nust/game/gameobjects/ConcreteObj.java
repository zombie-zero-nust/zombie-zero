package edu.nust.game.gameobjects;

public interface ConcreteObj {
    void setHitbox();
    HitBox getHitbox();
    void triggerCollisionEffect();
    void restrictMovement();
}
