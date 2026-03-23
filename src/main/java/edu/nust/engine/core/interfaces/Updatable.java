package edu.nust.engine.core.interfaces;

import edu.nust.engine.math.TimeSpan;

public interface Updatable<T>
{
    void onUpdate(TimeSpan deltaTime);

    boolean isActive();

    T setActive(boolean active);

    default T toggleActive() { return setActive(!isActive()); }

    default T activate() { return setActive(true); }

    default T deactivate() { return setActive(false); }

    default void onActivate() { }

    default void onDeactivate() { }
}
