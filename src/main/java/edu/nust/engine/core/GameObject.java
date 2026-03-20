package edu.nust.engine.core;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.core.gameobjects.Tag;
import edu.nust.engine.math.TimeSpan;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GameObject
{
    private GameScene scene;
    private final List<Component> components = new ArrayList<>();

    // tags are identifiers themselves, no instance is stored
    private final Set<Class<? extends Tag>> tags = new HashSet<>();

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

    /* TAG */

    public <T extends Tag> void addTag(Class<T> tagClass) { tags.add(tagClass); }

    public <T extends Tag> void removeTag(Class<T> tagClass) { tags.remove(tagClass); }

    /// Allows inherited tags, e.g. if
    /// <br>
    /// `class Enemy extends Character` and
    /// <br>
    /// `class Character extends Tag`
    /// <br>
    /// <br>
    /// then `hasTag(Character.class)` will return true for an Enemy-tagged GameObject
    public <T extends Tag> boolean hasTag(Class<T> tagClass)
    {
        for (Class<? extends Tag> tag : tags)
        {
            if (tagClass.isAssignableFrom(tag))
            {
                return true;
            }
        }
        return false;
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