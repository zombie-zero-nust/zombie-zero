package edu.nust.engine.core;

import edu.nust.engine.math.TimeSpan;
import javafx.scene.canvas.GraphicsContext;

public abstract class Component
{
    protected GameObject gameObject;
    /// Whether this should update self or not
    protected boolean active = true;

    /* LIFETIME */

    public boolean isActive() { return active; }

    public void setActive(boolean active)
    {
        this.active = active;
        if (active) onActivate();
        else onDeactivate();
    }

    public void toggleActive() { setActive(!active); }

    public void removeSelf()
    {
        if (gameObject != null && gameObject.getScene() != null)
        {
            gameObject.removeComponent(this);
        }
    }

    /* GAME OBJECT */

    /// **`INTERNAL`**: initializes the GameObject reference for this component, called when added to a GameObject.
    void setGameObject(GameObject gameObject) { this.gameObject = gameObject; }

    public GameObject getGameObject() { return gameObject; }

    /* LIFETIME EVENTS */

    protected void onInit() { }

    protected void onUpdate(TimeSpan deltaTime) { }

    protected void onRender(GraphicsContext context) { }

    protected void onActivate() { }

    protected void onDeactivate() { }
}