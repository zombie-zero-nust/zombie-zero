package edu.nust.engine.math;

/**
 * Represents an axis-aligned rectangle using position (top-left) and size (width, height).
 */
public class Rectangle
{
    private final Vector2D position;
    private final Vector2D size;

    /* FACTORY */

    public Rectangle() { this(Vector2D.zero(), Vector2D.zero()); }

    public Rectangle(Vector2D position, Vector2D size)
    {
        this.position = position;
        this.size = size;
    }

    public Rectangle(double x, double y, double width, double height)
    {
        this(new Vector2D(x, y), new Vector2D(width, height));
    }

    public static Rectangle fromCorners(Vector2D topLeft, Vector2D bottomRight)
    {
        return new Rectangle(topLeft, bottomRight.subtract(topLeft));
    }

    public static Rectangle fromCorners(double sx, double sy, double ex, double ey)
    {
        return fromCorners(new Vector2D(sx, sy), new Vector2D(ex, ey));
    }

    public static Rectangle fromCenter(Vector2D center, Vector2D size)
    {
        return new Rectangle(center.subtract(size.multiply(0.5)), size);
    }

    /* SIDES */

    public double getLeft() { return position.getX(); }

    public double getTop() { return position.getY(); }

    public double getRight() { return getLeft() + getWidth(); }

    public double getBottom() { return getTop() + getHeight(); }

    public Rectangle setLeft(double left) { return setPosition(left, getTop()); }

    public Rectangle setTop(double top) { return setPosition(getLeft(), top); }

    public Rectangle setRight(double right) { return setPosition(right - getWidth(), getTop()); }

    public Rectangle setBottom(double bottom) { return setPosition(getLeft(), bottom - getHeight()); }

    /* CORNERS */

    public Vector2D getTopLeft() { return position; }

    public Vector2D getTopRight() { return new Vector2D(getRight(), getTop()); }

    public Vector2D getBottomLeft() { return new Vector2D(getLeft(), getBottom()); }

    public Vector2D getBottomRight() { return new Vector2D(getRight(), getBottom()); }

    public Vector2D getCenter()
    {
        return new Vector2D(getLeft() + (getWidth() / 2.0), getTop() + (getHeight() / 2.0));
    }

    public Rectangle setTopLeft(Vector2D topLeft) { return setPosition(topLeft); }

    public Rectangle setTopRight(Vector2D topRight) { return setPosition(topRight.subtract(getWidth(), 0)); }

    public Rectangle setBottomLeft(Vector2D bottomLeft) { return setPosition(bottomLeft.subtract(0, getHeight())); }

    public Rectangle setBottomRight(Vector2D bottomRight) { return setPosition(bottomRight.subtract(getSize())); }

    public Rectangle setCenter(Vector2D center)
    {
        return setPosition(center.subtract(getWidth() / 2.0, getHeight() / 2.0));
    }

    /* SIZE */

    public Vector2D getSize() { return size; }

    public double getWidth() { return size.getX(); }

    public double getHeight() { return size.getY(); }

    public Rectangle setSize(Vector2D size)
    {
        this.size.set(size);
        return this;
    }

    public Rectangle setSize(double width, double height) { return setSize(new Vector2D(width, height)); }

    public Rectangle setWidth(double width) { return setSize(width, getHeight()); }

    public Rectangle setHeight(double height) { return setSize(getWidth(), height); }

    /* OPERATIONS SELF */

    public Rectangle translateSelf(Vector2D delta)
    {
        this.position.addSelf(delta);
        return this;
    }

    public Rectangle translateSelf(double x, double y) { return translateSelf(new Vector2D(x, y)); }

    public Rectangle scaleSelf(double sx, double sy)
    {
        this.size.set(this.size.getX() * sx, this.size.getY() * sy);
        return this;
    }

    public Rectangle scaleSelf(double scalar) { return scaleSelf(scalar, scalar); }

    /* OPERATIONS NON-MUTATING */

    public Rectangle copy() { return new Rectangle(position, size); }

    public Rectangle translated(Vector2D delta) { return copy().translateSelf(delta); }

    public Rectangle translated(double x, double y) { return translated(new Vector2D(x, y)); }

    public Rectangle scaled(double sx, double sy) { return copy().scaleSelf(sx, sy); }

    public Rectangle scaled(double scalar) { return scaled(scalar, scalar); }

    /* OPERATIONS STATICS */

    public static Rectangle translate(Rectangle rect, Vector2D delta) { return rect.translated(delta); }

    public static Rectangle translate(Rectangle rect, double dx, double dy) { return rect.translated(dx, dy); }

    public static Rectangle scale(Rectangle rect, double scalar) { return rect.scaled(scalar); }

    public static Rectangle scale(Rectangle rect, double sx, double sy) { return rect.scaled(sx, sy); }

    /* QUERIES */

    public boolean contains(Vector2D point)
    {
        return point.getX() >= getLeft() && //
                point.getX() <= getRight() && //
                point.getY() >= getTop() && //
                point.getY() <= getBottom();
    }

    public boolean contains(double x, double y)
    {
        return contains(new Vector2D(x, y));
    }

    public boolean intersects(Rectangle other)
    {
        return this.getLeft() < other.getRight() && //
                this.getRight() > other.getLeft() && //
                this.getTop() < other.getBottom() && //
                this.getBottom() > other.getTop();
    }

    public double area()
    {
        return getWidth() * getHeight();
    }

    /* STATIC QUERIES */

    public static boolean contains(Rectangle rect, Vector2D point) { return rect.contains(point); }

    public static boolean contains(Rectangle rect, double x, double y) { return rect.contains(x, y); }

    public static boolean intersects(Rectangle a, Rectangle b) { return a.intersects(b); }

    public static double area(Rectangle rect) { return rect.area(); }

    /* INTERNAL */

    /// <b>{@code INTERNAL}</b>
    private Rectangle setPosition(Vector2D position)
    {
        this.position.set(position);
        return this;
    }

    /// <b>{@code INTERNAL}</b>
    private Rectangle setPosition(double x, double y) { return setPosition(new Vector2D(x, y)); }
}