package edu.nust.game.systems.collision;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;

import java.util.HashSet;
import java.util.Set;

public class CollisionManager
{
    private final GameScene scene;
    private final Set<Concrete> concreteObjs = new HashSet<>();
    private final Set<Damaging> damagingObjs = new HashSet<>();
    private final Set<Damageable> damageableObjs = new HashSet<>();

    public CollisionManager(GameScene scene)
    {
        this.scene = scene;
    }

    private void getObjs()
    {
        concreteObjs.clear();
        damagingObjs.clear();
        damageableObjs.clear();

        for (GameObject obj : this.scene.getAllGameObjects())
        {
            if (obj instanceof Concrete)
            {
                concreteObjs.add((Concrete) obj);
            }

            if (obj instanceof Damaging)
            {
                damagingObjs.add((Damaging) obj);
            }

            if (obj instanceof Damageable)
            {
                damageableObjs.add((Damageable) obj);
            }
        }
    }

    public void manageCollisions()
    {
        getObjs();
        // first checks damageable objs
        for (Damageable obj : damageableObjs)
        {
            if (obj != null && !obj.isDead())
            {
                for (Damaging otherObj : damagingObjs)
                {
                    if (otherObj != null && otherObj.getHitbox() != null)
                    {
                        if (otherObj.notDamageObj() != null)
                        {
                            if (!otherObj.notDamageObj().contains(obj.getClass()))
                            {
                                if (obj.getHitbox().isTouching(otherObj.getHitbox()))
                                {
                                    obj.getHitbox().setTouchingFalse();
                                    obj.takeDamage(otherObj.getDamage());
                                    otherObj.destroyThis();
                                }
                            }
                        }
                    }
                }
            }
        }
        // then check concrete objects collisions
        for (Concrete obj : concreteObjs)
        {
            if (obj != null)
            {
                for (Concrete otherObj : concreteObjs)
                {
                    if (otherObj != null)
                    {
                        if (obj.notInteractWith() != null)
                        {
                            if (!obj.notInteractWith().contains(otherObj.getClass()))
                            {
                                if (obj == otherObj || obj.getHitbox() == null || otherObj.getHitbox() == null)
                                    continue;
                                obj.getHitbox().setMin(otherObj.getHitbox());
                                if (obj.getHitbox().isTouching(otherObj.getHitbox()))
                                {
                                    obj.triggerCollisionEffect(otherObj);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
