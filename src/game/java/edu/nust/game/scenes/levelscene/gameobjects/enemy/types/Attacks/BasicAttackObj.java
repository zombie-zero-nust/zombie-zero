package edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Attacks;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.systems.collision.Damageable;
import edu.nust.game.systems.collision.Damaging;
import edu.nust.game.systems.collision.HitBox;
import javafx.scene.image.Image;

import java.util.List;

public class BasicAttackObj extends GameObject implements Damaging {

    private int damage;
    private Enemy enemy;
    private HitBox hitBox;

    private double height;
    private double width;
    private double range;

    private final List<Class<? extends Damageable>> notDamageObj;

    private boolean isMoving;
    private TimeSpan lastsFor;

    private Vector2D currentPos;

    private double movedDistance = 0;
    private double movementSpeed;

    private double elapsed = 0;
    private double distanceForSpawn;

    private SpriteRenderer spriteRenderer;
    private boolean isAnimating = false;
    private TimeSpan animationTime;

    private Vector2D targetDirection;

    // normal melee hitbox constructor
    public BasicAttackObj(int damage, Enemy enemy, double width, double height,
                          List<Class<? extends Damageable>> notDamageObj,
                          TimeSpan lastsFor, double distanceForSpawn)
    {
        this.damage = damage;
        this.enemy = enemy;
        this.width = width;
        this.height = height;
        this.notDamageObj = notDamageObj;

        this.isMoving = false;
        this.lastsFor = lastsFor;
        this.distanceForSpawn = distanceForSpawn;
    }

    // moving projectile constructor (no sprite)
    public BasicAttackObj(int damage, Enemy enemy, double width, double height,
                          List<Class<? extends Damageable>> notDamageObj,
                          double distanceForSpawn, double movementSpeed)
    {
        this.damage = damage;
        this.enemy = enemy;
        this.width = width;
        this.height = height;
        this.notDamageObj = notDamageObj;

        this.isMoving = true;
        this.distanceForSpawn = distanceForSpawn;
        this.movementSpeed = movementSpeed;
    }

    // moving projectile constructor (WITH sprite + animation)
    public BasicAttackObj(int damage, Enemy enemy, double width, double height, double range,
                          List<Class<? extends Damageable>> notDamageObj,
                          double distanceForSpawn, Image image,
                          int numFrameX, int numFrameY,
                          TimeSpan animationTime, double movementSpeed)
    {
        this.damage = damage;
        this.enemy = enemy;
        this.width = width;
        this.height = height;
        this.notDamageObj = notDamageObj;

        this.range = range;
        this.isMoving = true;
        this.movementSpeed = movementSpeed;
        this.distanceForSpawn = distanceForSpawn;

        this.animationTime = animationTime;

        spriteRenderer = new SpriteRenderer(width, height, image, numFrameX, numFrameY);
        spriteRenderer.setAnimationTime(animationTime);
    }

    @Override
    public void onInit()
    {
        GameScene scene = enemy.getScene();

        // ADD RENDERER TO OBJECT
        if (spriteRenderer != null) {
            this.addComponent(spriteRenderer);
        }

        if (targetDirection == null)
        {
            Player player = (Player) scene.getFirstOfType(Player.class);
            if (player != null) {
                targetDirection = player.getTransform().getPosition()
                        .subtract(enemy.getTransform().getPosition())
                        .normalize();
            }
        }

        if (targetDirection != null)
        {
            currentPos = enemy.getTransform().getPosition()
                    .add(targetDirection.multiply(distanceForSpawn));
        }
        else {
            currentPos = enemy.getTransform().getPosition();
        }

        this.getTransform().setPosition(currentPos);

        hitBox = new HitBox(currentPos, height / 2, width / 2);
        this.addComponent(hitBox);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (isMoving) {
            move(deltaTime);
        }
        else {
            elapsed += deltaTime.asMilliseconds();
            if (elapsed >= lastsFor.asMilliseconds()) {
                destroyThis();
            }
        }
    }

    public void move(TimeSpan deltaTime)
    {
        // start animation ONCE
        if (spriteRenderer != null && !isAnimating)
        {
            spriteRenderer.startAnimation();
            isAnimating = true;
        }

        if (movedDistance >= range) {
            destroyThis();
            return;
        }

        double distance = movementSpeed * deltaTime.asSeconds();

        currentPos = currentPos.add(targetDirection.multiply(distance));
        movedDistance += distance;

        this.getTransform().setPosition(currentPos);

    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public boolean isDestroyable() {
        return true;
    }

    @Override
    public HitBox getHitbox() {
        return hitBox;
    }

    @Override
    public void destroyThis() {
        this.destroy();
    }

    @Override
    public List<Class<? extends Damageable>> notDamageObj() {
        return notDamageObj;
    }

    public double getRange() {
        return range;
    }

    public void setTargetDirection(Vector2D targetDirection) {
        this.targetDirection = targetDirection;
    }
}