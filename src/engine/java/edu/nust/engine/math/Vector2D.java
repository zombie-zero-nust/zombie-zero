package edu.nust.engine.math;

/**
 * A container for holding 2 {@code double} variables, x, y.
 * <br>
 * <br>
 * Can be used to represent positions, velocities, etc.
 */
public class Vector2D
{
    private double x;
    private double y;

    public Vector2D()
    {
        this.x = 0;
        this.y = 0;
    }

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /* PROPERTIES */

    public double magnitude() { return Math.sqrt(x * x + y * y); }

    @Override
    public String toString() { return String.format("Vector2D(%.2f, %.2f)", x, y); }

    /* FACTORY */

    public static Vector2D zero() { return new Vector2D(0, 0); }

    public static Vector2D fromAngle(Angle angle)
    {
        return new Vector2D(Math.cos(angle.getRadians()), Math.sin(angle.getRadians()));
    }

    public static Vector2D fromAngleDegrees(double degrees) { return fromAngle(Angle.fromDegrees(degrees)); }

    public static Vector2D fromAngleRadians(double radians) { return fromAngle(Angle.fromRadians(radians)); }

    public static Vector2D fromAngle(Angle angle, double magnitude)
    {
        double radians = angle.getRadians();
        return new Vector2D(Math.cos(radians) * magnitude, Math.sin(radians) * magnitude);
    }

    public static Vector2D fromAngleDegrees(double degrees, double magnitude)
    {
        return fromAngle(Angle.fromDegrees(degrees), magnitude);
    }

    public static Vector2D fromAngleRadians(double radians, double magnitude)
    {
        return fromAngle(Angle.fromRadians(radians), magnitude);
    }

    public static Vector2D up() { return new Vector2D(0, 1); }

    public static Vector2D right() { return new Vector2D(1, 0); }

    public static Vector2D down() { return new Vector2D(0, -1); }

    public static Vector2D left() { return new Vector2D(-1, 0); }

    /* POSITION */

    public double getX() { return x; }

    public double getY() { return y; }

