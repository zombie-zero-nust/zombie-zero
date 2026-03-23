package edu.nust.engine.core;

import edu.nust.engine.core.interfaces.Renderable;
import edu.nust.engine.core.interfaces.Updatable;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.math.TimeSpan;
import javafx.scene.canvas.GraphicsContext;

public abstract class Component implements Updatable<Component>, Renderable<Component>
{
    protected final GameLogger logger = GameLogger.getLogger(this.getClass());

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
        logger.trace("setActive({}) called on {}", active, this.getClass().getSimpleName());
        this.active = active;
        if (active)
        {
            logger.trace("Activating {}", this.getClass().getSimpleName());
            onActivate();
            logger.debug("{} activated", this.getClass().getSimpleName());
        }
        else
        {
            logger.trace("Deactivating {}", this.getClass().getSimpleName());
            onDeactivate();
            logger.debug("{} deactivated", this.getClass().getSimpleName());
        }
        return this;
    }

    @Override
    public boolean isVisible() { return visible; }

    @Override
    public Component setVisible(boolean visible)
    {
        logger.trace("setVisible({}) called on {}", visible, this.getClass().getSimpleName());
        this.visible = visible;
        if (visible)
        {
            logger.trace("Showing {}", this.getClass().getSimpleName());
            onShow();
            logger.debug("{} shown", this.getClass().getSimpleName());
        }
        else
        {
            logger.trace("Hiding {}", this.getClass().getSimpleName());
            onHide();
            logger.debug("{} hidden", this.getClass().getSimpleName());
        }
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