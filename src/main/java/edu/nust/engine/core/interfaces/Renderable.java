package edu.nust.engine.core.interfaces;

import javafx.scene.canvas.GraphicsContext;

public interface Renderable<T>
{
    void onRender(GraphicsContext context);

    boolean isVisible();

    T setVisible(boolean active);

    default T toggleVisible() { return setVisible(!isVisible()); }

    default T show() { return setVisible(true); }

    default T hide() { return setVisible(false); }

    default void onShow() { }

    default void onHide() { }
}
