package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;

import java.util.List;
import java.util.function.Consumer;

public final class Level1SpawnPoints
{
    private Level1SpawnPoints() { }

    public static final Vector2D PLAYER_SPAWN_POINT = new Vector2D(40, 50);

    private static final List<SpawnPoint> ENEMY_SPAWN_POINTS = List.of(
            // path (top left - bottom left)
            SpawnPoint.enabled(new Vector2D(90, 200), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 250), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 300), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 350), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 400), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 450), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 500), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 550), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 600), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 650), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 700), BasicEnemy.class),
            // bottom left section
            SpawnPoint.enabled(new Vector2D(40, 700), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(40, 725), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(40, 750), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 700), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 725), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(90, 750), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(140, 700), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(140, 725), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(140, 750), BasicEnemy.class),
            // path (center left - city)
            SpawnPoint.enabled(new Vector2D(90, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(140, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(190, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(240, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(290, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(340, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(390, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(440, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(490, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(540, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(590, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(640, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(690, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(740, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(790, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(840, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(890, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(940, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(990, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1040, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1090, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1140, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1190, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1240, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1290, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1340, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1390, 340), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1464, 340), BasicEnemy.class),
            // path (center city - top city enter)
            SpawnPoint.enabled(new Vector2D(1464, 300), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1464, 250), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1464, 200), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1464, 150), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1464, 100), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1464, 50), BasicEnemy.class),
            // city enter area (top center)
            SpawnPoint.enabled(new Vector2D(1500, 50), BasicEnemy.class),
            SpawnPoint.enabled(new Vector2D(1540, 50), BasicEnemy.class)
    );

    public static void forEachEnemySpawnPoint(Consumer<SpawnPoint> action)
    {
        ENEMY_SPAWN_POINTS.forEach(point -> action.accept(point.copy()));
    }

    public record SpawnPoint(Vector2D position, Class<? extends Enemy> enemyType, boolean enabled)
    {
        public SpawnPoint { position = position.copy(); }

        public static SpawnPoint enabled(Vector2D position, Class<? extends Enemy> enemyType)
        {
            return new SpawnPoint(position, enemyType, true);
        }

        public static SpawnPoint disabled(Vector2D position, Class<? extends Enemy> enemyType)
        {
            return new SpawnPoint(position, enemyType, false);
        }

        public SpawnPoint copy() { return new SpawnPoint(position.copy(), enemyType, enabled); }
    }
}

