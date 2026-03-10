package edu.nust.engine.math;

/**
 * A container for holding 2 variables of type int, x, y.
 * <br>
 * <br>
 * Can be used to represent grid coordinates, frame indices, etc.
 */
public class Vector2I
{
    private int x;
    private int y;

    public Vector2I()
    {
        this.x = 0;
        this.y = 0;
    }

    public Vector2I(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /* GETTERS AND SETTERS */

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    /* OPERATORS */

    public Vector2I add(Vector2I other)
    {
        return new Vector2I(this.x + other.x, this.y + other.y);
    }

    public Vector2I subtract(Vector2I other)
    {
        return new Vector2I(this.x - other.x, this.y - other.y);
    }

    public Vector2I multiply(int scalar)
    {
        return new Vector2I(this.x * scalar, this.y * scalar);
    }

    public int dot(Vector2I other)
    {
        return this.x * other.x + this.y * other.y;
    }

    public double magnitude()
    {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize()
    {
        double mag = magnitude();
        if (mag == 0) return new Vector2D(0, 0);
        return new Vector2D(x / mag, y / mag);
    }
}
