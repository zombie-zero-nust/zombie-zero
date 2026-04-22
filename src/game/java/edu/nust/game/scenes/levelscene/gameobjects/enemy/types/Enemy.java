package edu.nust.game.scenes.levelscene.gameobjects.enemy.types;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.player.Health;
import edu.nust.game.systems.assets.EnemyAsset;
import edu.nust.game.systems.collision.Concrete;
import edu.nust.game.systems.collision.Damageable;
import edu.nust.game.systems.collision.Damaging;
import edu.nust.game.systems.collision.HitBox;
import edu.nust.game.systems.pathfinder.Node;
import edu.nust.game.systems.pathfinder.PathFinder;

import java.util.ArrayList;
import java.util.List;

public abstract class Enemy extends GameObject implements Concrete, Damageable, Damaging
{


    protected enum Facing{
        UP,
        DOWN,
        RIGHT,
        LEFT,
    }

    private PathFinder pathFinder;


    private final double movementSpeed;
    private final double height;
    private final double width;
    private boolean destroyable = false;

    private EnemyAsset enemyType;

    private HitBox hitbox;

    private int damage;
    private Health health;

    private ArrayList<Node> movement = new ArrayList<>();
    private int currentPathIndex = 0;


    private TimeSpan pathTimer = TimeSpan.zero();
    private final TimeSpan pathUpdateInterval = TimeSpan.fromMilliseconds(300);

    private final int nodeSize = 4; // IMPORTANT NODE SIZE

    public Enemy(Vector2D startPosition, double speed, int health)
    {
        this(startPosition, speed, EnemyAsset.ZOMBIE_SMALL, health);
    }

    public Enemy(Vector2D startPosition, double speed, int health, double height, double width, double damage, EnemyAsset enemyType)
    {
        this.movementSpeed = speed;
        this.width = width;
        this.height = height;
        this.enemyType = enemyType;
        this.damage = (int) damage;

        this.getTransform().setPosition(startPosition);
        this.health = new Health(health);

    }

    public Enemy(Vector2D startPosition, double speed, EnemyAsset enemyType, int health)
    {
        this.movementSpeed = speed;
        this.width = 12;
        this.height = 16;
        this.enemyType = enemyType;

        this.getTransform().setPosition(startPosition);
        this.health = new Health(health);


    }

    @Override
    public void onInit()
    {
        setHitbox();
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if(!health.isAlive()) this.destroy();
        if (pathFinder == null)
        {
            pathFinder = new PathFinder((LevelScene) this.getScene());
            movement = pathFinder.getPath(this);
        }


        pathTimer = pathTimer.add(deltaTime);

        if (pathTimer.asSeconds() >= pathUpdateInterval.asSeconds())
        {
            movement = pathFinder.getPath(this);
            currentPathIndex = 0;
            pathTimer = TimeSpan.zero();
        }

        moveAlongPath(deltaTime);
    }

    private void moveAlongPath(TimeSpan deltaTime)
    {
        if (movement == null || movement.isEmpty()) return;
        if (currentPathIndex >= movement.size()) return;

        Vector2D currentPos = this.getTransform().getPosition();
        Node targetNode = movement.get(currentPathIndex);

        // FIXED: Convert node grid coords into world coords using nodeSize
        Vector2D targetPos = pathFinder.getMapTopLeftPos().add(
                targetNode.getCol() * nodeSize,
                targetNode.getRow() * nodeSize
        );

        Vector2D direction = targetPos.subtract(currentPos);
        double distance = direction.magnitude();
        updateSprite(direction.getX(),direction.getY());

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

    @Override
    public void setHitbox()
    {
        if (hitbox == null)
        {
            hitbox = new HitBox(this.getTransform().getPosition(), 16, 16);
            this.addComponent(hitbox);
        }
    }

    @Override
    public HitBox getHitbox()
    {
        if (hitbox == null) setHitbox();
        return hitbox;
    }

    @Override
    public void triggerCollisionEffect(Concrete collidedObj)
    {

    }

    @Override
    public int getDamage()
    {
        return this.damage;
    }

    @Override
    public boolean isDestroyable()
    {
        return destroyable;
    }

    @Override
    public void destroyThis()
    {
        if (isDestroyable()) this.destroy();
    }

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
        return !health.isAlive();
    }



    public abstract void updateSprite(double dx,double dy);

    public abstract void loadSprites(EnemyAsset enemyType);

    public abstract void attack();


}
