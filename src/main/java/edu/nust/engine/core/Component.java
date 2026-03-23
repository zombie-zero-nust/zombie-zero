package edu.nust.engine.core;

import edu.nust.engine.core.interfaces.Renderable;
import edu.nust.engine.core.interfaces.Updatable;
import edu.nust.engine.math.TimeSpan;
import javafx.scene.canvas.GraphicsContext;

public abstract class Component implements Updatable<Component>, Renderable<Component>
{
    protected GameObject gameObject;
    /// Whether this should update self or not
    protected boolean active = true;
    /// Whether this should render self or not
    protected boolean visible = true;

    /* LIFETIME */

    @Override
    public boolean isActive() { return active; }

    @Override
    public Component setActive(boolean active)
    {
        this.active = active;
        if (active) onActivate();
        else onDeactivate();
        return this;
    }

    @Override
    public boolean isVisible() { return visible; }

    @Override
    public Component setVisible(boolean visible)
    {
        this.visible = visible;
        if (visible) onShow();
        else onHide();
        return this;
    }

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

    public void onInit() { }

    @Override
    public void onUpdate(TimeSpan deltaTime) { }

    @Override
    public void onRender(GraphicsContext context) { }
}