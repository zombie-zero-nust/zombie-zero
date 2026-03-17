package edu.nust.engine.core;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.math.TimeSpan;
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

    @SuppressWarnings("UnusedReturnValue")
    public <T extends Component> T addComponent(T component)
    {
        component.setGameObject(this);
        components.add(component);
        component.onInit();
        return component;
    }

    // getComponent(Transform.class)
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
        assert transform != null : "Transform component not present in GameObject";
        return transform;
    }

    /* LIFETIME */

    protected void onInit()
    {
        // INFO: components are initialized when added
    }

    protected void onUpdate(TimeSpan deltaTime)
    {
        for (Component component : components)
        {
            component.onUpdate(deltaTime);
        }
    }

    protected void onRender(GraphicsContext context)
    {
        for (Component component : components)
        {
            component.onRender(context);
        }
    }
}