package edu.nust.engine.math;

/**
 * Angle that can be represented in both degrees and radians.
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

    /* SIMPLE */

    public static Angle zero()
    {
        return new Angle(0);
    }
}
