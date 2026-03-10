package edu.nust.engine.core.components.renderers;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.core.Component;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CircleRenderer extends Component
{
    private final double radius;
    private final Color color;

    public CircleRenderer(double radius, Color color)
    {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void onRender(GraphicsContext context)
    {
        context.setFill(this.color);
        Transform transform = this.gameObject.getTransform();

        double x = transform.getPosition().getX();
        double y = transform.getPosition().getY();

        double rotation = transform.getRotation().getDegrees();

        double anchorX = transform.getAnchor().getX();
        double anchorY = transform.getAnchor().getY();

        double offsetX = -this.radius * anchorX;
        double offsetY = -this.radius * anchorY;

        context.save();

        context.translate(x, y);
        context.rotate(rotation);

        context.fillOval(offsetX, offsetY, this.radius, this.radius);

        context.restore();
    }
}