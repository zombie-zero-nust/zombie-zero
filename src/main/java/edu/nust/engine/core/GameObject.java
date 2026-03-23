package edu.nust.engine.core;

import edu.nust.engine.core.components.Transform;
import edu.nust.engine.core.gameobjects.Tag;
import edu.nust.engine.core.interfaces.Initiable;
import edu.nust.engine.core.interfaces.Renderable;
import edu.nust.engine.core.interfaces.Updatable;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.enums.LogFormats;
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

/**
 * Represents an object in the game world that can have components and tags, and can be updated and rendered.
 * <br><br>
 * A {@link GameObject} can have multiple {@link Component}s (e.g., {@link Transform}, Renderer, Collider) that define
 * its behavior and appearance, and multiple {@link Tag}s that can be used for identification and grouping. The
 * GameObject itself can also be updated and rendered.
 * <br><br>
 * To use, create a subclass of GameObject or use the provided factory method to create an instance. Then, add
 * components and tags as needed, and implement the onUpdate() and onRender() methods for custom behavior and
 * rendering.
 */
public abstract class GameObject implements Initiable, Updatable<GameObject>, Renderable<GameObject>
{
    protected final GameLogger logger = GameLogger.getLogger(this.getClass());

    private GameScene scene;
    // can only add one component of each type, e.g. only one Transform, only one BoxRenderer, etc.
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();
    // tags are identifiers themselves, no instance is stored
    private final Set<Class<? extends Tag>> tags = new HashSet<>();

    /// Controls whether to update components and self
    protected boolean active = true;
    /// Controls whether to render components and self
    protected boolean visible = true;

    /**
     * Initializes a {@link  GameObject} with a default {@link  Transform} component.
     */
    public GameObject() { this.addComponent(new Transform()); }

    /* FACTORY */

    /**
     * <b>Syntax:</b> {@code GameObject.create(obj -> { obj.addComponent(...); ... })}
     * <br><br>
     * A factory method to create a {@link GameObject} with a custom {@link Consumer}
     *
     * @param initializer A Consumer that takes the newly created GameObject as a parameter, allowing you to add
     *                    components, tags, etc. to it in a lambda expression.
     *
     * @return The same {@link GameObject} that was created, for chaining
     */
    public static GameObject create(Consumer<GameObject> initializer)
    {
        return new GameObject()
        {
            @Override
            public void onInit() { initializer.accept(this); }
        };
    }

    /**
     * <b>Syntax:</b> {@code GameObject.create()}
     * <br><br>
     * A factory method to create a simple {@link GameObject}.
     *
     * @return The same {@link GameObject} that was created, for chaining
     */
    public static GameObject create() { return create((ignored) -> { }); }

    /* COMPONENT */

    /**
     * <b>Syntax:</b> {@code gameObject.addComponent(new BoxRenderer())}
     * <br><br>
     * Adds the specified {@link Component} to this {@link GameObject} if a {@link Component} of the same type doesn't
     * already exist, and returns the added {@link Component}. If a {@link Component} of the same type already exists,
     * {@code null} is returned and the new {@link Component<T>} is discarded.
     * <br><br>
     * <b>DO NOT USE</b>, for checking if a component exists; use {@link GameObject#hasComponent(Class)} instead.
     *
     * @param component The component to add to this GameObject.
     * @param <T>       The {@link Component} class type being added, which must extend {@link Component}.
     *
     * @return The {@link Component} if added, or {@code null} if already existed.
     */
    public <T extends Component> @Nullable T addComponent(T component)
    {
        Class<? extends Component> type = component.getClass();
        logger.trace("addComponent({}) called", type.getSimpleName());

        if (components.containsKey(type))
        {
            logger.trace(
                    "{} already exists, returning {}",
                    type.getSimpleName(),
                    LogFormats.BRIGHT_MAGENTA.apply("null")
            );
            return null;
        }

        logger.trace("Attaching {} to GameObject", type.getSimpleName());
        component.setGameObject(this);
        components.put(type, component);
        component.onInit();
        logger.debug("{} attached to GameObject", type.getSimpleName());

        return component;
    }

