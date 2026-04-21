package edu.nust.engine.core.components.renderers;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoxRenderer extends ShapeRenderer
{
    private final Vector2D size = new Vector2D();
    private double cornerRadius = 0;

    public BoxRenderer(double width, double height, Color fillColor)
    {
        this.size.set(width, height);
        this.fillColor = fillColor;
    }

    @Override
    public void onRender(GraphicsContext context)
    {
        Transform transform = this.gameObject.getTransform();

        double x = transform.getPosition().getX();
        double y = transform.getPosition().getY();

        double rotation = transform.getRotation().getDegrees();

        double anchorX = transform.getAnchor().getX();
        double anchorY = transform.getAnchor().getY();

        double offsetX = -this.size.getX() * anchorX;
        double offsetY = -this.size.getY() * anchorY;

        context.save();

        context.translate(x, y);
        context.rotate(rotation);

        if (filled && fillColor != null)
        {
            context.setFill(fillColor);
            context.fillRoundRect(offsetX, offsetY, this.size.getX(), this.size.getY(), cornerRadius, cornerRadius);
        }

        if (stroked && strokeColor != null)
        {
            context.setStroke(strokeColor);
            context.setLineWidth(strokeWidth);
            context.strokeRoundRect(offsetX, offsetY, this.size.getX(), this.size.getY(), cornerRadius, cornerRadius);
        }

        context.restore();
    }

    /* INITIALIZER */

    public Vector2D getSize() { return size; }

    public double getCornerRadius() { return cornerRadius; }

    public BoxRenderer setSize(Vector2D size)
    {
        this.size.set(size);
        return this;
    }

    public BoxRenderer setSize(double width, double height)
    {
        this.size.set(width, height);
        return this;
    }

    public BoxRenderer setCornerRadius(double cornerRadius)
    {
        this.cornerRadius = cornerRadius;
        return this;
    }
}