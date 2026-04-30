package edu.nust.game.scenes.levelscene.gameobjects.enemy.types;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.audio.SoundEffectReference;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.player.Health;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.systems.assets.EnemyAsset;
import edu.nust.game.systems.audio.Audios;
import edu.nust.game.systems.collision.Concrete;
import edu.nust.game.systems.collision.Damageable;
import edu.nust.game.systems.collision.HitBox;
import edu.nust.game.systems.pathfinder.Node;
import edu.nust.game.systems.pathfinder.PathFinder;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Enemy extends GameObject implements Concrete, Damageable
{
    protected enum Facing
    {
        UP,
        DOWN,
        RIGHT,
        LEFT;

        public static Facing getRandom()
        {
            return values()[ThreadLocalRandom.current().nextInt(values().length)];
        }
    }

    private PathFinder pathFinder;
    private boolean isDying = false;

    private double movementSpeed;
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

    private boolean attacking = false;

    private static final int NODE_SIZE = 2;
    private static final double MAX_RED_TINT_STRENGTH = 0.5;

    private double followRadius = getBaseFollowRadius();

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
        pathFinder = new PathFinder((LevelScene) this.getScene());
        movement = pathFinder.getPath(this);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (!health.isAlive())
        {
            playDeathAnimation(deltaTime);
            return;
        }
        if (pathFinder == null) pathFinder = new PathFinder((LevelScene) this.getScene());

        Player player = (Player) this.getScene().getFirstOfType(Player.class);
        boolean canChasePlayer = isPlayerWithinChaseRadius(player);

        if (!canChasePlayer)
        {
            movement = null;
            currentPathIndex = 0;
            pathTimer = TimeSpan.zero();
            attack(deltaTime);
            if (!isAttacking()) updateSprite(0, 0);
            return;
        }

        pathTimer = pathTimer.add(deltaTime);

        if (pathTimer.asSeconds() >= pathUpdateInterval.asSeconds())
        {

            ArrayList<Node> newPath = pathFinder.getPath(this);

            if (newPath != null && !newPath.isEmpty())
            {
                movement = newPath;
                currentPathIndex = 0;
            }

            pathTimer = TimeSpan.zero();
        }

        if (!attacking)
        {
            boolean moved = moveAlongPath(deltaTime);
            if (!moved && !isAttacking()) updateSprite(0, 0);
        }
        attack(deltaTime);

    }

    private boolean isPlayerWithinChaseRadius(Player player)
    {
        if (player == null) return false;

        double distanceToPlayer = player.getTransform()
                .getPosition()
                .subtract(this.getTransform().getPosition())
                .magnitude();

        return distanceToPlayer <= followRadius;
    }

    public void multiplyFollowRadius(double factor)
    {
        if (factor <= 0) return;
        followRadius *= factor;
    }

    private boolean moveAlongPath(TimeSpan deltaTime)
    {

        if (movement == null || currentPathIndex >= movement.size()) return false;

        Vector2D currentPos = this.getTransform().getPosition();
        Node targetNode = movement.get(currentPathIndex);

        Vector2D targetPos = pathFinder.getMapTopLeftPos()
                .add(new Vector2D(
                        targetNode.getCol() * NODE_SIZE + NODE_SIZE / 2.0,
                        targetNode.getRow() * NODE_SIZE + NODE_SIZE / 2.0
                ));

        Vector2D direction = targetPos.subtract(currentPos);
        double distance = direction.magnitude();


        updateSprite(direction.getX(), direction.getY());

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

        return true;
    }

    @Override
    public void setHitbox()
    {
        if (hitbox == null)
        {
            hitbox = new HitBox(this.getTransform().getPosition(), height / 2, width / 2);
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
    public void triggerCollisionEffect(Concrete collidedObj) { }

    @Override
    public List<Class<? extends Concrete>> notInteractWith()
    {
        return List.of(Enemy.class);
    }

    @Override
    public void takeDamage(int damage)
    {
        health.takeDamage(damage);
        onHealthChanged();
        if(this.getClass() == BasicEnemy.class) {
            Audios.randomZombieBasicHurtRef().ifPresent(SoundEffectReference::play);
        }
        else{
            Audios.randomZombieBossHurtRef().ifPresent(SoundEffectReference::play);
        }
        if (!health.isAlive() && !isDying)
        {
            isDying = true;
            freezeEnemy();
        }
    }

    private void freezeEnemy()
    {
        movement = null;
        currentPathIndex = 0;
        attacking = false;
        pathTimer = TimeSpan.zero();
        if (getHitbox() != null) getHitbox().setActive(false);
    }

    public void unfreezeEnemy()
    {
        isDying = false;
        movement = new ArrayList<>();
        if (getHitbox() != null) getHitbox().setActive(true);
    }


    @Override
    public void setHealth(Health health)
    {
        this.health = health;
        onHealthChanged();
    }

    protected Color getHealthTintColor()
    {
        if (health == null) return null;

        double tintStrength = (1.0 - health.getHealthRatio()) * MAX_RED_TINT_STRENGTH;
        if (tintStrength <= 0.0) return null;

        return Color.color(1.0, 1.0 - tintStrength, 1.0 - tintStrength);
    }

    protected void onHealthChanged() { }

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

    public abstract void playDeathAnimation(TimeSpan deltaTime);

    public abstract void updateSprite(double dx, double dy);

    public abstract void loadSprites(EnemyAsset enemyType);

    public abstract void attack(TimeSpan deltaTime);

    public double getWidth()
    {
        return width;
    }

    public double getHeight()
    {
        return height;
    }

    public boolean isAttacking()
    {
        return attacking;
    }

    public void setAttacking(boolean attacking)
    {
        this.attacking = attacking;
    }

    public void setMovementSpeed(double speed)
    {
        movementSpeed = speed;
    }

    public double getMovementSpeed()
    {
        return movementSpeed;
    }


    public double getBaseFollowRadius() { return 200; }
}