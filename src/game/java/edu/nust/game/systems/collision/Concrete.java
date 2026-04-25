package edu.nust.game.systems.collision;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Concrete
{
    List<Class<? extends Concrete>> notInteractWith();

    void setHitbox();

    HitBox getHitbox();

    void triggerCollisionEffect(@Nullable Concrete collidedObj);
}
