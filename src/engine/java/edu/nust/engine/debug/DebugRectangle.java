package edu.nust.engine.debug;

import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public final class DebugRectangle extends DebugShape
{
    private DebugRectangle(Vector2D start, Vector2D end)
    {
        super(start, end);
    }

    /* FACTORY */

    public static DebugRectangle fromCorners(Vector2D start, Vector2D end) { return new DebugRectangle(start, end); }

    public static DebugRectangle fromSize(Vector2D position, Vector2D size)
    {
        return fromCorners(position, position.add(size));
    }

    public static DebugRectangle fromRectangle(Rectangle rectangle)
    {
        return fromCorners(rectangle.getTopLeft(), rectangle.getBottomRight());
    }

    /* RENDER */

    @Override
    public void render(GraphicsContext context)
    {
        context.fillRect(start.getX(), start.getY(), getSize().getX(), getSize().getY());
        context.strokeRect(start.getX(), start.getY(), getSize().getX(), getSize().getY());
    }
}
