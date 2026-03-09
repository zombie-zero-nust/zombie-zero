package edu.nust.engine.core;

import edu.nust.engine.components.Transform;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject
{
    private GameScene scene;
    private final List<Component> components = new ArrayList<>();

    public GameObject()
    {
        this.addComponent(new Transform());
    }

    protected void setScene(GameScene scene)
    {
        this.scene = scene;
    }

    public GameScene getScene()
    {
        return scene;
    }

    /* COMPONENT */

    public <T extends Component> T addComponent(T component)
    {
        component.setGameObject(this);
        components.add(component);
        component.onInit();
        return component;
    }

    public <T extends Component> @Nullable T getComponent(Class<T> type)
    {
        for (Component component : components)
        {
            if (type.isInstance(component))
            {
                return type.cast(component);
            }
        }
        return null;
    }

    public @NotNull Transform getTransform()
    {
        Transform transform = getComponent(Transform.class);
        assert transform != null : "Transform component is required for every GameObject";
        return transform;
    }

    /* LIFETIME */

    void onInit()
    {
        for (Component component : components)
        {
            component.onInit();
        }
    }

    void onUpdate()
    {
        for (Component component : components)
        {
            component.onUpdate();
        }
    }

    void onRender(GraphicsContext context)
    {
        for (Component component : components)
        {
            component.onRender(context);
        }
    }
}