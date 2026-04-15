package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.assets.EnemyAsset;
import edu.nust.game.gameobjects.enums.EnemyConfig;
import edu.nust.game.gameobjects.interfaces.Concrete;
import edu.nust.game.gameobjects.interfaces.Damageable;
import edu.nust.game.gameobjects.interfaces.Damaging;
import edu.nust.game.gameobjects.interfaces.Health;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public class Enemy extends GameObject implements Concrete, Damageable, Damaging
{
    private Vector2D targetPosition = Vector2D.zero();
    private double movementSpeed;
    private double height;
    private double width;
    private int hits = 0;
    private EnemyAsset enemyType;
    private SpriteRenderer spriteRenderer;
    private HitBox hitbox;
    private int damage;
    private Health health;


    public Enemy(Vector2D startPosition, double speed)
    {
        this(startPosition, speed, EnemyAsset.ZOMBIE_SMALL);
    }

    public Enemy(Vector2D startPosition, double speed, EnemyAsset enemyType)
    {
        this.health = new HealthImpl();
        this.movementSpeed = speed;
        this.width = EnemyConfig.DEFAULT_SIZE.getValue();
        this.height = EnemyConfig.DEFAULT_SIZE.getValue();
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
            spriteRenderer = new SpriteRenderer(width, height, idleSprite, 6, 1);
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


    public void addHit()
    {
        this.hits++;
    }

    public void resetHitCount()
    {
        this.hits = 0;
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
        return hits >= EnemyConfig.HITS_TO_DEFEAT.getIntValue();
    }

    @Override
    public void setHitbox()
    {
        if (hitbox == null)
            hitbox = new HitBox(this.getTransform().getPosition(), height / 2.0, width / 2.0);
    }

    @Override
    public HitBox getHitbox(){
        return hitbox;
    }

    @Override
    public void triggerCollisionEffect(){}

    @Override
    public int getDamage(){
        return this.damage;
    }
    @Override
    public boolean isDestroyable(){
        return false;
    }

    @Override
    public void destroy(boolean isDestroyable){ if(isDestroyable) this.destroy();}

    @Override
    public String[] notInteractWith(){
        return new String[]{"Enemy"};
    }

    @Override
    public void takeDamage(int damage){
        health.takeDamage(damage);
    }

    @Override
    public void setHealth(Health health){
        this.health = health;
    }

    @Override
    public Health getHealth(){
        return health;
    }

    @Override
    public boolean isDead(){
        if(health.isAlive()) return false;
        return true;
    }

}