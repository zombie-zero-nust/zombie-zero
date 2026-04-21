package edu.nust.game.systems.collision;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Vector2D;

import java.util.*;

public class CollisionManager
{
    private final GameScene scene;

    private final Set<Concrete> concreteObjs = new HashSet<>();
    private final Set<Damaging> damagingObjs = new HashSet<>();
    private final Set<Damageable> damageableObjs = new HashSet<>();


    private final Map<Long, List<GameObject>> grid = new HashMap<>();

    private final int cellSize;

    public CollisionManager(GameScene scene, int cellSize)
    {
        this.scene = scene;
        this.cellSize = cellSize;
    }

    private long cellKey(int cellX, int cellY)
    {
        return (((long) cellX) << 32) ^ (cellY & 0xffffffffL);
    }

    private int worldToCell(double value)
    {
        return (int) Math.floor(value / cellSize);
    }

    private void clearAll()
    {
        concreteObjs.clear();
        damagingObjs.clear();
        damageableObjs.clear();
        grid.clear();
    }

    private void insertIntoGrid(GameObject obj)
    {
        if (obj == null || obj.getTransform() == null) return;

        Vector2D pos = obj.getTransform().getPosition();

        int cellX = worldToCell(pos.getX());
        int cellY = worldToCell(pos.getY());

        long key = cellKey(cellX, cellY);

        grid.computeIfAbsent(key, k -> new ArrayList<>()).add(obj);
    }

    private void getObjs()
    {
        clearAll();

        for (GameObject obj : scene.getAllGameObjects())
        {
            if (obj == null) continue;

            insertIntoGrid(obj);

            if (obj instanceof Concrete)
                concreteObjs.add((Concrete) obj);

            if (obj instanceof Damaging)
                damagingObjs.add((Damaging) obj);

            if (obj instanceof Damageable)
                damageableObjs.add((Damageable) obj);
        }
    }

    private List<GameObject> getNearbyObjects(GameObject obj)
    {
        Vector2D pos = obj.getTransform().getPosition();

        int cellX = worldToCell(pos.getX());
        int cellY = worldToCell(pos.getY());

        List<GameObject> nearby = new ArrayList<>();


        for (int dx = -1; dx <= 1; dx++)
        {
            for (int dy = -1; dy <= 1; dy++)
            {
                long key = cellKey(cellX + dx, cellY + dy);
                List<GameObject> cellObjects = grid.get(key);

                if (cellObjects != null)
                    nearby.addAll(cellObjects);
            }
        }

        return nearby;
    }

    public void manageCollisions()
    {
        getObjs();

        for (Damageable obj : damageableObjs)
        {
            if (obj == null || obj.isDead() || obj.getHitbox() == null)
                continue;

            GameObject objAsGO = (GameObject) obj;

            for (GameObject near : getNearbyObjects(objAsGO))
            {
                if (!(near instanceof Damaging otherObj)) continue;

                if (otherObj.getHitbox() == null) continue;

                if (otherObj.notDamageObj() != null && otherObj.notDamageObj().contains(obj.getClass())) continue;

                if (obj.getHitbox().isTouching(otherObj.getHitbox()))
                {
                    obj.getHitbox().setTouchingFalse();
                    obj.takeDamage(otherObj.getDamage());
                    otherObj.destroyThis();
                }
            }
        }

        for (Concrete obj : concreteObjs)
        {
            if (obj == null || obj.getHitbox() == null) continue;

            GameObject objAsGO = (GameObject) obj;

            for (GameObject near : getNearbyObjects(objAsGO))
            {
                if (!(near instanceof Concrete otherObj)) continue;

                if (otherObj == obj || otherObj.getHitbox() == null) continue;

                if (obj.notInteractWith() != null &&
                        obj.notInteractWith().contains(otherObj.getClass()))
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