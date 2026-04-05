package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.Score;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager
{
    private Enemy enemy;
    private final GameScene scene;
    private final Score score;

    public EnemyManager(GameScene scene, Score score, Enemy initialEnemy)
    {
        this.scene = scene;
        this.score = score;
        this.enemy = initialEnemy;
    }

    public void updateEnemyLogic(Vector2D playerPos)
    {
        if (enemy == null) return;

        enemy.setTargetPosition(playerPos);

        if (enemy.checkPlayerCollision(playerPos))
        {
            handlePlayerEnemyCollision();
            return;
        }

        checkBulletEnemyCollisions();
    }

    private void handlePlayerEnemyCollision()
    {
        score.setScore(score.getScore() - 2);

        if (score.getScore() < 0)
        {
            score.setScore(0);
        }
        else
        {
            respawnEnemy();
        }
    }

    private void checkBulletEnemyCollisions()
    {
        List<GameObject> allObjects = this.scene.getAllGameObjects();

        for (GameObject obj : new ArrayList<>(allObjects))
        {
            if (obj instanceof Bullet)
            {
                Bullet bullet = (Bullet) obj;

                if (bullet.isDestroyed())
                    continue;

                if (enemy.checkBulletCollision(bullet.getTransform().getPosition()))
                {
                    enemy.addHit();
                    bullet.destroy();

                    if (enemy.isDefeated())
                    {
                        score.setScore(score.getScore() + 3);
                        respawnEnemy();
                    }
                }
            }
        }
    }

    private void respawnEnemy()
    {
        scene.removeGameObject(enemy);
        enemy = new Enemy(getRandomEdgePosition(), 100);
        scene.addGameObject(enemy.addTag(EnemyTag.class));
    }

    private Vector2D getRandomEdgePosition()
    {
        Vector2D cameraPos = scene.getWorldCamera().getPosition();
        double canvasW = scene.getWorldLayer().getWidth();
        double canvasH = scene.getWorldLayer().getHeight();
        double zoom = scene.getWorldCamera().getZoom();

        double halfW = canvasW / 2.0 / zoom;
        double halfH = canvasH / 2.0 / zoom;

        double left = cameraPos.getX() - halfW;
        double right = cameraPos.getX() + halfW;
        double top = cameraPos.getY() - halfH;
        double bottom = cameraPos.getY() + halfH;

        int edge = (int)(Math.random() * 4);
        double x, y;

        switch(edge) {
            case 0:
                x = left + Math.random() * (right - left);
                y = top;
                break;
            case 1:
                x = left + Math.random() * (right - left);
                y = bottom;
                break;
            case 2:
                x = left;
                y = top + Math.random() * (bottom - top);
                break;
            case 3:
                x = right;
                y = top + Math.random() * (bottom - top);
                break;
            default:
                x = 0;
                y = 0;
        }

        return new Vector2D(x, y);
    }

    public boolean isPlayerColliding(Vector2D playerPos)
    {
        return enemy != null && enemy.checkPlayerCollision(playerPos);
    }

    public Enemy getEnemy()
    {
        return enemy;
    }
}

