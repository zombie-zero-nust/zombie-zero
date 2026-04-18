package edu.nust.game.gameobjects.interfaces;

import edu.nust.game.gameobjects.CollisionSystem.HitBox;

public interface Concrete {
    String[] notInteractWith();
    void setHitbox();
    HitBox getHitbox();
    void triggerCollisionEffect();
}
