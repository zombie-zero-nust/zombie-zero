package edu.nust.game.systems.collision;

import java.util.List;

public interface Concrete
{
    List<Class<? extends Concrete>> notInteractWith();

    void setHitbox();

    HitBox getHitbox();

    void triggerCollisionEffect(Concrete collidedObj);
}
