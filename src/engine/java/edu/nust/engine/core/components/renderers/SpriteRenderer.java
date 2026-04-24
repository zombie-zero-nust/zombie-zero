package edu.nust.engine.core.components.renderers;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.components.Transform;
import edu.nust.engine.core.interfaces.WorldBoundsProvider;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.TimeSpan;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SpriteRenderer extends Component implements WorldBoundsProvider
{
    private double width;
    private double height;

    private Image image;

    private Effect tintEffect = null;
    private double opacity = 1.0;

    private double frameWidth;
    private double frameHeight;

    private int columns;
    private int rows;

    private int frameX = 0;
    private int frameY = 0;

    private boolean animating = false;
    private TimeSpan animationTime = TimeSpan.zero();

    private TimeSpan elapsedAnimationTime = TimeSpan.zero();

    public SpriteRenderer(double width, double height, Image image, int numFramesX, int numFramesY)
    {
        this.width = width;
        this.height = height;
        setImage(image, numFramesX, numFramesY);
    }

    public SpriteRenderer(double width, double height, Image image)
    {
        this(width, height, image, 1, 1);
    }

    public SpriteRenderer(Image image)
    {
        this(image.getWidth(), image.getHeight(), image, 1, 1);
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (animating)
        {
            elapsedAnimationTime = elapsedAnimationTime.add(deltaTime);
            while (elapsedAnimationTime.subtract(animationTime).asSeconds() >= 0)
            {
                elapsedAnimationTime = elapsedAnimationTime.subtract(animationTime);
                nextFrame();
            }
        }
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

        double sx = frameX * this.frameWidth;
        double sy = frameY * this.frameHeight;

        context.save();

        context.translate(x, y);
        context.rotate(rotation);

        context.setEffect(this.tintEffect);

        double defaultAlpha = context.getGlobalAlpha();
        context.setGlobalAlpha(this.opacity);

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

        context.setGlobalAlpha(defaultAlpha);

        context.setEffect(null);

        context.restore();
    }

    /* TINT IMAGE */

    public SpriteRenderer tintSelf(Color tintColor)
    {
        if (this.image == null) return this;

        // Create a flat lighting effect
        Lighting lighting = new Lighting();

        // These settings ensure the light acts as a flat color multiplier
        // without adding 3D shadows or shiny highlights
        lighting.setDiffuseConstant(1.0);
        lighting.setSpecularConstant(0.0);
        lighting.setSpecularExponent(0.0);
        lighting.setSurfaceScale(0.0);

        // Create a distant light with your target tint color
        lighting.setLight(new Light.Distant(45, 45, tintColor));

        this.tintEffect = lighting;

        return this;
    }

    public SpriteRenderer clearTint()
    {
        this.tintEffect = null;
        return this;
    }

    /* ANIMATION */

    public boolean isAnimating() { return animating; }

    public TimeSpan getAnimationTime() { return animationTime; }

    public SpriteRenderer setAnimating(boolean animating)
    {
        this.animating = animating;
        return this;
    }

    public SpriteRenderer setAnimationTime(TimeSpan animationTime)
    {
        this.animationTime = animationTime;
        return this;
    }

    public SpriteRenderer startAnimation()
    {
        setFrame(0, 0);
        elapsedAnimationTime = TimeSpan.zero();
        return setAnimating(true);
    }

    public SpriteRenderer stopAnimation()
    {
        setFrame(0, 0);
        elapsedAnimationTime = TimeSpan.zero();
        return setAnimating(false);
    }

    public SpriteRenderer pauseAnimation() { return setAnimating(false); }

    public SpriteRenderer resumeAnimation() { return setAnimating(true); }

    public int getFrameX() { return frameX; }

    public int getFrameY() { return frameY; }

    public SpriteRenderer setFrame(int x, int y)
    {
        this.frameX = Math.clamp(x, 0, columns - 1);
        this.frameY = Math.clamp(y, 0, rows - 1);
        return this;
    }

    public SpriteRenderer setFrameX(int frameX) { return setFrame(frameX, this.frameY); }

    public SpriteRenderer setFrameY(int frameY) { return setFrame(this.frameX, frameY); }

    public SpriteRenderer nextFrame()
    {
        this.frameX = (this.frameX + 1) % columns;
        if (this.frameX == 0) this.frameY = (this.frameY + 1) % rows;
        return this;
    }

    public SpriteRenderer previousFrame()
    {
        this.frameX = (this.frameX - 1 + columns) % columns;
        if (this.frameX == columns - 1) this.frameY = (this.frameY - 1 + rows) % rows;
        return this;
    }

    /* FLIP */

    public SpriteRenderer flipHorizontal()
    {
        this.width = -Math.abs(this.width);
        return this;
    }

    public SpriteRenderer flipVertical()
    {
        this.height = -Math.abs(this.height);
        return this;
    }

    public SpriteRenderer unFlipVertical()
    {
        this.height = Math.abs(this.height);
        return this;
    }

    /* GETTERS AND SETTERS */

    public Image getImage() { return image; }

    public double getFrameWidth() { return frameWidth; }

    public double getFrameHeight() { return frameHeight; }

    public double getWidth() { return width; }

    public double getHeight() { return height; }

    public int getColumns() { return columns; }

    public int getRows() { return rows; }

    public double getOpacity() { return opacity; }

    public SpriteRenderer setImage(Image image, int numFramesX, int numFramesY)
    {
        this.image = image;

        this.columns = numFramesX;
        this.rows = numFramesY;

        this.frameWidth = image.getWidth() / numFramesX;
        this.frameHeight = image.getHeight() / numFramesY;

        return this;
    }

    public SpriteRenderer setSize(double width, double height)
    {
        this.width = width;
        this.height = height;
        return this;
    }

    public SpriteRenderer setOpacity(double opacity)
    {
        this.opacity = Math.clamp(opacity, 0.0, 1.0);
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
        double localLeft = Math.min(-this.width * anchorX, -this.width * anchorX + this.width);
        double localRight = Math.max(-this.width * anchorX, -this.width * anchorX + this.width);
        double localTop = Math.min(-this.height * anchorY, -this.height * anchorY + this.height);
        double localBottom = Math.max(-this.height * anchorY, -this.height * anchorY + this.height);

        // Conservative bound around anchor point that remains safe for any rotation.
        return Math.max(
                Math.max(Math.hypot(localLeft, localTop), Math.hypot(localRight, localTop)),
                Math.max(Math.hypot(localLeft, localBottom), Math.hypot(localRight, localBottom))
        );
    }
}