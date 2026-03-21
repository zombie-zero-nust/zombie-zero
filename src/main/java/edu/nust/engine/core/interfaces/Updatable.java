package edu.nust.engine.core.interfaces;

import edu.nust.engine.math.TimeSpan;

public interface Updatable
{
    void onUpdate(TimeSpan deltaTime);

    boolean isActive();

    void setActive(boolean active);

    default void toggleActive() { setActive(!isActive()); }

    default void activate() { setActive(true); }

    default void deactivate() { setActive(false); }

    default void onActivate() { }

    default void onDeactivate() { }
}