    /**
     * <b>Syntax:</b> {@code Transform transform = gameObject.getComponent(Transform.class)}
     * <br><br>
     * Retrieves the {@link Component} of the specified type from this {@link GameObject}. If a {@link Component} of
     * that type exists, it is returned; otherwise, {@code null} is returned.
     *
     * @param type The class type of the component to retrieve. Must not be null.
     * @param <T>  The {@link Component} class type being retrieved, which must extend {@link Component}.
     *
     * @return The {@link Component} of the specified type if it exists in this GameObject, or {@code null} if no such
     * component exists.
     */
    public <T extends Component> @Nullable T getComponent(Class<T> type)
    {
        Component component = components.get(type);
        if (component == null) return null;
        return type.cast(component);
    }

    /**
     * <b>Syntax:</b> {@code Transform transform = gameObject.getOrAddComponent(Transform::new)}
     * <br><br>
     * Retrieves the {@link Component} of the specified type from this {@link GameObject}. If a {@link Component} of
     * that type exists, it is returned; otherwise, a new {@link Component} is created using the provided constructor,
     * added to this {@link GameObject}, and returned
     *
     * @param constructor {@link Supplier} for {@link Component}
     * @param <T>         The {@link Component} class type being retrieved or added, which must extend
     *                    {@link Component}.
     *
     * @return The {@link Component} of the specified type if it exists in this GameObject, or a new component of that
     * type if no such component exists.
     */
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

    /**
     * <b>Syntax:</b> {@code gameObject.hasComponent(Transform.class)}
     * <br><br>
     * Checks if a {@link Component} of the specified type exists in this {@link GameObject}.
     *
     * @param type The class type of the component to check for.
     *
     * @return {@code true} if a {@link Component} of the specified type exists in this GameObject, or {@code false} if
     * no such
     */
    public boolean hasComponent(Class<? extends Component> type) { return components.containsKey(type); }

    /**
     * <b>Syntax:</b> {@code gameObject.removeComponent(Transform.class)}
     * <br><br>
     * Removes the {@link Component} of the specified type from this {@link GameObject} if it exists. If a
     * {@link Component} of that type exists, it is removed; otherwise, nothing happens.
     *
     * @param type The class type of the component to remove.
     */
    public void removeComponent(Class<? extends Component> type)
    {
        logger.trace("removeComponent({}) called", type.getSimpleName());
        components.remove(type);
        logger.debug("{} detached from GameObject", type.getSimpleName());
    }

    /**
     * <b>Syntax:</b> {@code gameObject.removeComponent(transform)}
     * <br><br>
     * Removes the specified {@link Component} from this {@link GameObject} if it exists. If the specified
     * {@link Component} exists in this {@link GameObject}, it is removed; otherwise, nothing happens.
     *
     * @param component The component instance to remove.
     */
    public void removeComponent(Component component)
    {
        logger.trace("removeComponent(instance) called for {}", component.getClass().getSimpleName());
        components.remove(component.getClass(), component);
        logger.debug("{} detached from GameObject", component.getClass().getSimpleName());
    }

    /**
     * Removes <b>all</b> {@link Component}s from this {@link GameObject}.
     */
    public void removeAllComponents()
    {
        logger.trace("removeAllComponents() called");
        components.clear();
        logger.debug("All components detached from GameObject");
    }

    /**
     * Performs the given action for each {@link Component} in this {@link GameObject}.
     *
     * @param action The {@link Consumer} action to be performed for each component.
     */
    public void forEachComponent(Consumer<Component> action) { components.values().forEach(action); }


    /**
     * A convenience method to directly access the default {@link Transform} component of this {@link GameObject}.
     *
     * @return The {@link Transform} component of this GameObject. Always <b>{@code non-null}</b>.
     */
    public @NotNull Transform getTransform()
    {
        Transform transform = getComponent(Transform.class);
        assert transform != null : "Transform component not present in GameObject";
        return transform;
    }

    /* TAG */

