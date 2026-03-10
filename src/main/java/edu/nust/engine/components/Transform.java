package edu.nust.engine.components;

import edu.nust.engine.core.Component;
import edu.nust.engine.math.Angle;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.math.Vector2UI;

public class Transform extends Component
{
    private Vector2D position = new Vector2D();
    private Vector2UI anchor = new Vector2UI(0.5, 0.5);
    private Angle rotation = Angle.zero();

    /* GETTERS & SETTERS */

    public Vector2D getPosition()
    {
        return position;
    }

    public void setPosition(Vector2D position)
    {
        this.position = position;
    }

    public Vector2UI getAnchor()
    {
        return anchor;
    }

    public void setAnchor(Vector2UI anchor)
    {
        this.anchor = anchor;
    }

    public Angle getRotation()
    {
        return rotation;
    }

    public void setRotation(Angle rotation)
    {
        this.rotation = rotation;
    }

    /* HELPERS */

    public Vector2UI forward()
    {
        return new Vector2UI(Math.cos(rotation.getRadians()), Math.sin(rotation.getRadians()));
    }

    public void translate(Vector2D translation)
    {
        this.position = this.position.add(translation);
    }

    public void translateForward(double distance)
    {
        translate(forward().multiply(distance));
    }

    public void rotate(Angle rotation)
    {
        this.rotation = this.rotation.add(rotation);
    }
}
