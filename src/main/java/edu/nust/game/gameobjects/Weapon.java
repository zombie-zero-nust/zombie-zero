package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.paint.Color;

public class Weapon extends GameObject
{
    private OrbitingBox orbitComponent;
    private final double orbitDistance = 80;
    private boolean isFiring = false;
    private boolean autoFire = true;
    private double fireRate = 20;
    private double fireCooldown = 0;
    private Ammo ammo;

    public Weapon()
    {
        orbitComponent = new OrbitingBox(orbitDistance);
        this.addComponent(orbitComponent);
        this.addComponent(new BoxRenderer(40, 40, Color.CYAN));
        ammo = new AmmoImpl();
    }

    public void updatePosition(Vector2D mousePos, Vector2D playerPos)
    {
        if (orbitComponent != null)
        {
            orbitComponent.updateMousePosition(mousePos);
            orbitComponent.updatePositionBasedOnMouse(playerPos);
        }
    }

    public Bullet fireWeapon(Vector2D targetPos,TimeSpan deltaTime)
    {
        Vector2D weaponPos = this.getTransform().getPosition();
        ammo.update(deltaTime);

        if (ammo.isReloading() || !ammo.hasAmmo())
            return null;

        if(isFiring){
            if(!autoFire){
                setFiring(false);
                ammo.decreaseAmmo();
                return new Bullet(2000, weaponPos, 1000, 30, 30, targetPos);
            }
            fireCooldown -= deltaTime.asSeconds();
            if(fireCooldown <= 0.01) {
                fireCooldown = 1.0/fireRate;
                ammo.decreaseAmmo();
                return new Bullet(2000, weaponPos, 1000, 30, 30, targetPos);
            }
        }
        return null;
    }

    public void setFiring(boolean isFiring){
        this.isFiring = isFiring;
    }

    public Vector2D getWeaponPosition()
    {
        return this.getTransform().getPosition();
    }

    public Ammo getAmmo()
    {
        return ammo;
    }

    public void reload()
    {
        ammo.startReload();
    }
}

