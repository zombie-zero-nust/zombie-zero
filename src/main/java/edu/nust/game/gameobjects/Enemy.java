package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.paint.Color;

/**
 * Enemy GameObjects that spawn and move towards the player
 *
 * Flow:
 * 1. Enemy spawned at a position with a given movement speed
 * 2. Every frame, targetPosition is updated (set by scene)
 * 3. onUpdate() calculates movement towards target
 * 4. Enemy moves and rotates to face target direction
 */
public class Enemy extends GameObject
{
    private Vector2D targetPosition = Vector2D.zero(); // The player position (updated each frame)
    private double movementSpeed = 100; // units per second - how fast enemy moves
    private double size = 50; // Size of the red box renderer

    /**
     * Constructor - Initialize enemy at a starting position with movement speed
     * @param startPosition Where the enemy spawns
     * @param speed How fast the enemy moves (units/sec)
     */
    public Enemy(Vector2D startPosition, double speed)
    {
        this.movementSpeed = speed; // Store the movement speed
        this.getTransform().setPosition(startPosition); // Set starting position

        // Create and add a red box renderer (visual representation of enemy)
        BoxRenderer boxRenderer = new BoxRenderer(size, size, Color.RED);
        this.addComponent(boxRenderer);
    }

    @Override
    public void onInit()
    {
        // Enemy initialization complete - no special setup needed
    }

    /**
     * Called every frame - handles enemy movement towards target
     * @param deltaTime Time elapsed since last frame
     */
    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        // Move the enemy towards the target position each frame
        moveTowardsTarget(deltaTime);
    }

    /**
     * Update the target position (called by scene to point at player)
     * @param target The player's current position
     */
    public void setTargetPosition(Vector2D target)
    {
        // Copy target coordinates into our targetPosition vector
        this.targetPosition.set(target);
    }

    /**
     * Move the enemy step by step towards the target
     *
     * Algorithm:
     * 1. Get current enemy position
     * 2. Calculate direction vector from enemy to target
     * 3. Normalize the direction (convert to unit vector)
     * 4. Calculate how far to move this frame (speed * deltaTime)
     * 5. Apply movement in that direction
     * 6. Rotate enemy to face the target
     *
     * @param deltaTime Time elapsed this frame (used for smooth movement)
     */
    private void moveTowardsTarget(TimeSpan deltaTime)
    {
        // Get the enemy's current position
        Vector2D currentPosition = this.getTransform().getPosition();

        // Calculate the vector from enemy to target (target - current)
        Vector2D directionToTarget = Vector2D.subtract(targetPosition, currentPosition);

        // Calculate the distance between enemy and target
        double distanceToTarget = directionToTarget.magnitude();

        // Only move if there's a target distance (avoid division by zero)
        if (distanceToTarget > 0)
        {
            // Normalize the direction vector to unit length (1.0)
            // This makes movement speed consistent regardless of distance
            directionToTarget = directionToTarget.normalize();

            // Calculate movement distance for this frame
            // movementDistance = speed (units/sec) * deltaTime (seconds)
            // Example: 100 units/sec * 0.016 sec = 1.6 units moved this frame
            double movementDistance = movementSpeed * deltaTime.asSeconds();

            // Scale the direction by movement distance to get movement vector
            Vector2D movement = Vector2D.multiply(directionToTarget, movementDistance);

            // Add movement to current position to get new position
            Vector2D newPosition = currentPosition.add(movement);

            // Update enemy position for rendering
            this.getTransform().setPosition(newPosition);

            // Rotate enemy to face target direction
            // atan2 calculates angle from x and y components
            // This makes the enemy visually point towards the target
            double angleToTarget = Math.atan2(directionToTarget.getY(), directionToTarget.getX());
            this.getTransform().setRotationRadians(angleToTarget);
        }
    }

    // Getter/Setter for movement speed
    public double getMovementSpeed() { return movementSpeed; }
    public void setMovementSpeed(double speed) { this.movementSpeed = speed; }
}



