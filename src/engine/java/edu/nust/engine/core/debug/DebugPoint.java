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

    public DebugPoint(Vector2D position, double radius, boolean singleFrame)
    {
        super(
                position.subtract(Vector2D.one().multiply(radius / 2)),
                position.add(Vector2D.one().multiply(radius / 2)),
                singleFrame
        );
    }

    /* RENDER */

    @Override
    public void setColors(GraphicsContext context) { context.setFill(STROKE_COLOR); }

    @Override
    public void render(GraphicsContext context)
    {
        context.fillOval(startPos.getX(), startPos.getY(), getSize().getX(), getSize().getY());
    }
}
