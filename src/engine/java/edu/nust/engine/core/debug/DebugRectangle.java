package edu.nust.engine.core.debug;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public final class DebugRectangle extends DebugShape
{
    public DebugRectangle(Vector2D start, Vector2D end, TimeSpan lifespan) { super(start, end, lifespan); }

    /* RENDER */

    @Override
    public void render(GraphicsContext context)
    {
        context.fillRect(start.getX(), start.getY(), getSize().getX(), getSize().getY());
        context.strokeRect(start.getX(), start.getY(), getSize().getX(), getSize().getY());
    }
}
