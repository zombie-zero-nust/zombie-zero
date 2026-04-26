package edu.nust.game.scenes.levelscene.gameobjects.enemy.spawner;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects._tags.EnemyTag;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Boss;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;

import java.lang.reflect.Constructor;

public class EnemySpawnPointGameObject extends GameObject
{
    private static final double DEFAULT_BOSS_SPEED = 30;
    private static final int DEFAULT_BOSS_HEALTH = 1000;
    private static final double DEFAULT_BOSS_DAMAGE = 20;

    private final Vector2D spawnPosition;
    private final Class<? extends Enemy> enemyType;
    private final Vector2D cameraViewGrowth;

    private boolean spawnEnabled = true;
    private boolean spawned = false;

    public EnemySpawnPointGameObject(Vector2D spawnPosition, Class<? extends Enemy> enemyType, Vector2D cameraViewGrowth)
    {
        this.spawnPosition = spawnPosition.copy();
        this.enemyType = enemyType;
        this.cameraViewGrowth = cameraViewGrowth.copy();
    }

    @Override
    public void onInit()
    {
        this.getTransform().setPosition(spawnPosition);
        this.setVisible(false);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (!spawnEnabled || spawned) return;

        Rectangle activationRect = getExpandedCameraRect();
        if (activationRect == null) return;

        if (!activationRect.contains(spawnPosition)) return;

        Enemy enemy = createEnemyInstance();
        this.getScene().addGameObject(enemy.addTag(EnemyTag.class));
        spawned = true;
        this.destroy();
    }

    public boolean isSpawnEnabled()
    {
        return spawnEnabled;
    }

    public void setSpawnEnabled(boolean spawnEnabled)
    {
        this.spawnEnabled = spawnEnabled;
    }

    private Rectangle getExpandedCameraRect()
    {
        GameScene scene = this.getScene();
        if (scene == null || scene.getWorldCamera() == null) return null;

        double zoom = scene.getWorldCamera().getZoom();
        if (zoom <= 0) return null;

        double worldWidth = scene.getWorldLayer().getWidth() / zoom;
        double worldHeight = scene.getWorldLayer().getHeight() / zoom;
        Vector2D cameraCenter = scene.getWorldCamera().getPosition();

        return Rectangle.fromCenter(cameraCenter, new Vector2D(worldWidth, worldHeight)).grown(cameraViewGrowth);
    }

    private Enemy createEnemyInstance()
    {
        if (enemyType == Boss.class)
        {
            return new Boss(spawnPosition.copy(), DEFAULT_BOSS_SPEED, DEFAULT_BOSS_HEALTH, DEFAULT_BOSS_DAMAGE);
        }

        if (enemyType == BasicEnemy.class)
        {
            return new BasicEnemy(spawnPosition.copy());
        }

        // Fallback for enemy classes that expose a Vector2D constructor.
        try
        {
            Constructor<? extends Enemy> ctor = enemyType.getDeclaredConstructor(Vector2D.class);
            ctor.setAccessible(true);
            return ctor.newInstance(spawnPosition.copy());
        }
        catch (Exception e)
        {
            throw new IllegalStateException(
                    "Failed to instantiate enemy type " + enemyType.getSimpleName() + " at " + spawnPosition,
                    e
            );
        }
    }
}

