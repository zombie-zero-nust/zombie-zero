package edu.nust.engine.math;

/**
 * A container for holding 2 variables of type int, x, y.
 * <br>
 * <br>
 * Can be used to represent pixel coordinates, grid coordinates, etc.
 */
public class Vector2I extends Vector2D
{
    public Vector2I()
    {
        this.setX(0);
        this.setY(0);
    }

    public Vector2I(int x, int y)
    {
        this.setX(x);
        this.setY(y);
    }

    /* GETTERS AND SETTERS */

    @Override
    public void setX(double x)
    {
        super.setX((int) x);
    }

    @Override
    public void setY(double y)
    {
        super.setY((int) y);
    }
}
