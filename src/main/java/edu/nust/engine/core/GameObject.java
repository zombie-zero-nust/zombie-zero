package edu.nust.engine.core;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.core.gameobjects.Tag;
import edu.nust.engine.core.interfaces.Renderable;
import edu.nust.engine.core.interfaces.Updatable;
import edu.nust.engine.math.TimeSpan;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GameObject implements Updatable, Renderable
{
    private GameScene scene;
    // can only add one component of each type, e.g. only one Transform, only one BoxRenderer, etc.
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    // tags are identifiers themselves, no instance is stored
    private final Set<Class<? extends Tag>> tags = new HashSet<>();

    /// Controls whether to update components and self
    protected boolean active = true;
    /// Controls whether to render components and self
    protected boolean visible = true;

    public GameObject() { this.addComponent(new Transform()); }

    /* COMPONENT */

    /// `addComponent(new BoxRenderer())`
    /// <br><br>
    /// Adds the specified component to this GameObject if a component of the same type doesn't already exist, and
    /// returns the added component. Otherwise, does nothing and returns null.
    @SuppressWarnings("UnusedReturnValue")
    public <T extends Component> @Nullable T addComponent(T component)
    {
        Class<? extends Component> type = component.getClass();

        if (components.containsKey(type)) return null;

        component.setGameObject(this);
        components.put(type, component);
        component.onInit();
        return component;
    }

    /// getComponent(Transform.class)
    /// <br><br>
    /// Returns the component of the specified type if it exists, otherwise returns null.
    public <T extends Component> @Nullable T getComponent(Class<T> type)
    {
        Component component = components.get(type);
        if (component == null) return null;
        return type.cast(component);
    }

    public <T extends Component> T getOrAddComponent(Supplier<T> constructor)
    {
        T temp = constructor.get();
        Class<? extends Component> type = temp.getClass();

        // check if exists
        @SuppressWarnings("unchecked") T existing = (T) components.get(type);
        if (existing != null) return existing;

        // doesn't exist; add
        addComponent(temp);
        return temp;
    }

    /// `hasComponent(Transform.class)`
    /// <br><br>
    /// Checks if a component of the specified type exists in this GameObject.
    public boolean hasComponent(Class<? extends Component> type)
    {
        return components.containsKey(type);
    }

    /// `removeComponent(Transform.class)`
    /// <br><br>
    /// Removes the component of the specified type if it exists.
    public void removeComponent(Class<? extends Component> type)
    {
        components.remove(type);
    }

    /// `removeComponent(transformComponent)`
    /// <br><br>
    /// Removes the specified component instance if it exists.
    public void removeComponent(Component component) { components.remove(component.getClass(), component); }

    public void removeAllComponents() { components.clear(); }

    public void forEachComponent(Consumer<Component> action) { components.values().forEach(action); }

    /// `getTransform()`
    /// <br><br>
    /// A convenience method to get the Transform component, which is guaranteed to exist.
    public @NotNull Transform getTransform()
    {
        Transform transform = getComponent(Transform.class);
        assert transform != null : "Transform component not present in GameObject";
        return transform;
    }

    /// **`INTERNAL`**: updates all components, called by the scene`
    void updateComponents(TimeSpan deltaTime)
    {
        for (Component component : components.values())
            if (component.isActive()) component.onUpdate(deltaTime);
    }

    /// **`INTERNAL`**: renders all components, called by the scene`
    void renderComponents(GraphicsContext context)
    {
        for (Component component : components.values())
            if (component.isVisible()) component.onRender(context);
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

    @Override
    public boolean isActive() { return active; }

    @Override
    public void setActive(boolean active)
    {
        this.active = active;
        if (active) onActivate();
        else onDeactivate();
    }

    @Override
    public boolean isVisible() { return visible; }

    @Override
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        if (visible) onShow();
        else onHide();
    }

    public void destroy()
    {
        if (scene != null)
        {
            scene.removeGameObject(this);
        }
    }

    /* SCENE */

    /// **`INTERNAL`**: initializes the scene reference for this GameObject, called when added to a scene.
    void setScene(GameScene scene) { this.scene = scene; }

    public GameScene getScene() { return scene; }

    /* LIFETIME API */

    public abstract void onInit();

    @Override
    public abstract void onUpdate(TimeSpan deltaTime);

    @Override
    public abstract void onRender(GraphicsContext context);
}