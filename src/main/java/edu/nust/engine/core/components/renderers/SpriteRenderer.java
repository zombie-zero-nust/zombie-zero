package edu.nust.engine.core.components.renderers;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.core.Component;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SpriteRenderer extends Component
{
    private double width;
    private double height;

    private final Image image;

    private final int frameWidth;
    private final int frameHeight;

    private final int columns;
    private final int rows;

    private int frameIndexX = 0;
    private int frameIndexY = 0;

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

        double sx = frameIndexX * this.frameWidth;
        double sy = frameIndexY * this.frameHeight;

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

    public double getWidth() { return width; }

    public double getHeight() { return height; }

    public int getColumns() { return columns; }

    public int getRows() { return rows; }

    public int getFrameIndexX() { return frameIndexX; }

    public int getFrameIndexY() { return frameIndexY; }

    public SpriteRenderer setSize(double width, double height)
    {
        this.width = width;
        this.height = height;
        return this;
    }

    public void setFrameIndex(int frameIndexX, int frameIndexY)
    {
        this.frameIndexX = Math.max(0, Math.min(frameIndexX, columns - 1));
        this.frameIndexY = Math.max(0, Math.min(frameIndexY, rows - 1));
    }

    public void setFrameIndexX(int frameIndexX)
    {
        this.frameIndexX = Math.max(0, Math.min(frameIndexX, columns - 1));
    }

    public void setFrameIndexY(int frameIndexY)
    {
        this.frameIndexY = Math.max(0, Math.min(frameIndexY, rows - 1));
    }
}