package edu.nust.game.gameobjects;

import edu.nust.engine.core.Component;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;

/**
 * Weapon component that orbits around the character and follows mouse position
 *
 * Flow:
 * 1. Scene tracks mouse position every frame
 * 2. Scene calls updateMousePosition() with world mouse coordinates
 * 3. Scene calls updatePositionBasedOnMouse() with character position
 * 4. Weapon calculates direction from character to mouse
 * 5. Weapon positions itself at fixed distance in that direction
 * 6. Weapon rotates to point at mouse
 */
public class OrbitingBox extends Component
{
    private final double orbitDistance; // Fixed distance from character (e.g., 80 units)
    private Vector2D mousePosition = Vector2D.zero(); // Current world mouse position

    /**
     * Constructor - Set the orbit distance for the weapon
     * @param orbitDistance How far the weapon stays from character (80 = 80 units away)
     */
    public OrbitingBox(double orbitDistance)
    {
        this.orbitDistance = orbitDistance;
    }

    /**
     * Called every frame - weapon doesn't move on its own
     * Movement is handled by updatePositionBasedOnMouse()
     * @param deltaTime Time elapsed since last frame (unused here)
     */
    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        // No automatic movement - weapon is controlled by scene
        // Scene calls updatePositionBasedOnMouse() directly
    }

    /**
     * Update the weapon's knowledge of where the mouse is
     * Called by scene with world coordinates (not screen coordinates)
     * @param mousePos The current mouse position in world space
     */
    public void updateMousePosition(Vector2D mousePos)
    {
        // Store the mouse position for use in updatePositionBasedOnMouse()
        this.mousePosition = mousePos;
    }

    /**
     * Calculate weapon position and rotation based on mouse relative to character
     *
     * Algorithm:
     * 1. Calculate direction vector from character to mouse
     * 2. Check if mouse is at a valid distance from character
     * 3. If valid:
     *    - Normalize direction to unit vector
     *    - Multiply by orbitDistance to place weapon
     *    - Calculate rotation angle to point at mouse
     * 4. If invalid (mouse at character position):
     *    - Default to right side (orbitDistance, 0)
     * 5. Apply position and rotation to weapon
     *
     * @param characterPosition The player's current position
     */
    public void updatePositionBasedOnMouse(Vector2D characterPosition)
    {
        // Calculate the vector pointing from character towards mouse
        Vector2D directionToMouse = Vector2D.subtract(mousePosition, characterPosition);

        // Get the distance between character and mouse
        double distance = directionToMouse.magnitude();

        Vector2D newPosition;

        // Check if there's a valid direction (not at same position)
        if (distance > 0)
        {
            // Normalize to unit vector (length = 1.0)
            // This ensures consistent weapon distance regardless of mouse distance
            directionToMouse = directionToMouse.normalize();

            // Position weapon at fixed orbit distance from character
            // Formula: character position + (normalized direction * orbitDistance)
            newPosition = characterPosition.add(Vector2D.multiply(directionToMouse, orbitDistance));
            
            // Calculate rotation angle to make weapon point at mouse
            // atan2(y, x) gives angle in radians from -π to π
            // This visually makes the weapon aim towards the mouse
            double angleToMouse = Math.atan2(directionToMouse.getY(), directionToMouse.getX());
            this.gameObject.getTransform().setRotationRadians(angleToMouse);
        }
        else
        {
            // Mouse is at character position - use default position
            // Place weapon to the right of character (orbitDistance, 0)
            newPosition = characterPosition.add(orbitDistance, 0);
            // Keep current rotation (no change needed)
        }

        // Apply calculated position to the weapon GameObject
        this.gameObject.getTransform().setPosition(newPosition);
    }
}




