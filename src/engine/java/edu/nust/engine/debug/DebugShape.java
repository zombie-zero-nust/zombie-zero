package edu.nust.engine.debug;

import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract sealed class DebugShape permits DebugPoint, DebugRectangle, DebugEllipse
{
    protected final Vector2D start;
    protected final Vector2D end;

    protected Color fillColor = new Color(1, 0, 1, 0.25);
    protected Color strokeColor = new Color(1, 0, 1, 0.5);

    public DebugShape(Vector2D start, Vector2D end)
    {
        this.start = start;
        this.end = end;
    }

    /* RENDER */

    public void setColors(GraphicsContext context)
    {
        context.setFill(fillColor);
        context.setStroke(strokeColor);
    }

    public abstract void render(GraphicsContext context);

    /* SIZE */

    public Vector2D getStart() { return start; }

    public Vector2D getEnd() { return end; }

    public Vector2D getSize() { return end.subtract(start); }

    /* GETTERS & SETTERS */

    public Color getFillColor() { return fillColor; }

    public Color getStrokeColor() { return strokeColor; }

    public DebugShape setFillColor(Color fillColor)
    {
        this.fillColor = fillColor;
        return this;
    }

    public DebugShape setStrokeColor(Color strokeColor)
    {
        this.strokeColor = strokeColor;
        return this;
    }
}
