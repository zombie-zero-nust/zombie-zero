package edu.nust.engine.debug;

import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public final class DebugPoint extends DebugShape
{
    public static final int DEFAULT_RADIUS = 5;

    private DebugPoint(Vector2D position, double radius)
    {
        super(
                position.subtract(Vector2D.one().multiply(radius / 2)), // start
                position.add(Vector2D.one().multiply(radius / 2))       // end
        );
    }

    /* FACTORY */

    public static DebugPoint from(Vector2D position, double radius)
    {
        return new DebugPoint(position, radius);
    }

    public static DebugPoint from(Vector2D position) { return from(position, DEFAULT_RADIUS); }

    /* RENDER */

    @Override
    public void setColors(GraphicsContext context)
    {
        context.setFill(strokeColor);
    }

    @Override
    public void render(GraphicsContext context)
    {
        context.fillOval(start.getX(), start.getY(), getSize().getX(), getSize().getY());
    }
}