    public Vector2D set(Vector2D other)
    {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    public Vector2D set(double x, double y) { return set(new Vector2D(x, y)); }

    public Vector2D setX(double x) { return set(x, this.y); }

    public Vector2D setY(double y) { return set(this.x, y); }

    /* TRANSLATE */

    public void translate(Vector2D translation) { this.addSelf(translation); }

    public void translate(double x, double y) { translate(new Vector2D(x, y)); }

    /* OPERATORS NON MUTATING (COPY) */

    public Vector2D add(Vector2D other) { return new Vector2D(this.x + other.x, this.y + other.y); }

    public Vector2D add(double x, double y) { return add(new Vector2D(x, y)); }

    public Vector2D subtract(Vector2D other) { return new Vector2D(this.x - other.x, this.y - other.y); }

    public Vector2D subtract(double x, double y) { return subtract(new Vector2D(x, y)); }

    public Vector2D multiply(double scalar) { return new Vector2D(this.x * scalar, this.y * scalar); }

    public Vector2D divide(double scalar) { return new Vector2D(this.x / scalar, this.y / scalar); }

    public Vector2D normalize()
    {
        double mag = magnitude();
        if (mag == 0) return new Vector2D(0, 0);
        return new Vector2D(this.x / mag, this.y / mag);
    }

    public Vector2D lerp(Vector2D target, double t)
    {
        return new Vector2D(this.x + (target.x - this.x) * t, this.y + (target.y - this.y) * t);
    }

    public Vector2D rotate(Vector2D origin, Angle angle)
    {
        double cos = Math.cos(angle.getRadians());
        double sin = Math.sin(angle.getRadians());

        double translatedX = this.x - origin.x;
        double translatedY = this.y - origin.y;

        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;

        return new Vector2D(rotatedX + origin.x, rotatedY + origin.y);
    }

    /* OPERATORS SELF */

    public Vector2D addSelf(Vector2D other) { return set(this.x + other.x, this.y + other.y); }

    public Vector2D addSelf(double x, double y) { return addSelf(new Vector2D(x, y)); }

    public Vector2D subtractSelf(Vector2D other) { return set(this.x - other.x, this.y - other.y); }

    public Vector2D subtractSelf(double x, double y) { return subtractSelf(new Vector2D(x, y)); }

    public Vector2D multiplySelf(double scalar) { return set(this.x * scalar, this.y * scalar); }

    public Vector2D divideSelf(double scalar) { return set(this.x / scalar, this.y / scalar); }

    public Vector2D normalizeSelf()
    {
        double mag = magnitude();
        if (mag == 0) return this;
        return set(this.x / mag, this.y / mag);
    }

    public Vector2D lerpSelf(Vector2D target, double t)
    {
        return set(this.x + (target.x - this.x) * t, this.y + (target.y - this.y) * t);
    }

    public Vector2D rotateSelf(Vector2D origin, Angle angle)
    {
        double cos = Math.cos(angle.getRadians());
        double sin = Math.sin(angle.getRadians());

        double translatedX = this.x - origin.x;
        double translatedY = this.y - origin.y;

        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;

        return set(rotatedX + origin.x, rotatedY + origin.y);
    }

    /* OPERATORS STATICS */

    public static Vector2D add(Vector2D a, Vector2D b) { return new Vector2D(a.x + b.x, a.y + b.y); }

    public static Vector2D add(Vector2D a, double x, double y) { return add(a, new Vector2D(x, y)); }

    public static Vector2D add(double x, double y, Vector2D b) { return add(new Vector2D(x, y), b); }

    public static Vector2D add(double x1, double y1, double x2, double y2)
    {
        return add(new Vector2D(x1, y1), new Vector2D(x2, y2));
    }

    public static Vector2D subtract(Vector2D a, Vector2D b) { return new Vector2D(a.x - b.x, a.y - b.y); }

    public static Vector2D subtract(Vector2D a, double x, double y) { return subtract(a, new Vector2D(x, y)); }

    public static Vector2D subtract(double x, double y, Vector2D b) { return subtract(new Vector2D(x, y), b); }

    public static Vector2D subtract(double x1, double y1, double x2, double y2)
    {
        return subtract(new Vector2D(x1, y1), new Vector2D(x2, y2));
    }

    public static Vector2D multiply(Vector2D vector, double scalar)
    {
        return new Vector2D(vector.x * scalar, vector.y * scalar);
    }

    public static Vector2D multiply(double scalar, Vector2D vector) { return multiply(vector, scalar); }

    public static Vector2D multiply(double x, double y, double scalar) { return multiply(new Vector2D(x, y), scalar); }

    public static Vector2D divide(Vector2D vector, double scalar)
    {
        return new Vector2D(vector.x / scalar, vector.y / scalar);
    }

    public static Vector2D divide(double scalar, Vector2D vector) { return divide(vector, scalar); }

    public static Vector2D divide(double x, double y, double scalar) { return divide(new Vector2D(x, y), scalar); }

    public static Vector2D normalize(Vector2D vector)
    {
        double mag = vector.magnitude();
        if (mag == 0) return new Vector2D(0, 0);
        return new Vector2D(vector.x / mag, vector.y / mag);
    }

    public static Vector2D normalize(double x, double y) { return normalize(new Vector2D(x, y)); }

    public static Vector2D lerp(Vector2D source, Vector2D target, double t)
    {
        return new Vector2D(source.x + (target.x - source.x) * t, source.y + (target.y - source.y) * t);
    }

    public static Vector2D lerp(Vector2D source, double x, double y, double t)
    {
        return lerp(source, new Vector2D(x, y), t);
    }

    public static Vector2D lerp(double x, double y, Vector2D target, double t)
    {
        return lerp(new Vector2D(x, y), target, t);
    }

    public static Vector2D lerp(double sourceX, double sourceY, double targetX, double targetY, double t)
    {
        return lerp(new Vector2D(sourceX, sourceY), new Vector2D(targetX, targetY), t);
    }

    public static Vector2D rotate(Vector2D vector, Vector2D origin, Angle angle)
    {
        double cos = Math.cos(angle.getRadians());
        double sin = Math.sin(angle.getRadians());

        double translatedX = vector.x - origin.x;
        double translatedY = vector.y - origin.y;

        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;

        return new Vector2D(rotatedX + origin.x, rotatedY + origin.y);
    }

    public static Vector2D rotate(double x, double y, Vector2D origin, Angle angle)
    {
        return rotate(new Vector2D(x, y), origin, angle);
    }

    public static Vector2D rotate(Vector2D point, double originX, double originY, Angle angle)
    {
        return rotate(point, new Vector2D(originX, originY), angle);
    }

    public static Vector2D rotate(double x, double y, double originX, double originY, Angle angle)
    {
        return rotate(new Vector2D(x, y), new Vector2D(originX, originY), angle);
    }

    /* QUERIES & COMPARISON */

    public double distanceTo(Vector2D other)
    {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceTo(double x, double y) { return distanceTo(new Vector2D(x, y)); }

    public static double distanceBetween(Vector2D a, Vector2D b)
    {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distanceBetween(double x1, double y1, Vector2D b)
    {
        return distanceBetween(new Vector2D(x1, y1), b);
    }

    public static double distanceBetween(Vector2D a, double x2, double y2)
    {
        return distanceBetween(a, new Vector2D(x2, y2));
    }

    public static double distanceBetween(double x1, double y1, double x2, double y2)
    {
        return distanceBetween(new Vector2D(x1, y1), new Vector2D(x2, y2));
    }

    public Angle angleTo(Vector2D other)
    {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return Angle.fromRadians(Math.atan2(dy, dx));
    }

    public Angle angleTo(double x, double y) { return angleTo(new Vector2D(x, y)); }

    public static Angle angleBetween(Vector2D a, Vector2D b)
    {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Angle.fromRadians(Math.atan2(dy, dx));
    }

    public static Angle angleBetween(double x1, double y1, Vector2D b) { return angleBetween(new Vector2D(x1, y1), b); }

    public static Angle angleBetween(Vector2D a, double x2, double y2) { return angleBetween(a, new Vector2D(x2, y2)); }

    public static Angle angleBetween(double x1, double y1, double x2, double y2)
    {
        return angleBetween(new Vector2D(x1, y1), new Vector2D(x2, y2));
    }

    public Vector2D perpendicular() { return new Vector2D(-y, x); }

    public static Vector2D perpendicularOf(Vector2D vector) { return new Vector2D(-vector.y, vector.x); }

    public static Vector2D perpendicularOf(double x, double y) { return perpendicularOf(new Vector2D(x, y)); }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2D other = (Vector2D) obj;
        return this.x == other.x && this.y == other.y;
    }

    public boolean equals(double x, double y) { return equals(new Vector2D(x, y)); }

    public boolean equalsEpsilon(Vector2D other, double epsilon)
    {
        return Math.abs(this.x - other.x) < epsilon && Math.abs(this.y - other.y) < epsilon;
    }

    public boolean equalsEpsilon(double x, double y, double epsilon)
    {
        return equalsEpsilon(new Vector2D(x, y), epsilon);
    }

    public boolean isZero() { return equals(0, 0); }

    public boolean isZeroEpsilon(double epsilon) { return equalsEpsilon(0, 0, epsilon); }
}
