package edu.nust.engine.core.components.renderers;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.core.interfaces.WorldBoundsProvider;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoxRenderer extends ShapeRenderer implements WorldBoundsProvider
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

    @Override
    public Rectangle getWorldBounds()
    {
        Transform transform = this.gameObject.getTransform();
        double x = transform.getPosition().getX();
        double y = transform.getPosition().getY();

        double anchorX = transform.getAnchor().getX();
        double anchorY = transform.getAnchor().getY();

        double maxCornerDistance = getMaxCornerDistance(anchorX, anchorY);

        return Rectangle.fromCorners(
                x - maxCornerDistance,
                y - maxCornerDistance,
                x + maxCornerDistance,
                y + maxCornerDistance
        );
    }

    private double getMaxCornerDistance(double anchorX, double anchorY)
    {
        double width = this.size.getX();
        double height = this.size.getY();

        double localLeft = Math.min(-width * anchorX, -width * anchorX + width);
        double localRight = Math.max(-width * anchorX, -width * anchorX + width);
        double localTop = Math.min(-height * anchorY, -height * anchorY + height);
        double localBottom = Math.max(-height * anchorY, -height * anchorY + height);

        return Math.max(
                Math.max(Math.hypot(localLeft, localTop), Math.hypot(localRight, localTop)),
                Math.max(Math.hypot(localLeft, localBottom), Math.hypot(localRight, localBottom))
        );
    }
}