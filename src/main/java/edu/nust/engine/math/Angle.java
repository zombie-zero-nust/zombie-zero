package edu.nust.engine.math;

/**
 * Angle that can be represented in both degrees and radians.
 * <br>
 * <br>
 * Uses {@code double} internally
 */
public class Angle
{
    private double degrees;

    public Angle(double degrees)
    {
        this.degrees = degrees;
    }

    /* RADIANS */

    public double getRadians()
    {
        return Math.toRadians(degrees);
    }

    public void setRadians(double radians)
    {
        this.degrees = Math.toDegrees(radians);
    }

    /* DEGREES */

    public double getDegrees()
    {
        return degrees;
    }

    public void setDegrees(double degrees)
    {
        this.degrees = degrees;
    }

    /* OPERATORS */

    public Angle add(Angle other)
    {
        return new Angle(this.degrees + other.degrees);
    }

    public Angle subtract(Angle other)
    {
        return new Angle(this.degrees - other.degrees);
    }

    public Angle multiply(double scalar)
    {
        return new Angle(this.degrees * scalar);
    }

    public Angle divide(double scalar)
    {
        return new Angle(this.degrees / scalar);
    }

    /* SIMPLE */

    public static Angle zero()
    {
        return new Angle(0);
    }
}
