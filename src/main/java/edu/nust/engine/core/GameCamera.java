package edu.nust.engine.core;

import edu.nust.engine.math.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameCamera
{
    public static final Logger LOGGER = LoggerFactory.getLogger(GameCamera.class);

    private Vector2D position = Vector2D.zero();
    private double zoom = 1.0;

    /* POSITION */

    public Vector2D getPosition() { return position; }

    public GameCamera setPosition(Vector2D position)
    {
        this.position = position;
        return this;
    }

    public GameCamera translate(Vector2D offset)
    {
        this.position.addSelf(offset);
        return this;
    }

    public GameCamera translate(double x, double y)
    {
        this.position.addSelf(x, y);
        return this;
    }

    public GameCamera translateX(double x)
    {
        this.position.addSelf(x, 0);
        return this;
    }

    public GameCamera translateY(double y)
    {
        this.position.addSelf(0, y);
        return this;
    }

    /* ZOOM */

    public double getZoom() { return zoom; }

    public void setZoom(double zoom)
    {
        if (zoom <= 0)
        {
            LOGGER.warn("Attempted to set zoom to non-positive value: {}", zoom);
            return;
        }

        this.zoom = zoom;
    }

    public void incrementZoom(double factor)
    {
        setZoom(this.zoom * factor);
    }

    public void decrementZoom(double factor)
    {
        setZoom(this.zoom / factor);
    }
}
