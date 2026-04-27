package edu.nust.game.systems.collision;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Boss;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.MiniBoss;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.weapon.Bullet;
import edu.nust.game.scenes.levelscene.level_1.Level1CollisionMask;

import java.util.HashSet;
import java.util.Set;

public class CollisionManager
{
    private static final int POINTS_PER_ZOMBIE_KILL = 3;
    private static final int POINTS_PER_MINIBOSS_KILL = 8;
    private static final int POINTS_PER_BOSS_KILL = 15;
    private static final double BULLET_HIT_FOLLOW_RADIUS_FACTOR = 2.5;

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
                    boolean wasAlive = !obj.isDead();
                    obj.takeDamage(otherObj.getDamage());

                    if (obj instanceof Enemy enemy && otherObj instanceof Bullet)
                    {
                        enemy.multiplyFollowRadius(BULLET_HIT_FOLLOW_RADIUS_FACTOR);
                        if (scene instanceof LevelScene levelScene)
                            levelScene.shakeOnEnemyHit();
                    }

                    if (obj instanceof Player && scene instanceof LevelScene levelScene)
                    {
                        levelScene.shakeOnPlayerHit();
                    }

                    // Award kill score exactly when an enemy transitions from alive to dead.
                    if (wasAlive && obj.isDead() && obj instanceof Enemy)
                    {
                        awardKillPoints((Enemy) obj);
                        break;
                    }

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

            if (collidesWithLevelRect(obj))
            {
                obj.triggerCollisionEffect(null);
                continue;
            }

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

    private static boolean collidesWithLevelRect(Concrete obj)
    {
        if (obj.getHitbox() == null)
            return false;

        for (var levelRect : Level1CollisionMask.getAllCollisionRects())
        {
            if (obj.getHitbox().asRect().intersects(levelRect))
                return true;
        }

        return false;
    }

    private void awardKillPoints(Enemy enemy)
    {
        if (scene instanceof LevelScene levelScene)
        {
            if (enemy instanceof Boss)
            {
                if(((Boss) (enemy)).isResurrected() && ((Boss)enemy).getIsDead()) {
                    levelScene.addScorePoints(POINTS_PER_BOSS_KILL);
                }
            }
            else if (enemy instanceof MiniBoss)
            {
                levelScene.addScorePoints(POINTS_PER_MINIBOSS_KILL);
            }
            else
            {
                levelScene.addScorePoints(POINTS_PER_ZOMBIE_KILL);
            }
        }
    }
}