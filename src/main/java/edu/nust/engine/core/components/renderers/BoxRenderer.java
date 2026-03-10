package edu.nust.engine.core.components.renderers;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.core.Component;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BoxRenderer extends Component
{
    private final double width;
    private final double height;
    private final Color color;

    public BoxRenderer(double width, double height, Color color)
    {
        this.width = width;
        this.height = height;
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

        double offsetX = -this.width * anchorX;
        double offsetY = -this.height * anchorY;

        context.save();

        context.translate(x, y);
        context.rotate(rotation);

        context.fillRect(offsetX, offsetY, this.width, this.height);

        context.restore();
    }
}