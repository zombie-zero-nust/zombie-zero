package edu.nust.engine.core.interfaces;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public interface InputHandler
{
    default void onKeyPressed(KeyEvent kEv) { }

    default void onKeyReleased(KeyEvent kEv) { }

    default void onMousePressed(MouseEvent mEv) { }

    default void onMouseReleased(MouseEvent mEv) { }

    default void onMouseMoved(MouseEvent mEv) { }
}
