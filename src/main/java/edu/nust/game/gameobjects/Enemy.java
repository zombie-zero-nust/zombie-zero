package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.assets.EnemyAsset;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public class Enemy extends GameObject
{
    private Vector2D targetPosition = Vector2D.zero();
    private double movementSpeed;
    private double size;
    private int hitCount = 0;
    private EnemyAsset enemyType;
    private SpriteRenderer spriteRenderer;

    public Enemy(Vector2D startPosition, double speed)
    {
        this(startPosition, speed, EnemyAsset.ZOMBIE_SMALL);
    }

    public Enemy(Vector2D startPosition, double speed, EnemyAsset enemyType)
    {
        this.movementSpeed = speed;
        this.size = EnemyConfig.DEFAULT_SIZE.getValue();
        this.enemyType = enemyType;
        this.getTransform().setPosition(startPosition);

        try
        {
            // Load enemy sprite from PostApocalypse assets
            Image idleSprite = Resources.loadImageOrThrow(
                "assets",
                enemyType.getPath(),
                "Zombie_Small_Down_Idle-Sheet6.png"
            );

            // Create sprite renderer with 6 frames
            spriteRenderer = new SpriteRenderer(size, size, idleSprite, 6, 1);
            spriteRenderer.setAnimationTime(TimeSpan.fromMilliseconds(150))
                         .startAnimation();

            this.addComponent(spriteRenderer);
        }
        catch (FileNotFoundException e)
        {
            // If sprite loading fails, still continue
            System.err.println("Failed to load enemy sprite: " + e.getMessage());
        }
    }

    @Override
    public void onInit()
    {
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        moveTowardsTarget(deltaTime);
    }

    public void setTargetPosition(Vector2D target)
    {
        this.targetPosition.set(target);
    }

    private void moveTowardsTarget(TimeSpan deltaTime)
    {
        Vector2D currentPosition = this.getTransform().getPosition();
        Vector2D directionToTarget = Vector2D.subtract(targetPosition, currentPosition);
        double distanceToTarget = directionToTarget.magnitude();

        if (distanceToTarget > 0)
        {
            directionToTarget = directionToTarget.normalize();
            double movementDistance = movementSpeed * deltaTime.asSeconds();
            Vector2D movement = Vector2D.multiply(directionToTarget, movementDistance);
            Vector2D newPosition = currentPosition.add(movement);

            this.getTransform().setPosition(newPosition);

            double angleToTarget = Math.atan2(directionToTarget.getY(), directionToTarget.getX());
            this.getTransform().setRotationRadians(angleToTarget);
        }
    }

    public double getMovementSpeed()
    {
        return movementSpeed;
    }

    public void setMovementSpeed(double speed)
    {
        this.movementSpeed = speed;
    }

    public int getHitCount()
    {
        return hitCount;
    }

    public void addHit()
    {
        this.hitCount++;
    }

    public void resetHitCount()
    {
        this.hitCount = 0;
    }

    public boolean checkPlayerCollision(Vector2D playerPos)
    {
        double distance = Vector2D.subtract(playerPos, this.getTransform().getPosition()).magnitude();
        return distance < EnemyConfig.ENEMY_ATTACK_DISTANCE.getValue();
    }

    public boolean checkBulletCollision(Vector2D bulletPos)
    {
        double distance = Vector2D.subtract(bulletPos, this.getTransform().getPosition()).magnitude();
        return distance < EnemyConfig.BULLET_COLLISION_DISTANCE.getValue();
    }

    public boolean isDefeated()
    {
        return hitCount >= EnemyConfig.HITS_TO_DEFEAT.getIntValue();
    }
}