package edu.nust.engine.components.renderers;

import edu.nust.engine.components.Transform;
import edu.nust.engine.core.Component;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteRenderer extends Component
{
    private final double width;
    private final double height;
    private final Image image;

    public SpriteRenderer(double width, double height, Image image)
    {
        this.width = width;
        this.height = height;
        this.image = image;
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

        double offsetX = -this.width * anchorX;
        double offsetY = -this.height * anchorY;

        context.save();

        context.translate(x, y);
        context.rotate(rotation);

        context.drawImage(this.image, offsetX, offsetY, this.width, this.height);

        context.restore();
    }
}