package edu.nust.engine.core;

import edu.nust.engine.math.Vector2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a camera in the {@link GameScene}, controlling the view of the scene. Contains position and zoom
 * properties.
 * <br><br>
 * <b>{@code NOTE}</b> : The camera's position represents the center of the view.
 * <br><br>
 * This class does not handle any rendering or transformations itself; it simply stores the camera's state.
 */
public class GameCamera
{
    public static final Logger LOGGER = LoggerFactory.getLogger(GameCamera.class);
    private static final double DEFAULT_TRAUMA_DECAY_PER_SECOND = 3.0;

    private Vector2D position = Vector2D.zero();
    private Vector2D shakeOffset = Vector2D.zero();
    private double zoom = 1.0;
    private double shakeIntensity = 0.0;
    private double maxShakeDistance = 0.0;

    /* POSITION */

    /// Gets the current position of the camera. This represents the center of the view.
    public Vector2D getPosition() { return position; }

    /// **`INTERNAL`** Gets the position used for rendering, including the current shake offset.
    Vector2D getRenderPosition() { return position.add(shakeOffset); }

    /// **`CHAINABLE`** Sets the position of the camera. This represents the center of the view.
    public GameCamera setPosition(Vector2D position)
    {
        this.position = position;
        return this;
    }

    /// **`CHAINABLE`** Sets the position of the camera. This represents the center of the view.
    public GameCamera setPosition(double x, double y)
    {
        this.position = new Vector2D(x, y);
        return this;
    }

    /// **`CHAINABLE`** Moves the camera by the specified offset. Adds to the existing position.
    public GameCamera translate(Vector2D offset)
    {
        this.position.addSelf(offset);
        return this;
    }

    /// **`CHAINABLE`** Moves the camera by the specified offset. Adds to the existing position, effectively translating
    /// the view.
    public GameCamera translate(double x, double y)
    {
        this.position.addSelf(x, y);
        return this;
    }

    /// **`CHAINABLE`** Moves the camera horizontally by the specified amount. Adds to the existing position.
    public GameCamera translateX(double x)
    {
        this.position.addSelf(x, 0);
        return this;
    }

    /// **`CHAINABLE`** Moves the camera vertically by the specified amount. Adds to the existing position.
    public GameCamera translateY(double y)
    {
        this.position.addSelf(0, y);
        return this;
    }

    /* ZOOM */

    /// Gets the current zoom level of the camera. A zoom of 1.0 means no zoom, greater than 1.0 means zoomed in, and
    /// less than 1.0 means zoomed out.
    public double getZoom() { return zoom; }

    /// **`CHAINABLE`** Sets the zoom level of the camera. A zoom of 1.0 means no zoom, greater than 1.0 means zoomed
    /// in, and less than 1.0 means zoomed out.
    /// <br><br>
    /// Zoom must be a positive value.
    public GameCamera setZoom(double zoom)
    {
        if (zoom <= 0)
        {
            LOGGER.warn("Attempted to set zoom to non-positive value: {}", zoom);
            return this;
        }

        this.zoom = zoom;
        return this;
    }

    /// **`CHAINABLE`** Increases the zoom level of the camera by multiplying the current zoom by the specified factor.
    /// A factor greater than 1.0 will zoom in, while a factor between 0 and 1.0 will zoom out.
    /// <br><br>
    /// Factor must be a positive value.
    public GameCamera incrementZoom(double factor) { return setZoom(this.zoom * factor); }

    /// **`CHAINABLE`** Decreases the zoom level of the camera by dividing the current zoom by the specified factor. A
    /// factor greater than 1.0 will zoom out, while a factor between 0 and 1.0 will zoom in.
    /// <br><br>
    /// Factor must be a positive value.
    public GameCamera decrementZoom(double factor) { return setZoom(this.zoom / factor); }

    /* SHAKE */

    public boolean isShaking() { return shakeIntensity > 0.0; }

    public GameCamera shake(double intensity, double maxDistance)
    {
        if (intensity <= 0 || maxDistance <= 0)
            return this;

        shakeIntensity = Math.clamp(shakeIntensity + intensity, 0.0, 1.0);
        maxShakeDistance = Math.max(maxShakeDistance, maxDistance);
        return this;
    }

    public GameCamera update(double deltaSeconds)
    {
        if (deltaSeconds <= 0)
            return this;

        if (!isShaking())
        {
            shakeOffset = Vector2D.zero();
            return this;
        }

        double shakeStrength = shakeIntensity;

        if (shakeStrength <= 0.0)
        {
            shakeOffset = Vector2D.zero();
            maxShakeDistance = 0.0;
            return this;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        double offsetX = random.nextDouble(-1.0, 1.0) * maxShakeDistance * shakeStrength;
        double offsetY = random.nextDouble(-1.0, 1.0) * maxShakeDistance * shakeStrength;
        shakeOffset = new Vector2D(offsetX, offsetY);

        shakeIntensity = Math.max(0.0, shakeIntensity - DEFAULT_TRAUMA_DECAY_PER_SECOND * deltaSeconds);
        if (shakeIntensity <= 0.0)
            maxShakeDistance = 0.0;

        return this;
    }
}
