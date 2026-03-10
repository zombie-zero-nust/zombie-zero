package edu.nust.game.gameobjects;

import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.math.Angle;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.math.Vector2I;
import edu.nust.engine.math.Vector2UI;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

public class MovingObject extends GameObject
{
    public MovingObject()
    {
        this.getTransform().setPosition(new Vector2D(100, 100));
        this.getTransform().setAnchor(new Vector2UI(0, 0));
        this.getTransform().setRotation(new Angle(25));
        Image test = Resources.loadImage("assets", "images", "test.png");
        this.addComponent(new SpriteRenderer(50, 50, test, 1, 1));
    }

    @Override
    protected void onUpdate()
    {
        super.onUpdate();
        this.getTransform().translateForward(2);
        this.getTransform().rotate(new Angle(1));
        SpriteRenderer renderer = this.getComponent(SpriteRenderer.class);
        if (renderer == null) return;

        if (System.currentTimeMillis() % 1000 < 500)
        {
            renderer.setFrameIndex(new Vector2I(0, 0));
        }
        else
        {
            renderer.setFrameIndex(new Vector2I(1, 0));
        }
    }
}
