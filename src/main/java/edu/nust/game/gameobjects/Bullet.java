package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;



public class Bullet extends GameObject {
    private int speed;
    private Vector2D pos;
    private Vector2D direction;
    private Image image;

    private double totalDistance;
    private double range;
    private int height;
    private int width;
    private boolean destroyed = false;

    public Bullet(int speed, Vector2D pos, double range, int height, int width, Vector2D mousePos) {
        this.speed = speed;
        this.pos = pos;
        totalDistance = 0;
        this.range = range;
        this.height = height;
        this.width = width;
        direction = mousePos.subtract(pos).normalize();
        try {
            image = (Resources.loadImageOrThrow("assets", "images", "test.png"));

        } catch (FileNotFoundException ignored) {
        }
        this.addComponent(new SpriteRenderer(width, height, image));
        this.getTransform().setPosition(pos);


    }

    public Bullet(int speed, Vector2D pos, Image image, double range, int height, int width, Vector2D mousePos) {
        this.speed = speed;
        this.pos = pos;
        this.image = image;
        totalDistance = 0;
        this.range = range;
        this.height = height;
        this.width = width;
        this.addComponent(new SpriteRenderer(width, height, image));
        this.getTransform().setPosition(pos);


    }



    @Override
    public void onUpdate(TimeSpan deltaTime) {
        Vector2D moveDistance = direction.multiply(speed * deltaTime.asSeconds());
        pos = pos.add(moveDistance);
        double angle = Math.atan2(direction.getY(), direction.getX());
        this.getTransform().setPosition(pos);
        this.getTransform().setRotationRadians(angle);
        totalDistance += moveDistance.magnitude();
        if (totalDistance >= range) {
            destroyed = true;
        }
    }

    @Override
    public void lateUpdate(TimeSpan deltaTime){
        if(destroyed) this.destroy();
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}