package edu.nust.engine.components.renderers;

import edu.nust.engine.components.Transform;
import edu.nust.engine.core.Component;
import edu.nust.engine.math.Vector2I;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteRenderer extends Component
{
    private final double width;
    private final double height;

    private final Image image;

    private final int frameWidth;
    private final int frameHeight;

    private final int columns;
    private final int rows;

    private Vector2I frameIndex = new Vector2I(0, 0);

    public SpriteRenderer(double width, double height, Image image, int numFramesX, int numFramesY)
    {
        this.width = width;
        this.height = height;
        this.image = image;

        this.columns = numFramesX;
        this.rows = numFramesY;

        this.frameWidth = (int) (image.getWidth() / numFramesX);
        this.frameHeight = (int) (image.getHeight() / numFramesY);
    }

    public SpriteRenderer(double width, double height, Image image)
    {
        this(width, height, image, 1, 1);
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

        // clamp frame index
        int fx = (int) Math.min(frameIndex.getX(), columns - 1);
        int fy = (int) Math.min(frameIndex.getY(), rows - 1);

        double sx = fx * this.frameWidth;
        double sy = fy * this.frameHeight;

        context.save();

        context.translate(x, y);
        context.rotate(rotation);

        context.drawImage(
                this.image,
                sx,
                sy,
                this.frameWidth,
                this.frameHeight,
                offsetX,
                offsetY,
                this.width,
                this.height
        );

        context.restore();
    }

    /* GETTERS AND SETTERS */

    public void setFrameIndex(Vector2I frameIndex)
    {
        // clamp to valid range
        int fx = (int) Math.max(0, Math.min(frameIndex.getX(), columns - 1));
        int fy = (int) Math.max(0, Math.min(frameIndex.getY(), rows - 1));

        this.frameIndex = new Vector2I(fx, fy);
    }

    public Vector2I getFrameIndex()
    {
        return frameIndex;
    }
}