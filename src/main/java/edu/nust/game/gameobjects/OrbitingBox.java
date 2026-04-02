package edu.nust.game.gameobjects;

import edu.nust.engine.core.Component;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;


// A component that makes a weapon follow the mouse position.
//The weapon stays at a fixed distance from the character and points towards the mouse.

public class OrbitingBox extends Component
{
    private final double orbitDistance;
    private Vector2D mousePosition = Vector2D.zero();

    public OrbitingBox(double orbitDistance)
    {
        this.orbitDistance = orbitDistance;
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
    }


     // Update the mouse position (called from the scene)
    public void updateMousePosition(Vector2D mousePos)
    {
        this.mousePosition = mousePos;
    }


     // Calculate and update the weapon position based on mouse relative to character
    public void updatePositionBasedOnMouse(Vector2D characterPosition)
    {
        // Calculate direction from character to mouse
        Vector2D directionToMouse = Vector2D.subtract(mousePosition, characterPosition);
        double distance = directionToMouse.magnitude();

        Vector2D newPosition;
        // Normalize and apply orbit distance
        if (distance > 0)
        {
            directionToMouse = directionToMouse.normalize();
            newPosition = characterPosition.add(Vector2D.multiply(directionToMouse, orbitDistance));
            
            // Rotate weapon to point towards mouse
            double angleToMouse = Math.atan2(directionToMouse.getY(), directionToMouse.getX());
            this.gameObject.getTransform().setRotationRadians(angleToMouse);
        }
        else
        {
            // Mouse is at character position, default to the right
            newPosition = characterPosition.add(orbitDistance, 0);
        }

        // Update the weapon position
        this.gameObject.getTransform().setPosition(newPosition);
    }
}




