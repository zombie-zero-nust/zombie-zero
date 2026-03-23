package edu.nust.engine.core.interfaces;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * An interface for objects that can handle input events.
 * <br><br>
 * Used in {@link GameWorld}, {@link GameScene}, {@link GameObject}, and {@link Component}.
 */
public interface InputHandler
{
    /**
     * Called when a key is pressed. The provided {@link KeyEvent} contains information about the key event, such as
     * which key was pressed, whether any modifier keys were held down, etc.
     *
     * @param kEv The {@link KeyEvent} representing the key press event.
     */
    default void onKeyPressed(KeyEvent kEv) { }

    /**
     * Called when a key is released. The provided {@link KeyEvent} contains information about the key event, such as
     * which key was released, whether any modifier keys were held down, etc.
     *
     * @param kEv The {@link KeyEvent} representing the key release event.
     */
    default void onKeyReleased(KeyEvent kEv) { }

    /**
     * Called when the mouse is pressed. The provided {@link MouseEvent} contains information about the mouse event,
     * such as which button was pressed, the position of the mouse, etc.
     *
     * @param mEv The {@link MouseEvent} representing the mouse press event.
     */
    default void onMousePressed(MouseEvent mEv) { }

    /**
     * Called when the mouse is released. The provided {@link MouseEvent} contains information about the mouse event,
     * such as which button was released, the position of the mouse, etc.
     *
     * @param mEv The {@link MouseEvent} representing the mouse release event.
     */
    default void onMouseReleased(MouseEvent mEv) { }

    /**
     * Called when the mouse is moved. The provided {@link MouseEvent} contains information about the mouse event, such
     * as the new position of the mouse, whether any buttons are currently pressed, etc.
     *
     * @param mEv The {@link MouseEvent} representing the mouse move event.
     */
    default void onMouseMoved(MouseEvent mEv) { }
}
