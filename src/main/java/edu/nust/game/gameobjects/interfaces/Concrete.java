package edu.nust.game.gameobjects.interfaces;

import java.util.List;

import edu.nust.engine.core.GameObject;
import edu.nust.game.gameobjects.CollisionSystem.HitBox;

public interface Concrete {
    List<Class<? extends Concrete>> notInteractWith();
    void setHitbox();
    HitBox getHitbox();
    void triggerCollisionEffect(Concrete collidedObj);
}
