package edu.nust.game.systems.collision;

import java.util.List;

public interface Damaging
{
    List<Class<? extends Damageable>> notDamageObj();

    int getDamage();

    boolean isDestroyable();

    HitBox getHitbox();

    void destroyThis();
}
