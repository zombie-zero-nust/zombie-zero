package edu.nust.engine.core.interfaces;

import edu.nust.engine.math.TimeSpan;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;

/**
 * An interface for objects that can be updated. Contains both {@link Updatable#onUpdate(TimeSpan)} and
 * {@link Updatable#lateUpdate(TimeSpan)} methods.
 * <br><br>
 * Used in {@link GameWorld}, {@link GameScene}, {@link GameObject}, and {@link Component}.
 */
public interface Updatable<T>
{
    /**
     * Called every frame to update the object. The provided {@link TimeSpan} represents the time elapsed since the last
     * update, which can be used for time-based movement, animations, etc.
     * <br><br>
     * Update order is as follows:
     * <ol>
     * <li>{@link GameScene#onUpdate(TimeSpan)}</li>
     * <li>{@link GameObject#onUpdate(TimeSpan)}</li>
     * <li>{@link Component#onUpdate(TimeSpan)}</li>
     * <li>{@link GameObject#lateUpdate(TimeSpan)}</li>
     * <li>{@link Component#lateUpdate(TimeSpan)}</li>
     * <li>{@link GameScene#lateUpdate(TimeSpan)}</li>
     * </ol>
     *
     * @param deltaTime The time elapsed since the last update.
     */
    void onUpdate(TimeSpan deltaTime);

    /***
     * Called every frame after all {@link Updatable#onUpdate(TimeSpan)} calls to perform any necessary late updates.
     * <br><br>
     * Update order is as follows:
     * <ol>
     * <li>{@link GameScene#onUpdate(TimeSpan)}</li>
     * <li>{@link GameObject#onUpdate(TimeSpan)}</li>
     * <li>{@link Component#onUpdate(TimeSpan)}</li>
     * <li>{@link GameObject#lateUpdate(TimeSpan)}</li>
     * <li>{@link Component#lateUpdate(TimeSpan)}</li>
     * <li>{@link GameScene#lateUpdate(TimeSpan)}</li>
     * </ol>
     *
     * @param deltaTime The time elapsed since the last update.
     */
    void lateUpdate(TimeSpan deltaTime);

    /// Whether this should update self or not
    boolean isActive();

    /// **`CHAINABLE`** Sets whether this should update self or not
    T setActive(boolean active);

    /// **`CHAINABLE`** Toggles whether this should update self or not
    default T toggleActive() { return setActive(!isActive()); }

    /// **`CHAINABLE`** Activates this (sets active to true)
    default T activate() { return setActive(true); }

    /// **`CHAINABLE`** Deactivates this (sets active to false)
    default T deactivate() { return setActive(false); }

    /// Called when this is activated (active set to true)
    default void onActivate() { }

    /// Called when this is deactivated (active set to false)
    default void onDeactivate() { }
}
