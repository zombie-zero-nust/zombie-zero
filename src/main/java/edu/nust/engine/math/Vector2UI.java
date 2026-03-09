package edu.nust.engine.math;

/**
 * A container for holding 2 variables within Unit Interval, {@code [0, 1]}, x, y.
 * <br>
 * <br>
 * Can be used to represent colors, UV coordinates, etc.
 */
public class Vector2UI extends Vector2D
{
    public Vector2UI()
    {
        this.setX(0);
        this.setY(0);
    }

    public Vector2UI(double x, double y)
    {
        this.setX(clamp(x));
        this.setY(clamp(y));
    }

    /* GETTERS AND SETTERS */

    @Override
    public void setX(double x)
    {
        super.setX(clamp(x));
    }

    @Override
    public void setY(double y)
    {
        super.setY(clamp(y));
    }

    /* HELPERS */

    private double clamp(double value)
    {
        return Math.max(0, Math.min(1, value));
    }
}
