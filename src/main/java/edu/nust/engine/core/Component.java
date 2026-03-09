package edu.nust.engine.core;

import javafx.scene.canvas.GraphicsContext;

public abstract class Component
{
    protected GameObject gameObject;

    public void setGameObject(GameObject gameObject)
    {
        this.gameObject = gameObject;
    }

    public GameObject getGameObject()
    {
        return gameObject;
    }

    /* LIFETIME */

    public void onInit()
    {
    }

    public void onUpdate()
    {
    }

    public void onRender(GraphicsContext context)
    {
    }
}