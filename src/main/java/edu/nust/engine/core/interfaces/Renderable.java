package edu.nust.engine.core.interfaces;

import javafx.scene.canvas.GraphicsContext;

public interface Renderable
{
    void onRender(GraphicsContext context);

    boolean isVisible();

    void setVisible(boolean active);

    default void toggleVisible() { setVisible(!isVisible()); }

    default void show() { setVisible(true); }

    default void hide() { setVisible(false); }

    default void onShow() { }

    default void onHide() { }
}
