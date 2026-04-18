package edu.nust.game.gameobjects.interfaces;

import edu.nust.game.gameobjects.CollisionSystem.HitBox;

public interface Damaging {
    String[] notInteractWith();
    int getDamage();
    boolean isDestroyable();
    HitBox getHitbox();
    void destroy(boolean isDestroyable);
}
