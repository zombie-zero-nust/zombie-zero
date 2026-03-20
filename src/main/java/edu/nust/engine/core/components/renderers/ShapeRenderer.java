package edu.nust.engine.core.components.renderers;

import edu.nust.engine.core.Component;
import edu.nust.engine.math.TimeSpan;
import javafx.scene.paint.Color;

abstract class ShapeRenderer extends Component
{
    protected boolean filled = true;
    protected Color fillColor;

    protected boolean stroked = true;
    protected Color strokeColor = Color.BLACK;
    protected double strokeWidth = 4;

    /* INITIALIZER */

    public Color getFillColor() { return fillColor; }

    public boolean isFilled() { return filled; }

    public Color getStrokeColor() { return strokeColor; }

    public double getStrokeWidth() { return strokeWidth; }

    public boolean isStroked() { return stroked; }

    public ShapeRenderer setFillColor(Color fillColor)
    {
        this.fillColor = fillColor;
        return this;
    }

    public ShapeRenderer setFillOpacity(double opacity)
    {
        if (fillColor == null || !filled) return this;
        this.fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), opacity);
        return this;
    }

    public ShapeRenderer setFilled(boolean filled)
    {
        this.filled = filled;
        return this;
    }

    public ShapeRenderer setStrokeColor(Color strokeColor)
    {
        this.strokeColor = strokeColor;
        return this;
    }

    public ShapeRenderer setStrokeOpacity(double opacity)
    {
        if (strokeColor == null || !stroked) return this;
        this.strokeColor = new Color(strokeColor.getRed(), strokeColor.getGreen(), strokeColor.getBlue(), opacity);
        return this;
    }

    public ShapeRenderer setStrokeWidth(double strokeWidth)
    {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public ShapeRenderer setStroked(boolean stroked)
    {
        this.stroked = stroked;
        return this;
    }
}