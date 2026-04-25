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

    protected final Vector2D startPos;
    protected final Vector2D endPos;

    /// Ignored with {@code singleFrame} is true
    private final TimeSpan destroyTime;
    private final boolean singleFrame;

    protected static final Color FILL_COLOR = new Color(1, 0, 1, 0.25);
    protected static final Color STROKE_COLOR = new Color(1, 0, 1, 0.5);

    public DebugShape(Vector2D startPos, Vector2D endPos, TimeSpan lifespan)
    {
        this.startPos = startPos;
        this.endPos = endPos;
        this.destroyTime = TimeSpan.fromMilliseconds(System.currentTimeMillis()).add(lifespan);
        this.singleFrame = false;
    }

    public DebugShape(Vector2D startPos, Vector2D endPos) { this(startPos, endPos, DEFAULT_LIFESPAN); }

    public DebugShape(Vector2D startPos, Vector2D endPos, boolean singleFrame)
    {
        this.startPos = startPos;
        this.endPos = endPos;
        this.singleFrame = singleFrame;
        this.destroyTime = TimeSpan.fromNanoseconds(1); // ignored
    }

    /* RENDER */

    public void setColors(GraphicsContext context)
    {
        context.setFill(FILL_COLOR);
        context.setStroke(STROKE_COLOR);
    }

    public abstract void render(GraphicsContext context);

    /* SIZE */

    public Vector2D getStartPosition() { return startPos; }

    public Vector2D getEndPosition() { return endPos; }

    public Vector2D getSize() { return endPos.subtract(startPos); }

    /* GETTERS & SETTERS */

    public long getId() { return id; }

    public static Color getFillColor() { return FILL_COLOR; }

    public static Color getStrokeColor() { return STROKE_COLOR; }

    /* HELPERS */

    public boolean isPastDestroyTime(TimeSpan time)
    {
        if (singleFrame) return true;
        return time.asNanoseconds() >= destroyTime.asNanoseconds();
    }

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
