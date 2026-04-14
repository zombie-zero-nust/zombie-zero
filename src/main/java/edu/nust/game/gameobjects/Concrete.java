package edu.nust.game.gameobjects;

public interface Concrete {
    String[] notInteractWith();
    void setHitbox();
    HitBox getHitbox();
    void triggerCollisionEffect();
}
