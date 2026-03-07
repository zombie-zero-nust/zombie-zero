package edu.nust.engine.math;

/**
 * A container for holding 2 variables, x, y.
 * <br>
 * <br>
 * Can be used to represent positions, velocities, etc.
 */
public class Vector2
{
    private float x;
    private float y;

    public Vector2()
    {
        this.x = 0;
        this.y = 0;
    }

    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    /* GETTERS AND SETTERS */

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    /* OPERATORS */

    public Vector2 add(Vector2 other)
    {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 subtract(Vector2 other)
    {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public Vector2 multiply(float scalar)
    {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public float dot(Vector2 other)
    {
        return this.x * other.x + this.y * other.y;
    }

    public float magnitude()
    {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2 normalize()
    {
        float mag = magnitude();
        if (mag == 0) return new Vector2(0, 0);
        return new Vector2(x / mag, y / mag);
    }
}
