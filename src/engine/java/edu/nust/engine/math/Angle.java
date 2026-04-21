package edu.nust.engine.math;

/**
 * Angle that can be represented in both degrees and radians.
 * <br>
 * <br>
 * Uses {@code double} internally
 */
public class Angle
{
    /// **`INTERNAL`** The angle in degrees, wrapped between -180 and 180
    private double degrees;

    /// Use [Angle#fromDegrees(double)] or [Angle#fromRadians(double)] instead.
    private Angle(double degrees) { this.degrees = wrap180(degrees); }

    /* PROPERTIES */

    @Override
    public String toString() { return String.format("Angle(%.2f degrees)", degrees); }

    /* FACTORY */

    public static Angle zero() { return new Angle(0); }

    public static Angle fromDegrees(double degrees) { return new Angle(degrees); }

    public static Angle fromRadians(double radians) { return new Angle(Math.toDegrees(radians)); }

    /* DEGREES */

    public double getDegrees() { return degrees; }

    public Angle setDegrees(double degrees)
    {
        this.degrees = wrap180(degrees);
        return this;
    }

    /* RADIANS */

    public double getRadians() { return Math.toRadians(degrees); }

    public Angle setRadians(double radians)
    {
        setDegrees(Math.toDegrees(radians));
        return this;
    }

    /* ANGLE */

    public Angle set(Angle other) { return setDegrees(other.degrees); }

    /* OPERATORS NON MUTATING (COPY) */

    public Angle add(Angle other) { return new Angle(this.degrees + other.degrees); }

    public Angle subtract(Angle other) { return new Angle(this.degrees - other.degrees); }

    public Angle multiply(double scalar) { return new Angle(this.degrees * scalar); }

    public Angle divide(double scalar) { return new Angle(this.degrees / scalar); }

    public Angle lerp(Angle target, double t)
    {
        double delta = wrap180(target.degrees - this.degrees);
        return new Angle(this.degrees + delta * t);
    }

    public Angle clamp(Angle min, Angle max)
    {
        double clampedDegrees = Math.max(min.degrees, Math.min(max.degrees, this.degrees));
        return new Angle(clampedDegrees);
    }

    public Angle min(Angle other) { return new Angle(Math.min(this.degrees, other.degrees)); }

    public Angle max(Angle other) { return new Angle(Math.max(this.degrees, other.degrees)); }

    /* OPERATORS SELF */

    public Angle addSelf(Angle other) { return setDegrees(this.degrees + other.degrees); }

    public Angle subtractSelf(Angle other) { return setDegrees(this.degrees - other.degrees); }

    public Angle multiplySelf(double scalar) { return setDegrees(this.degrees * scalar); }

    public Angle divideSelf(double scalar) { return setDegrees(this.degrees / scalar); }

    public Angle lerpSelf(Angle target, double t)
    {
        double delta = wrap180(target.degrees - this.degrees);
        return setDegrees(this.degrees + delta * t);
    }

    public Angle clampSelf(Angle min, Angle max)
    {
        double clampedDegrees = Math.max(min.degrees, Math.min(max.degrees, this.degrees));
        return setDegrees(clampedDegrees);
    }

    public Angle minSelf(Angle other) { return setDegrees(Math.min(this.degrees, other.degrees)); }

    public Angle maxSelf(Angle other) { return setDegrees(Math.max(this.degrees, other.degrees)); }

    /* OPERATORS STATICS */

    public static Angle add(Angle first, Angle second) { return new Angle(first.degrees + second.degrees); }

    public static Angle subtract(Angle first, Angle second) { return new Angle(first.degrees - second.degrees); }

    public static Angle multiply(Angle angle, double scalar) { return new Angle(angle.degrees * scalar); }

    public static Angle divide(Angle angle, double scalar) { return new Angle(angle.degrees / scalar); }

    public static Angle lerp(Angle first, Angle second, double t)
    {
        double delta = wrap180(second.degrees - first.degrees);
        return new Angle(first.degrees + delta * t);
    }

    public static Angle clamp(Angle angle, Angle min, Angle max)
    {
        double clampedDegrees = Math.max(min.degrees, Math.min(max.degrees, angle.degrees));
        return new Angle(clampedDegrees);
    }

    public static Angle min(Angle first, Angle second) { return new Angle(Math.min(first.degrees, second.degrees)); }

    public static Angle max(Angle first, Angle second) { return new Angle(Math.max(first.degrees, second.degrees)); }

    /* QUERIES & COMPARISON */

    /// **`INTERNAL`** Wraps the angle between 0 and 180
    private static double wrap180(double degrees)
    {
        double modDegrees = degrees % 360; // between -360 and 360
        if (modDegrees >= 180) modDegrees -= 360;
        if (modDegrees < -180) modDegrees += 360;
        return modDegrees;
    }

    public Angle deltaTo(Angle target)
    {
        return subtract(target, this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Angle other = (Angle) obj;
        return Math.abs(this.degrees - other.degrees) < 1e-10; // consider angles equal if they are very close
    }

    public boolean equalsEpsilon(Angle other, double epsilon) { return Math.abs(this.degrees - other.degrees) < epsilon; }

    public boolean isZero() { return equals(Angle.zero()); }

    public boolean isZeroEpsilon(double epsilon) { return equalsEpsilon(Angle.zero(), epsilon); }
}
