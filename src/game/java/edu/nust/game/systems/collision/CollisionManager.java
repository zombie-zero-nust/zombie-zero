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

    private final Set<Damaging> destroyQueue = new HashSet<>();

    public CollisionManager(GameScene scene)
    {
        this.scene = scene;
    }

    private void getObjs()
    {
        concreteObjs.clear();
        damagingObjs.clear();
        damageableObjs.clear();

        for (GameObject obj : scene.getAllGameObjects())
        {
            if (obj == null) continue;

            if (obj instanceof Concrete)
                concreteObjs.add((Concrete) obj);

            if (obj instanceof Damaging)
                damagingObjs.add((Damaging) obj);

            if (obj instanceof Damageable)
                damageableObjs.add((Damageable) obj);
        }
    }

    public void manageCollisions()
    {
        getObjs();

        for (Damageable obj : damageableObjs)
        {
            // CAST to GameObject to check identity
            GameObject targetObj = (GameObject) obj;

            if (obj == null || obj.isDead() || obj.getHitbox() == null)
                continue;

            for (Damaging otherObj : damagingObjs)
            {
                GameObject attackerObj = (GameObject) otherObj;

                if (targetObj == attackerObj) continue;

                if (otherObj == null || otherObj.getHitbox() == null)
                    continue;


                if (otherObj.notDamageObj() != null) {
                    boolean shouldSkip = false;
                    for (Class<?> clazz : otherObj.notDamageObj()) {
                        if (clazz.isInstance(obj)) {
                            shouldSkip = true;
                            break;
                        }
                    }
                    if (shouldSkip) continue;
                }

                if (obj.getHitbox().isTouching(otherObj.getHitbox()))
                {
                    obj.takeDamage(otherObj.getDamage());
                    // Only queue for destruction if it's actually meant to be destroyed (like a bullet)
                    if (otherObj.isDestroyable()) {
                        destroyQueue.add(otherObj);
                    }
                }
            }
        }

        for (Concrete obj : concreteObjs)
        {
            if (obj == null || obj.getHitbox() == null)
                continue;

            for (Concrete otherObj : concreteObjs)
            {
                if (otherObj == null || otherObj == obj || otherObj.getHitbox() == null)
                    continue;

                if (obj.notInteractWith() != null) {
                    boolean skip = false;
                    for (Class<?> ignoredClass : obj.notInteractWith()) {
                        if (ignoredClass.isInstance(otherObj)) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip) continue;
                }
                obj.getHitbox().setMin(otherObj.getHitbox());

                if (obj.getHitbox().isTouching(otherObj.getHitbox()))
                {


                    obj.triggerCollisionEffect(otherObj);
                }
            }
        }


        for (Damaging obj : destroyQueue)
        {
            obj.destroyThis();
        }
        destroyQueue.clear();
    }
}