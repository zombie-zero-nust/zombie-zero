package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.systems.collision.Concrete;
import edu.nust.game.systems.collision.Damageable;
import edu.nust.game.systems.collision.Damaging;
import edu.nust.game.systems.collision.HitBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Bullet extends GameObject implements Damaging, Concrete
{
    private final int speed;
    private final Vector2D startPos;
    private Vector2D direction;
    private Image image;
    private int damage;

    private double totalDistance;
    private final double range;
    private boolean outOfRange = false;
    private HitBox hitbox;
    private static final double COLLISION_HALF_WIDTH = 4;
    private static final double COLLISION_HALF_HEIGHT = 4;

    private boolean shouldShowLine = false;
    private boolean lineShown = false;
    private Vector2D impactPos;

    public Bullet(int speed, Vector2D pos, double range, Vector2D mousePos, int damage)
    {
        this.speed = speed;
        this.startPos = pos;
        totalDistance = 0;
        this.range = range;
        direction = mousePos.subtract(pos).normalize();
        this.damage = damage;
        try
        {
            image = Resources.loadImageOrThrow("assets", "player", "weapon", "bullet.png");
        }
        catch (FileNotFoundException ignored) { }
        this.addComponent(new SpriteRenderer(image));
        this.getTransform().setPosition(pos);
    }

    public Bullet(int speed, Vector2D pos, Image image, double range)
    {
        this.speed = speed;
        this.startPos = pos;
        this.image = image;
        totalDistance = 0;
        this.range = range;
        this.addComponent(new SpriteRenderer(image));
        this.getTransform().setPosition(pos);
    }


    @Override
    public void onInit()
    {
        hitbox = new HitBox(startPos.copy(), COLLISION_HALF_HEIGHT, COLLISION_HALF_WIDTH);
        this.addComponent(hitbox);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        Vector2D moveDistance = direction.multiply(speed * deltaTime.asSeconds());
        double angle = Math.atan2(direction.getY(), direction.getX());
        this.getTransform().getPosition().addSelf(moveDistance);
        this.getTransform().setRotationRadians(angle);
        totalDistance += moveDistance.magnitude();
        if (totalDistance >= range) outOfRange = true;
    }

    @Override
    public void lateUpdate(TimeSpan deltaTime) { if (outOfRange) this.destroy(); }

    @Override
    public void onRender(GraphicsContext context)
    {
        if (shouldShowLine)
        {
            // draw a line from start position to impact position
            context.setStroke(Color.RED);
            context.setLineWidth(2);
            context.strokeLine(startPos.getX(), startPos.getY(), impactPos.getX(), impactPos.getY());
            lineShown = true;
        }
    }

    @Override
    public int getDamage() { return this.damage; }

    @Override
    public boolean isDestroyable() { return true; }

    @Override
    public HitBox getHitbox() { return hitbox; }

    @Override
    public void destroyThis()
    {
        shouldShowLine = true;
        if (isDestroyable() && lineShown) this.destroy();
    }

    @Override
    public List<Class<? extends Damageable>> notDamageObj() { return List.of(Player.class); }

    @Override
    public void setHitbox()
    {
        if (this.hitbox == null)
        {
            hitbox = new HitBox(startPos, COLLISION_HALF_HEIGHT, COLLISION_HALF_WIDTH);
            this.addComponent(hitbox);
        }
    }

    @Override
    public void triggerCollisionEffect(Concrete collidedObj)
    {
        impactPos = this.getTransform().getPosition().copy();
        destroyThis();
        // collidedObj is null when hitting collision mask
        // spawn impacts half the time
        if (ThreadLocalRandom.current().nextBoolean()) // 
            spawnBulletImpact(collidedObj == null
                              ? BulletImpact.ImpactType.ENVIRONMENT
                              : BulletImpact.ImpactType.BLOOD);
    }

    @Override
    public List<Class<? extends Concrete>> notInteractWith() { return List.of(Player.class); }

    private void spawnBulletImpact(BulletImpact.ImpactType impactType)
    {
        BulletImpact impact = new BulletImpact(this.getTransform().getPosition().copy(), impactType);
        this.getScene().addGameObject(impact);
    }
}