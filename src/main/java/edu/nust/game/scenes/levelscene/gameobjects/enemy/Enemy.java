package edu.nust.game.scenes.levelscene.gameobjects.enemy;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.player.Health;
import edu.nust.game.scenes.levelscene.gameobjects.player.HealthImplementation;
import edu.nust.game.systems.assets.EnemyAsset;
import edu.nust.game.systems.collision.Concrete;
import edu.nust.game.systems.collision.Damageable;
import edu.nust.game.systems.collision.Damaging;
import edu.nust.game.systems.collision.HitBox;
import edu.nust.game.systems.pathfinder.Node;
import edu.nust.game.systems.pathfinder.PathFinder;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Enemy extends GameObject implements Concrete, Damageable, Damaging
{
    private PathFinder pathFinder;
    private Vector2D targetPosition = Vector2D.zero();
    private double movementSpeed;// nodes per second
    private double height;
    private double width;
    private int hits = 0;
    private EnemyAsset enemyType;
    private SpriteRenderer spriteRenderer;
    private HitBox hitbox;
    private int damage;
    private Health health;
    private ArrayList<Node> movement;


    public Enemy(Vector2D startPosition, double speed, int health)
    {
        this(startPosition, speed, EnemyAsset.ZOMBIE_SMALL, health);
    }

    public Enemy(Vector2D startPosition, double speed, int health, double height, double width, double damage, EnemyAsset enemyType)
    {
        this.health = new HealthImplementation();
        this.movementSpeed = speed;
        this.width = width;
        this.height = height;
        this.enemyType = enemyType;
        this.getTransform().setPosition(startPosition);
        this.health = new HealthImplementation(health);
        try
        {
            // Load enemy sprite from PostApocalypse assets
            Image idleSprite = Resources.loadImageOrThrow(
                    "assets",
                    enemyType.getPath()
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


    public Enemy(Vector2D startPosition, double speed, EnemyAsset enemyType, int health)
    {
        this.health = new HealthImplementation();
        this.movementSpeed = speed;
        this.width = EnemyConfig.DEFAULT_SIZE.getValue();
        this.height = EnemyConfig.DEFAULT_SIZE.getValue();
        this.enemyType = enemyType;
        this.getTransform().setPosition(startPosition);
        this.health = new HealthImplementation(health);

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
        pathFinder = new PathFinder(this.getScene());
        setHitbox();
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (pathFinder == null)
        {
            pathFinder = new PathFinder(this.getScene());
        }
        setHitbox();
        moveTowardsTarget(deltaTime);
    }

    public void setTargetPosition(Vector2D target)
    {
        this.targetPosition.set(target);
    }

    private int currentPathIndex = 0;

    private void moveTowardsTarget(TimeSpan deltaTime)
    {

        movement = pathFinder.getPath(this);
        if (movement == null || currentPathIndex >= movement.size()) return;

        Vector2D currentPos = this.getTransform().getPosition();
        Node targetNode = movement.get(currentPathIndex);
        Vector2D targetPos = new Vector2D(targetNode.getCol(), targetNode.getRow());


        Vector2D direction = targetPos.subtract(currentPos);
        double distance = direction.magnitude();


        double moveDist = movementSpeed * deltaTime.asSeconds();
        if (distance <= moveDist)
        {
            this.getTransform().setPosition(targetPos);
            currentPathIndex++;
        }
        else
        {
            Vector2D velocity = direction.normalize().multiply(moveDist);
            this.getTransform().setPosition(currentPos.add(velocity));
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
        {
            hitbox = new HitBox(this.getTransform().getPosition(), height, width);
            this.addComponent(hitbox);
        }
    }

    @Override
    public HitBox getHitbox()
    {
        if (hitbox == null)
        {

            setHitbox();
        }
        return hitbox;
    }

    @Override
    public void triggerCollisionEffect(Concrete collidedObj)
    {
        double dx = 0, dy = 0;
        if (hitbox.isLeftTouching())
        {
            dx += 1.0;
        }
        if (hitbox.isRightTouching())
        {
            dx -= 1.0;
        }
        if (hitbox.isTopTouching())
        {
            dy += 1.0;
        }
        if (hitbox.isBottomTouching())
        {
            dy -= 1.0;
        }
        this.getTransform().setPosition(this.getTransform().getPosition().add(dx, dy));
    }

    @Override
    public int getDamage()
    {
        return this.damage;
    }

    @Override
    public boolean isDestroyable()
    {
        return false;
    }

    @Override
    public void destroyThis() { if (isDestroyable()) this.destroy(); }

    @Override
    public List<Class<? extends Concrete>> notInteractWith()
    {
        return List.of(Enemy.class);
    }

    @Override
    public List<Class<? extends Damageable>> notDamageObj()
    {
        return List.of(Enemy.class);
    }

    @Override
    public void takeDamage(int damage)
    {
        health.takeDamage(damage);
    }

    @Override
    public void setHealth(Health health)
    {
        this.health = health;
    }

    @Override
    public Health getHealth()
    {
        return health;
    }

    @Override
    public boolean isDead()
    {
        if (health.isAlive()) return false;
        return true;
    }

}