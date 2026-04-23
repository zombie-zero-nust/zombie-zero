package edu.nust.game.systems.collision;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.math.Vector2D;

import java.util.List;

/**
 * Invisible static wall used by CollisionManager for concrete boundary blocking.
 */
public class ConcreteWall extends GameObject implements Concrete
{
    private final Vector2D center;
    private final double width;
    private final double height;
    private HitBox hitbox;

    public ConcreteWall(Vector2D center, double width, double height)
    {
        this.center = center;
        this.width = width;
        this.height = height;
        this.getTransform().setPosition(center);
        setHitbox();
        if (hitbox != null)
            this.addComponent(hitbox);
    }

    @Override
    public List<Class<? extends Concrete>> notInteractWith()
    {
        return null;
    }

    @Override
    public void onInit()
    {
    }

    @Override
    public void setHitbox()
    {
        if (hitbox == null)
            hitbox = new HitBox(center, height / 2.0, width / 2.0);
    }

    @Override
    public HitBox getHitbox()
    {
        return hitbox;
    }

    @Override
    public void triggerCollisionEffect(Concrete collidedObj)
    {
        // Static wall: no movement response needed.
    }

}


