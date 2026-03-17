package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.Angle;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.paint.Color;

public class MovingObject extends GameObject
{
    public MovingObject()
    {
        this.getTransform().setPosition(new Vector2D(100, 100));
        this.getTransform().setRotation(new Angle(25));
        this.addComponent(new BoxRenderer(50, 50, Color.AQUA));
    }

    @Override
    protected void onUpdate(TimeSpan deltaTime)
    {
        super.onUpdate(deltaTime);
        this.getTransform().translateForward(2);
        this.getTransform().rotate(new Angle(1));

//        this.getScene().getCamera().setPosition(this.getTransform().getPosition());
    }
}