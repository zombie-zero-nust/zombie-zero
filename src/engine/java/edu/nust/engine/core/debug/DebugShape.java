package edu.nust.engine.core.debug;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract sealed class DebugShape permits DebugPoint, DebugRectangle, DebugEllipse
{
    public static final TimeSpan DEFAULT_LIFESPAN = TimeSpan.fromSeconds(5);

    private static long lastID = 0;
    private final long id = lastID++;

    protected final Vector2D start;
    protected final Vector2D end;

    private final TimeSpan destroyTime;

    protected Color fillColor = new Color(1, 0, 1, 0.25);
    protected Color strokeColor = new Color(1, 0, 1, 0.5);

    public DebugShape(Vector2D start, Vector2D end)
    {
        this.start = start;
        this.end = end;
        this.destroyTime = TimeSpan.fromMilliseconds(System.currentTimeMillis()).add(DEFAULT_LIFESPAN);
    }

    public DebugShape(Vector2D start, Vector2D end, TimeSpan lifespan)
    {
        this.start = start;
        this.end = end;
        this.destroyTime = TimeSpan.fromMilliseconds(System.currentTimeMillis()).add(lifespan);
    }

    /* RENDER */

    public void setColors(GraphicsContext context)
    {
        context.setFill(fillColor);
        context.setStroke(strokeColor);
    }

    public abstract void render(GraphicsContext context);

    /* SIZE */

    public Vector2D getStart() { return start; }

    public Vector2D getEnd() { return end; }

    public Vector2D getSize() { return end.subtract(start); }

    /* GETTERS & SETTERS */

    public long getId() { return id; }

    public Color getFillColor() { return fillColor; }

    public Color getStrokeColor() { return strokeColor; }

    public DebugShape setFillColor(Color fillColor)
    {
        this.fillColor = fillColor;
        return this;
    }

    public DebugShape setStrokeColor(Color strokeColor)
    {
        this.strokeColor = strokeColor;
        return this;
    }

    /* HELPERS */

    public boolean isPastDestroyTime(TimeSpan time) { return time.asNanoseconds() >= destroyTime.asNanoseconds(); }

    /* ID */
    // use id for equality and hashing

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        DebugShape other = (DebugShape) obj;
        return this.id == other.id;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }
}
