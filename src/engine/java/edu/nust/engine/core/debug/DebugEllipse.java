package edu.nust.engine.core.debug;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public final class DebugEllipse extends DebugShape
{
    public DebugEllipse(Vector2D start, Vector2D end, TimeSpan lifespan) { super(start, end, lifespan); }

    public DebugEllipse(Vector2D start, Vector2D end, boolean singleFrame) { super(start, end, singleFrame); }

    /* RENDER */

    @Override
    public void render(GraphicsContext context)
    {
        context.fillOval(startPos.getX(), startPos.getY(), getSize().getX(), getSize().getY());
        context.strokeOval(startPos.getX(), startPos.getY(), getSize().getX(), getSize().getY());
    }
}
