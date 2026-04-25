package edu.nust.engine.core.debug;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public final class DebugRectangle extends DebugShape
{
    public DebugRectangle(Vector2D start, Vector2D end, TimeSpan lifespan) { super(start, end, lifespan); }

    public DebugRectangle(Vector2D start, Vector2D end, boolean singleFrame) { super(start, end, singleFrame); }

    /* RENDER */

    @Override
    public void render(GraphicsContext context)
    {
        context.fillRect(startPos.getX(), startPos.getY(), getSize().getX(), getSize().getY());
        context.strokeRect(startPos.getX(), startPos.getY(), getSize().getX(), getSize().getY());
    }
}
