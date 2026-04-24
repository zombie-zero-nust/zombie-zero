package edu.nust.engine.core.interfaces;

import edu.nust.engine.core.Component;
import edu.nust.engine.math.Rectangle;

/**
 * Contract for render components that can provide conservative world-space bounds. The Engine only renders the said
 * object, if the given bounds intersects with the camera's viewport.
 * <br><br>
 * Used for Frustum Culling
 * <br><br>
 * Implement in {@link Component}
 */
public interface WorldBoundsProvider
{
    /// The Bounds of the object
    Rectangle getWorldBounds();
}
