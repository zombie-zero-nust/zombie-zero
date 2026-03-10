package edu.nust.game.gameobjects;

import edu.nust.engine.components.renderers.SpriteRenderer;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.math.Angle;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.math.Vector2UI;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

public class GameObjectA extends GameObject
{
    public GameObjectA()
    {
        this.getTransform().setPosition(new Vector2D(100, 100));
        this.getTransform().setAnchor(new Vector2UI(0, 0));
        this.getTransform().setRotation(new Angle(25));
        Image test = Resources.loadImage("assets", "images", "test.png");
        this.addComponent(new SpriteRenderer(50, 50, test));
    }

    @Override
    protected void onUpdate()
    {
        super.onUpdate();
        this.getTransform().translateForward(2);
        this.getTransform().rotate(new Angle(1));
    }
}
