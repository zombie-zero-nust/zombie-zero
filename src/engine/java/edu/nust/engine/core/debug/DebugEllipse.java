package edu.nust.engine.core.debug;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public final class DebugEllipse extends DebugShape
{
    public DebugEllipse(Vector2D start, Vector2D end, TimeSpan lifespan) { super(start, end, lifespan); }

    /* RENDER */

    @Override
    public void render(GraphicsContext context)
    {
        context.fillOval(start.getX(), start.getY(), getSize().getX(), getSize().getY());
        context.strokeOval(start.getX(), start.getY(), getSize().getX(), getSize().getY());
    }
}
