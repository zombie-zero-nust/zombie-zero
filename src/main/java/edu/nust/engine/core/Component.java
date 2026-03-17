package edu.nust.engine.core;

import edu.nust.engine.math.TimeSpan;
import javafx.scene.canvas.GraphicsContext;

public abstract class Component
{
    protected GameObject gameObject;

    protected void setGameObject(GameObject gameObject)
    {
        this.gameObject = gameObject;
    }

    public GameObject getGameObject()
    {
        return gameObject;
    }

    /* LIFETIME */

    public void onInit() {}

    public void onUpdate(TimeSpan deltaTime) {}

    public void onRender(GraphicsContext context) {}
}