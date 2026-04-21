package edu.nust.game.scenes.levelscene.gameobjects.enemy;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;

import java.util.ArrayList;

public class EnemySpawner extends GameObject
{
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final Vector2D pos;

    private final int totalEnemies;
    private final double spawnTime;
    private boolean spawnActive = false;
    private TimeSpan elapsed = TimeSpan.zero();

    public EnemySpawner(int totalEnemies, double spawnTime, Vector2D pos)
    {
        this.totalEnemies = totalEnemies;
        this.spawnTime = spawnTime;
        this.pos = pos;
    }

    @Override
    public void onInit()
    {
        setEnemies(totalEnemies);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        updateStatus(this.getScene());
        if (isSpawnActive())
        {
            if (!enemies.isEmpty())
            {
                spawn(this.getScene(), deltaTime);
            }
            else
            {
                this.destroy();
            }
        }
    }

    private void setEnemies(int enemies)
    {
        for (int i = 0; i < enemies; i++)
        {
            this.enemies.add(new Enemy(pos, 30, 100));
        }
    }


    public boolean isSpawnActive()
    {
        return spawnActive;
    }

    public void setSpawnActive(boolean spawnActive)
    {
        this.spawnActive = spawnActive;
    }

    public void spawn(GameScene scene, TimeSpan deltaTime)
    {
        elapsed = elapsed.add(deltaTime);
        if (elapsed.asSeconds() >= spawnTime)
        {
            scene.addGameObject(enemies.getFirst());
            enemies.removeFirst();
            elapsed = elapsed.subtract(TimeSpan.fromSeconds(spawnTime));
        }
    }

    public void updateStatus(GameScene scene)
    {
        Player player = (Player) scene.getFirstOfType(Player.class);
        if (player != null)
        {
            Vector2D playerPos = player.getTransform().getPosition();
            if (playerPos.subtract(this.pos).magnitude() < 150)
            {
                setSpawnActive(true);
            }
        }
    }
}
