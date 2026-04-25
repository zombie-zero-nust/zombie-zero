package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Boss;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;

import java.util.List;
import java.util.function.Consumer;

public final class Level1EnemySpawnPoints
{
    private Level1EnemySpawnPoints() { }

    private static final List<SpawnPointDefinition> SPAWN_POINTS = List.of(
            SpawnPointDefinition.enabled(new Vector2D(100, 340), BasicEnemy.class),
            SpawnPointDefinition.enabled(new Vector2D(150, 340), BasicEnemy.class),
            SpawnPointDefinition.enabled(new Vector2D(200, 340), BasicEnemy.class),
            SpawnPointDefinition.enabled(new Vector2D(250, 340), BasicEnemy.class),
            SpawnPointDefinition.enabled(new Vector2D(300, 340), Boss.class)
    );

    public static void forEachSpawnPoint(Consumer<SpawnPointDefinition> action)
    {
        SPAWN_POINTS.forEach(point -> action.accept(point.copy()));
    }

    public record SpawnPointDefinition(Vector2D position, Class<? extends Enemy> enemyType, boolean enabled)
    {
        public SpawnPointDefinition { position = position.copy(); }

        public static SpawnPointDefinition enabled(Vector2D position, Class<? extends Enemy> enemyType)
        {
            return new SpawnPointDefinition(position, enemyType, true);
        }

        public static SpawnPointDefinition disabled(Vector2D position, Class<? extends Enemy> enemyType)
        {
            return new SpawnPointDefinition(position, enemyType, false);
        }

        public SpawnPointDefinition copy() { return new SpawnPointDefinition(position.copy(), enemyType, enabled); }
    }
}

