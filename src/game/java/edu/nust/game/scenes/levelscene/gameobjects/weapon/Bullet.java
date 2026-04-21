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
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.util.List;


public class Bullet extends GameObject implements Damaging, Concrete
{
    private int speed;
    private Vector2D pos;
    private Vector2D direction;
    private Image image;
    private int damage;

    private double totalDistance;
    private double range;
    private int height;
    private int width;
    private boolean destroyed = false;
    private HitBox hitbox;

    public Bullet(int speed, Vector2D pos, double range, int height, int width, Vector2D mousePos, int damage)
    {
        this.speed = speed;
        this.pos = pos;
        totalDistance = 0;
        this.range = range;
        this.height = height;
        this.width = width;
        direction = mousePos.subtract(pos).normalize();
        this.damage = damage;
        try
        {
            image = Resources.loadImageOrThrow(
                    "assets",
                    "raw/PostApocalypse/Character/Guns/Bullets",
                    "Gun-bullet_Bullet.png"
            );
        }
        catch (FileNotFoundException ignored) { }
        this.addComponent(new SpriteRenderer(width, height, image));
        this.getTransform().setPosition(pos);
    }

    public Bullet(int speed, Vector2D pos, Image image, double range, int height, int width, Vector2D mousePos)
    {
        this.speed = speed;
        this.pos = pos;
        this.image = image;
        totalDistance = 0;
        this.range = range;
        this.height = height;
        this.width = width;
        this.addComponent(new SpriteRenderer(width, height, image));
        this.getTransform().setPosition(pos);
    }


    @Override
    public void onInit()
    {
        hitbox = new HitBox(this.pos, height, width);
        this.addComponent(hitbox);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        Vector2D moveDistance = direction.multiply(speed * deltaTime.asSeconds());
        pos = pos.add(moveDistance);
        double angle = Math.atan2(direction.getY(), direction.getX());
        this.getTransform().setPosition(pos);
        this.getTransform().setRotationRadians(angle);
        totalDistance += moveDistance.magnitude();
        if (totalDistance >= range) destroyed = true;
    }

    @Override
    public void lateUpdate(TimeSpan deltaTime) { if (destroyed) this.destroy(); }

    public boolean isDestroyed() { return destroyed; }

    @Override
    public int getDamage() { return this.damage; }

    @Override
    public boolean isDestroyable() { return true; }

    @Override
    public HitBox getHitbox() { return hitbox; }

    @Override
    public void destroyThis() { if (isDestroyable()) this.destroy(); }

    @Override
    public List<Class<? extends Damageable>> notDamageObj() { return List.of(Player.class); }

    @Override
    public void setHitbox()
    {
        if (this.hitbox == null)
        {
            hitbox = new HitBox(pos, height, width);
            this.addComponent(hitbox);
        }
    }

    @Override
    public void triggerCollisionEffect(Concrete collidedObj) { destroyThis(); }

    @Override
    public List<Class<? extends Concrete>> notInteractWith() { return List.of(Player.class); }
}