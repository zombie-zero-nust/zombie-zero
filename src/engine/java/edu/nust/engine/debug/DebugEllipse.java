package edu.nust.engine.debug;

import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;

public final class DebugEllipse extends DebugShape
{
    private DebugEllipse(Vector2D start, Vector2D end)
    {
        super(start, end);
    }

    /* FACTORY */

    public static DebugEllipse fromCorners(Vector2D start, Vector2D end) { return new DebugEllipse(start, end); }

    public static DebugEllipse fromSize(Vector2D position, Vector2D size)
    {
        return new DebugEllipse(position, position.add(size));
    }

    /* RENDER */

    @Override
    public void render(GraphicsContext context)
    {
        context.fillOval(start.getX(), start.getY(), getSize().getX(), getSize().getY());
        context.strokeOval(start.getX(), start.getY(), getSize().getX(), getSize().getY());
    }
}
