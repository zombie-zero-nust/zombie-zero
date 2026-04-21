package edu.nust.game.scenes.levelscene.gameobjects.weapon;

import edu.nust.engine.core.Component;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;

// Weapon component that orbits around character and follows mouse
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
        // Positioning handled by scene
    }

    public void updateMousePosition(Vector2D mousePos)
    {
        this.mousePosition = mousePos;
    }

    public void updatePositionBasedOnMouse(Vector2D characterPosition)
    {
        Vector2D directionToMouse = Vector2D.subtract(mousePosition, characterPosition);
        double distance = directionToMouse.magnitude();

        Vector2D newPosition;

        if (distance > 0)
        {
            directionToMouse = directionToMouse.normalize();
            newPosition = characterPosition.add(Vector2D.multiply(directionToMouse, orbitDistance));
            double angleToMouse = Math.atan2(directionToMouse.getY(), directionToMouse.getX());
            this.gameObject.getTransform().setRotationRadians(angleToMouse);
        }
        else
        {
            newPosition = characterPosition.add(orbitDistance, 0);
        }

        this.gameObject.getTransform().setPosition(newPosition);
    }
}