    /**
     * <b>Syntax:</b> {@code gameObject.addTag(Enemy.class)}
     * <br><br>
     * Adds the specified tag to this {@link GameObject}. If the tag already exists, nothing happens.
     *
     * @param tagClass The {@link Tag} class to add, which must extend {@link Tag}.
     *
     * @return This GameObject, for chaining.
     */
    public <T extends Tag> GameObject addTag(Class<T> tagClass)
    {
        logger.trace("addTag({}) called", tagClass.getSimpleName());
        tags.add(tagClass);
        logger.debug("Tag {} added to GameObject", tagClass.getSimpleName());
        return this;
    }

    /**
     * <b>Syntax:</b> {@code gameObject.removeTag(Enemy.class)}
     * <br><br>
     * Removes the specified tag from this {@link GameObject}. If the tag doesn't exist, nothing happens.
     *
     * @param tagClass The {@link Tag} class to remove, which must extend {@link Tag}.
     *
     * @return This GameObject, for chaining.
     */
    public <T extends Tag> GameObject removeTag(Class<T> tagClass)
    {
        logger.trace("removeTag({}) called", tagClass.getSimpleName());
        tags.remove(tagClass);
        logger.debug("Tag {} removed from GameObject", tagClass.getSimpleName());
        return this;
    }

    /**
     * <b>Syntax:</b> {@code gameObject.hasTag(Enemy.class)}
     * <br><br>
     * Checks if this {@link GameObject} has the specified tag. If this GameObject has the specified tag, or a tag that
     * is a subclass of the specified tag, {@code true} is returned; otherwise, {@code false} is returned.
     *
     * @param tagClass The {@link Tag} class to check for, which must extend {@link Tag}.
     *
     * @return {@code true} if this {@link GameObject} has the specified tag or a subclass of it, or {@code false} if it
     * doesn't.
     */
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
    public GameObject setActive(boolean active)
    {
        logger.trace("setActive({}) called on {}", active, this.getClass().getSimpleName());
        this.active = active;
        if (active)
        {
            logger.trace("Activating GameObject {}", this.getClass().getSimpleName());
            onActivate();
            logger.debug("GameObject activated");
        }
        else
        {
            logger.trace("Deactivating GameObject {}", this.getClass().getSimpleName());
            onDeactivate();
            logger.debug("GameObject deactivated");
        }
        return this;
    }

    @Override
    public boolean isVisible() { return visible; }

    @Override
    public GameObject setVisible(boolean visible)
    {
        logger.trace("setVisible({}) called on {}", visible, this.getClass().getSimpleName());
        this.visible = visible;
        if (visible)
        {
            logger.trace("Showing GameObject {}", this.getClass().getSimpleName());
            onShow();
            logger.debug("GameObject shown");
        }
        else
        {
            logger.trace("Hiding GameObject {}", this.getClass().getSimpleName());
            onHide();
            logger.debug("GameObject hidden");
        }
        return this;
    }

    public void destroy()
    {
        if (scene != null)
        {
            scene.removeGameObject(this);
        }
    }

    /* SCENE */

    /// Gets the {@link GameScene} linked to this {@link GameObject}.
    public GameScene getScene() { return scene; }

    /// **`INTERNAL`**: initializes the scene reference for this GameObject, called when added to a scene.
    void setScene(GameScene scene) { this.scene = scene; }

    void invokeUpdate(TimeSpan deltaTime)
    {
        if (!this.isActive()) return;

        this.onUpdate(deltaTime);
        for (Component component : components.values())
            if (component.isActive()) component.onUpdate(deltaTime);
    }

    void invokeLateUpdate(TimeSpan deltaTime)
    {
        if (!this.isActive()) return;

        this.lateUpdate(deltaTime);
        for (Component component : components.values())
            if (component.isActive()) component.lateUpdate(deltaTime);
    }

    void invokeRender(GraphicsContext context)
    {
        if (!this.isVisible()) return;

        this.onRender(context);
        for (Component component : components.values())
            if (component.isVisible()) component.onRender(context);
    }

    /* LIFETIME EVENTS */

    @Override
    public void onInit() { }

    @Override
    public void onUpdate(TimeSpan deltaTime) { }

    @Override
    public void lateUpdate(TimeSpan deltaTime) { }

    @Override
    public void onRender(GraphicsContext context) { }
}