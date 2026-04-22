package edu.nust.engine.core.debug;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public final class DebugPoint extends DebugShape
{
    public static final int DEFAULT_RADIUS = 5;

    public DebugPoint(Vector2D position, double radius, TimeSpan lifespan)
    {
        super(
                position.subtract(Vector2D.one().multiply(radius / 2)),
                position.add(Vector2D.one().multiply(radius / 2)),
                lifespan
        );
    }

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
