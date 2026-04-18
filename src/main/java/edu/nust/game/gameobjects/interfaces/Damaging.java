package edu.nust.game.gameobjects.interfaces;

import edu.nust.game.gameobjects.CollisionSystem.HitBox;
import java.util.List;

public interface Damaging {
    List<Class<? extends Damageable>> notDamageObj();
    int getDamage();
    boolean isDestroyable();
    HitBox getHitbox();
    void destroyThis();
}
