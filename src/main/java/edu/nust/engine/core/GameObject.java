package edu.nust.engine.core;

import edu.nust.engine.math.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameObject
{
    public Vector2 position = new Vector2();
    protected GameScene scene;

    public GameObject(GameScene scene)
    {
        this.scene = scene;
    }

    void onInit()
    {
        System.out.println("GameObject initialized at position: " + position.getX() + ", " + position.getY());
    }

    void onUpdate()
    {
    }

    void onRender(GraphicsContext context)
    {
        // TODO: Add rendering logic from Components
        context.setFill(Color.BLUE);
        context.fillRect(position.getX(), position.getY(), 10, 10);
    }
}
