package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.Vector2D;
import javafx.scene.paint.Color;

public class Weapon extends GameObject
{
    private OrbitingBox orbitComponent;
    private final double orbitDistance = 80;

    public Weapon()
    {
        orbitComponent = new OrbitingBox(orbitDistance);
        this.addComponent(orbitComponent);

        BoxRenderer boxRenderer = new BoxRenderer(40, 40, Color.CYAN);
        this.addComponent(boxRenderer);
    }

    public void updatePosition(Vector2D mousePos, Vector2D playerPos)
    {
        if (orbitComponent != null)
        {
            orbitComponent.updateMousePosition(mousePos);
            orbitComponent.updatePositionBasedOnMouse(playerPos);
        }
    }

    public Bullet fireWeapon(Vector2D targetPos)
    {
        Vector2D weaponPos = this.getTransform().getPosition();
        return new Bullet(1000, weaponPos, 1000, 30, 30, targetPos);
    }

    public Vector2D getWeaponPosition()
    {
        return this.getTransform().getPosition();
    }
}

