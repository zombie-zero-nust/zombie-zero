package edu.nust.engine.core.components.renderers;

import edu.nust.engine.core.components.Transform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CircleRenderer extends ShapeRenderer
{
    private double radius;

    public CircleRenderer(double radius, Color fillColor)
    {
        this.radius = radius;
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

        double diameter = this.radius * 2;

        double offsetX = -diameter * anchorX;
        double offsetY = -diameter * anchorY;

        context.save();

        context.translate(x, y);
        context.rotate(rotation);

        if (filled && fillColor != null)
        {
            context.setFill(fillColor);
            context.fillOval(offsetX, offsetY, diameter, diameter);
        }

        if (stroked && strokeColor != null)
        {
            context.setStroke(strokeColor);
            context.setLineWidth(strokeWidth);
            context.strokeOval(offsetX, offsetY, diameter, diameter);
        }

        context.restore();
    }

    /* INITIALIZER */

    public double getRadius() { return radius; }

    public CircleRenderer setRadius(double radius)
    {
        this.radius = radius;
        return this;
    }
}