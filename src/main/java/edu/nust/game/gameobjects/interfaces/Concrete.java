package edu.nust.game.gameobjects.interfaces;

import edu.nust.game.gameobjects.HitBox;

public interface Concrete {
    String[] notInteractWith();
    void setHitbox();
    HitBox getHitbox();
    void triggerCollisionEffect();
}
