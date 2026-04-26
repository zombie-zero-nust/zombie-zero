package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Boss;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.MiniBoss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public final class Level1SpawnPoints
{
    private static final Vector2D BASIC_ENEMY_SIZE = new Vector2D(12, 16);
    private static final Vector2D LARGE_ENEMY_SIZE = new Vector2D(24, 36);

    private Level1SpawnPoints() { }

    public static final Vector2D PLAYER_SPAWN_POINT = new Vector2D(40, 50);

    // disallow spawning in spawn area
    private static final Rectangle NON_SPAWNABLE_AREA = new Rectangle(0, 0, 110, 200);
    private static final double SPAWN_GRID_STEP = 40;
    private static final double RANDOM_SPAWN_OFFSET = 7;
    private static final double SPAWN_CHANCE = 0.7;
    private static final double SPAWN_PADDING = 2;
    private static final double SPAWN_MOVE_CHECK_DISTANCE = 12;


    private static final List<SpawnPoint> BOSS_SPAWN_POINTS = List.of(
            SpawnPoint.enabled(
                    new Vector2D(2200, 100),
                    Boss.class
            ),
            SpawnPoint.enabled(
                    new Vector2D(800, 740),
                    MiniBoss.class
            )
    );

    public static void forEachEnemySpawnPoint(Consumer<SpawnPoint> action)
    {
        generateEnemySpawnPoints().forEach(point -> action.accept(point.copy()));
    }

    private static List<SpawnPoint> generateEnemySpawnPoints()
    {
        List<Vector2D> positions = generateWalkablePositions();
        List<SpawnPoint> points = new ArrayList<>(positions.size() + BOSS_SPAWN_POINTS.size());
        Level1CollisionMask collisionMask = new Level1CollisionMask();

        for (Vector2D position : positions)
        {
            points.add(SpawnPoint.enabled(position, BasicEnemy.class));
        }

        for (SpawnPoint position : BOSS_SPAWN_POINTS)
        {
            if (isValidSpawnPoint(position.position(), position.enemyType(), collisionMask))
                points.add(SpawnPoint.enabled(position.position(), position.enemyType()));
        }

        return points;
    }

    private static List<Vector2D> generateWalkablePositions()
    {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        Level1CollisionMask collisionMask = new Level1CollisionMask();

        Rectangle spawnableArea = Level1CollisionMask.getMapBounds();

        List<Vector2D> candidates = new ArrayList<>();
        for (double y = spawnableArea.getTop(); y < spawnableArea.getBottom(); y += SPAWN_GRID_STEP)
        {
            for (double x = spawnableArea.getLeft(); x < spawnableArea.getRight(); x += SPAWN_GRID_STEP)
            {
                double offsetX = random.nextDouble(-RANDOM_SPAWN_OFFSET, RANDOM_SPAWN_OFFSET);
                double offsetY = random.nextDouble(-RANDOM_SPAWN_OFFSET, RANDOM_SPAWN_OFFSET);
                Vector2D point = new Vector2D(x, y).add(offsetX, offsetY);
                if (!NON_SPAWNABLE_AREA.contains(point) // not in spawn area
                        && isValidSpawnPoint(point, BasicEnemy.class, collisionMask) && // valid spawn
                        random.nextDouble() < SPAWN_CHANCE)
                {
                    candidates.add(point);
                }
            }
        }

        // shuffle to
        Collections.shuffle(candidates, random);
        return candidates;
    }

    private static boolean isValidSpawnPoint(Vector2D point, Class<? extends Enemy> enemyType, Level1CollisionMask collisionMask)
    {
        Rectangle spawnFootprint = getRequiredSize(point, enemyType);
        if (!collisionMask.isWalkable(spawnFootprint)) return false;

        for (Vector2D offset : getMovementCheckOffsets())
        {
            Rectangle movedFootprint = getRequiredSize(point.add(offset), enemyType);
            if (collisionMask.isWalkable(movedFootprint)) return true;
        }

        return false;
    }

    private static Rectangle getRequiredSize(Vector2D point, Class<? extends Enemy> enemyType)
    {
        Rectangle area = Rectangle.fromCenter(point, getEnemySize(enemyType));
        return area.grown(SPAWN_PADDING, SPAWN_PADDING);
    }

    private static Vector2D getEnemySize(Class<? extends Enemy> enemyType)
    {
        if (enemyType == Boss.class || enemyType == MiniBoss.class) return LARGE_ENEMY_SIZE;

        return BASIC_ENEMY_SIZE;
    }

    private static List<Vector2D> getMovementCheckOffsets()
    {
        return List.of(
                new Vector2D(SPAWN_MOVE_CHECK_DISTANCE, 0),
                new Vector2D(-SPAWN_MOVE_CHECK_DISTANCE, 0),
                new Vector2D(0, SPAWN_MOVE_CHECK_DISTANCE),
                new Vector2D(0, -SPAWN_MOVE_CHECK_DISTANCE)
        );
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

