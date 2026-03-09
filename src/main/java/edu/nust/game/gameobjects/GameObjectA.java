package edu.nust.game.gameobjects;

import edu.nust.engine.components.BoxRenderer;
import edu.nust.engine.components.MoveComponent;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.math.Vector2UI;

public class GameObjectA extends GameObject
{
    public GameObjectA()
    {
        this.getTransform().setAnchor(new Vector2UI(0, 0));
        this.addComponent(new BoxRenderer(50, 50));
        this.addComponent(new MoveComponent(1, 1));
    }
}
