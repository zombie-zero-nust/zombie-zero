package edu.nust.engine.core.interfaces;

import javafx.scene.canvas.GraphicsContext;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;

/**
 * An interface for objects that can be rendered.
 * <br><br>
 * Used in {@link GameWorld}, {@link GameScene}, {@link GameObject}, and {@link Component}.
 */
public interface Renderable<T>
{
    /**
     * Called every frame to render the object. The provided {@link GraphicsContext} can be used to draw shapes, images,
     * text, etc.
     *
     * @param context The {@link GraphicsContext} to render with.
     */
    void onRender(GraphicsContext context);

    /// Whether this should render self or not
    boolean isVisible();

    /// **`CHAINABLE`** Sets whether this should render self or not
    T setVisible(boolean active);

    /// **`CHAINABLE`** Toggles whether this should render self or not
    default T toggleVisible() { return setVisible(!isVisible()); }

    /// **`CHAINABLE`** Shows this (sets visible to true)
    default T show() { return setVisible(true); }

    /// **`CHAINABLE`** Hides this (sets visible to false)
    default T hide() { return setVisible(false); }

    /// Called when this is shown (visible set to true)
    default void onShow() { }

    /// Called when this is hidden (visible set to false)
    default void onHide() { }
}
