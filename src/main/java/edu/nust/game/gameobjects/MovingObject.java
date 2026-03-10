package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.math.Angle;
import edu.nust.engine.math.Vector2D;
import javafx.scene.paint.Color;

public class MovingObject extends GameObject
{
    public MovingObject()
    {
        this.getTransform().setPosition(new Vector2D(100, 100));
        this.getTransform().setRotation(new Angle(25));
        this.addComponent(new BoxRenderer(50, 50, Color.BLUE));
    }

    @Override
    protected void onUpdate()
    {
        super.onUpdate();
        this.getTransform().translateForward(2);
        this.getTransform().rotate(new Angle(1));
    }
}
