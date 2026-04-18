package edu.nust.game.gameobjects.Enemy;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.assets.EnemyAsset;
import edu.nust.game.gameobjects.General.HealthImpl;
import edu.nust.game.gameobjects.CollisionSystem.HitBox;
import edu.nust.game.gameobjects.enums.EnemyConfig;
import edu.nust.game.gameobjects.interfaces.Concrete;
import edu.nust.game.gameobjects.interfaces.Damageable;
import edu.nust.game.gameobjects.interfaces.Damaging;
import edu.nust.game.gameobjects.interfaces.Health;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.List;

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


    public Enemy(Vector2D startPosition, double speed,int health)
    {
        this(startPosition, speed, EnemyAsset.ZOMBIE_SMALL,health);
    }


    public Enemy(Vector2D startPosition, double speed, EnemyAsset enemyType,int health)
    {
        this.health = new HealthImpl();
        this.movementSpeed = speed;
        this.width = EnemyConfig.DEFAULT_SIZE.getValue();
        this.height = EnemyConfig.DEFAULT_SIZE.getValue();
        this.enemyType = enemyType;
        this.getTransform().setPosition(startPosition);
        this.health = new HealthImpl(health);

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
        setHitbox();
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        setHitbox();
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
        double movementDistance = movementSpeed * deltaTime.asSeconds();
        double dx=0,dy=0;

        double distanceToTarget = directionToTarget.magnitude();
        if(directionToTarget.getX()<0 && !this.hitbox.isLeftTouching()){
            dx -= movementDistance;
        }
        else if(directionToTarget.getX()>0 && !this.hitbox.isRightTouching()){
            dx += movementDistance;
        }
        if(directionToTarget.getY()>0 && !this.hitbox.isBottomTouching()){
            dy += movementDistance;
        }
        else if(directionToTarget.getY()<0 && !this.hitbox.isTopTouching()){
            dy -= movementDistance;
        }

        if (distanceToTarget > 0)
        {
            Vector2D movement = new Vector2D(dx,dy);

            if(dx != 0 && dy!= 0) movement.multiply(0.707);

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
        if (hitbox == null) {
            hitbox = new HitBox(this.getTransform().getPosition(), height, width);
            this.addComponent(hitbox);
        }
    }

    @Override
    public HitBox getHitbox(){
        if(hitbox == null){

            setHitbox();
        }
        return hitbox;
    }

    @Override
    public void triggerCollisionEffect(Concrete collidedObj){
        double dx=0,dy=0;
        if(hitbox.isLeftTouching()){
            dx += 1.0;
        }
        if(hitbox.isRightTouching()){
            dx -= 1.0;
        }
        if(hitbox.isTopTouching()){
            dy += 1.0;
        }
        if(hitbox.isBottomTouching()){
            dy -= 1.0;
        }
        this.getTransform().setPosition(this.getTransform().getPosition().add(dx,dy));
    }

    @Override
    public int getDamage(){
        return this.damage;
    }
    @Override
    public boolean isDestroyable(){
        return false;
    }

    @Override
    public void destroyThis(){ if(isDestroyable()) this.destroy();}

    @Override
    public List<Class<? extends Concrete>> notInteractWith(){
        return List.of(Enemy.class);
    }

    @Override
    public List<Class<?extends Damageable>> notDamageObj(){
        return List.of(Enemy.class);
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