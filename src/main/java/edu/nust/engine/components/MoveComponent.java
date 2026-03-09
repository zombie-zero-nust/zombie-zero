package edu.nust.engine.components;

import edu.nust.engine.core.Component;
import edu.nust.engine.math.Vector2D;

public class MoveComponent extends Component
{
    private final double speedX;
    private final double speedY;

    public MoveComponent(double speedX, double speedY)
    {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    @Override
    public void onUpdate()
    {
        Transform transform = gameObject.getTransform();
        transform.setPosition(transform.getPosition().add(new Vector2D(speedX, speedY)));
    }
}