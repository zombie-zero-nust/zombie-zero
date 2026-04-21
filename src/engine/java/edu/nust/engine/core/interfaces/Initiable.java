package edu.nust.engine.core.interfaces;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;

/**
 * An interface for objects that can be initialized.
 * <br><br>
 * Used in {@link GameWorld}, {@link GameScene}, {@link GameObject}, and {@link Component}.
 */
public interface Initiable
{
    /**
     * Called <b>ONCE</b> when initialized.
     * <br><br>
     * Called before the first update and render calls, used for setting up any necessary state or resources.
     */
    void onInit();
}
