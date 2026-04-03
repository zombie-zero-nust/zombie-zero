package edu.nust.engine.core;

import edu.nust.engine.core.gameobjects.Tag;
import edu.nust.engine.core.interfaces.Initiable;
import edu.nust.engine.core.interfaces.InputHandler;
import edu.nust.engine.core.interfaces.Updatable;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a single scene in the game, containing {@link GameObject}s and UI elements layered on top.
 * <br><br>
 * Each GameScene has two main layers: a world layer for rendering {@link GameObject}s, and a UI layer (JavaFX nodes)
 * for rendering user interface elements. The GameScene manages the lifecycle of {@link GameObject}s, including
 * initialization, updates, and rendering.
 * <br><br>
 * To create a new scene, subclass {@link GameScene} and implement the required methods to set up the scene.
 * <br><br>
 * For UI (FXML) create two files at {@code resources/edu/nust/game/scenes/YourSceneName/}:
 * <ol>
 *     <li>{@code layout.fxml} for the UI Layout <b>{@code (MANDATORY)}</b></li>
 *     <li>{@code style.css} for the scene's CSS styles <b>{@code (OPTIONAL)}</b></li>
 * </ol>
 * The FXML controller ({@code fx::controller}) will be the {@link GameScene} subclass itself, so you can define {@code @FXML} fields and
 * methods in your {@link GameScene} subclass to interact with the UI elements defined in the FXML file.
 * <br><br>
 * Additionally use the lifecycle methods {@link GameScene#onInit()}, {@link GameScene#onUpdate(TimeSpan)}, and
 * {@link GameScene#lateUpdate(TimeSpan)} to set up and manage the scene's behavior. Use
 * {@link GameScene#fetchWorldContextAndRun(Consumer)} to render on the world canvas with
 * the camera transformations applied.
 */
public abstract class GameScene implements Initiable, Updatable<GameScene>, InputHandler
{
    protected final GameLogger logger = GameLogger.getLogger(this.getClass());

    private final GameWorld window;
    /// Whether to update this scene or not
    private boolean active = true;

    /// Contains all elements etc. loaded from FXML file for thus scene
    private final Region uiLayer;
    /// Contains a canvas that renders all GameObjects
    private final Region worldLayer;

    private final GameCamera worldCamera;
    private final Canvas worldCanvas;
    protected final List<GameObject> gameObjects = new ArrayList<>();

    // debug options
    private boolean debugGrid = false;

    public GameScene(GameWorld window)
    {
        logger.trace("Constructing GameScene: {}", this.getClass().getSimpleName());
        LogProgress initSceneLogger = LogProgress.create("SCENE", logger);
        initSceneLogger.begin("Initializing scene: {}", this.getClass().getSimpleName());

        this.window = window;
        logger.trace("Canvas initialization starting");
        this.worldCanvas = initCanvas();

        // initialize layers
        logger.trace("UI Layer initialization starting");
        this.uiLayer = initUILayer();
        logger.trace("World Layer initialization starting");
        this.worldLayer = initWorldLayer();

        // add CSS
        String sceneName = this.getClass().getSimpleName();
        URL cssUrl = Resources.tryGetResource("scenes", sceneName, "style.css");
        if (cssUrl == null)
        {
            logger.warn("Missing CSS for: {}", sceneName);
        }
        else
        {
            logger.trace("Adding CSS stylesheet");
            // add CSS to raw scene to allow overriding
            this.window.getRawScene().getStylesheets().add(cssUrl.toExternalForm());
        }

        // bind canvas size to world layer, which is bound to `window.root`
        logger.trace("Binding canvas dimensions to world layer");
        this.worldCanvas.widthProperty().bind(this.worldLayer.widthProperty());
        this.worldCanvas.heightProperty().bind(this.worldLayer.heightProperty());

        // start the scene
        logger.trace("Calling onStart() for scene setup");
        onInit();
        logger.trace("Initializing all GameObjects");
        this.gameObjects.forEach(GameObject::onInit);

        // add events
        logger.trace("Registering input event handlers");
        this.window.getRawScene().setOnKeyPressed(this::onKeyPressed);
        this.window.getRawScene().setOnKeyReleased(this::onKeyReleased);
        this.window.getRawScene().setOnMousePressed(this::onMousePressed);
        this.window.getRawScene().setOnMouseReleased(this::onMouseReleased);
        this.window.getRawScene().setOnMouseMoved(this::onMouseMoved);

        // initialize camera
        logger.trace("Initializing world camera");
        this.worldCamera = new GameCamera();

        initSceneLogger.end("Scene initialized successfully");
    }

    // package-private so classes outside package cannot call it, neither can subclasses override it
    void invokeGameLoopFrame(TimeSpan deltaTime)
    {
        if (active)
        {
            this.onUpdate(deltaTime);
            // Create a copy of the list to iterate over to avoid ConcurrentModificationException
            // This allows addGameObject() and removeGameObject() to be called during update cycles
            new ArrayList<>(this.gameObjects).forEach(obj -> obj.invokeUpdate(deltaTime));
            // late update after all updates
            new ArrayList<>(this.gameObjects).forEach(obj -> obj.invokeLateUpdate(deltaTime));
            this.lateUpdate(deltaTime);
        }

        this.clearCanvas();

        fetchWorldContextAndRun((ctx) -> {
            // Create a copy of the list to iterate over to avoid ConcurrentModificationException
            new ArrayList<>(this.gameObjects).forEach(obj -> obj.invokeRender(ctx));

            this.renderDebug(ctx);
        });
    }

    /* GAME OBJECT */

    /**
     * <b>Syntax:</b> {@code addGameObject(new Player())}
     * <br><br>
     * Adds the given {@link GameObject} to this scene.
     *
     * @param gameObject The GameObject to add to this scene
     *
     * @return The same {@link GameObject} that was created, for chaining
     */
    public GameObject addGameObject(GameObject gameObject)
    {
        logger.trace("addGameObject({}) called", gameObject.getClass().getSimpleName());
        gameObject.setScene(this);
        gameObjects.add(gameObject);
        logger.debug("GameObject {} added to scene", gameObject.getClass().getSimpleName());
        return gameObject;
    }

    /**
     * <b>Syntax:</b> {@code addGameObject(Player::new)}
     * <br><br>
     * Adds the given {@link GameObject} to this scene.
     *
     * @param gameObject Supplier for {@link GameObject}
     *
     * @return The same {@link GameObject} that was created, for chaining
     */
    public GameObject addGameObject(Supplier<GameObject> gameObject) { return addGameObject(gameObject.get()); }


    /**
     * <b>Syntax:</b> {@code spawnGameObject(new Player(), new Vector2D(100, 200))}
     * <br><br>
     * Spawns the given {@link GameObject} at specified position
     *
     * @param gameObject The GameObject to spawn
     * @param position   The position to spawn the GameObject at
     *
     * @return The same {@link GameObject} that was created, for chaining
     */
    public GameObject spawnGameObject(GameObject gameObject, Vector2D position)
    {
        logger.trace("spawnGameObject({}) at position {}", gameObject.getClass().getSimpleName(), position);
        return addGameObject(gameObject).getTransform().setPosition(position).getGameObject();
    }

    /**
     * <b>Syntax:</b> {@code spawnGameObject(new Player(), 100, 200)}
     * <br><br>
     * Spawns the given {@link GameObject} at specified position
     *
     * @param gameObject The GameObject to spawn
     * @param x          The x-coordinate to spawn the GameObject at
     * @param y          The y-coordinate to spawn the GameObject at
     *
     * @return The same {@link GameObject} that was spawned, for chaining
     */
    public GameObject spawnGameObject(GameObject gameObject, double x, double y)
    {
        return spawnGameObject(gameObject, new Vector2D(x, y));
    }

    /**
     * <b>Syntax:</b> {@code spawnGameObject(Player::new, new Vector2D(100, 200))}
     * <br><br>
     * Spawns the given {@link GameObject} at specified position
     *
     * @param object Supplier for the GameObject to spawn
     * @param pos    The position to spawn the GameObject at
     *
     * @return The same {@link GameObject} that was spawned, for chaining
     */
    public GameObject spawnGameObject(Supplier<GameObject> object, Vector2D pos)
    {
        return spawnGameObject(object.get(), pos);
    }

    /**
     * <b>Syntax:</b> {@code spawnGameObject(Player::new, 100, 200)}
     * <br><br>
     * Spawns the given {@link GameObject} at specified position
     *
     * @param object Supplier for the GameObject to spawn
     * @param x      The x-coordinate to spawn the GameObject at
     * @param y      The y-coordinate to spawn the GameObject at
     *
     * @return The same {@link GameObject} that was spawned, for chaining
     */
    public GameObject spawnGameObject(Supplier<GameObject> object, double x, double y)
    {
        return spawnGameObject(object.get(), new Vector2D(x, y));
    }

    /**
     * Gets a list of all {@link GameObject}s currently in this scene.
     *
     * @return List of all {@link GameObject}s in this scene
     */
    public List<GameObject> getAllGameObjects() { return gameObjects; }

    /**
     * <b>Syntax:</b> {@code getFirstOfType(Player.class)}
     * <br><br>
     * Gets the first {@link GameObject} of the specified type in this scene, or null if not found.
     *
     * @param type The class type of the GameObject to find
     *
     * @return The first GameObject of the specified type, or null if not found
     */
    public @Nullable GameObject getFirstOfType(Class<? extends GameObject> type)
    {
        for (GameObject obj : gameObjects)
        {
            if (type.isInstance(obj)) return obj;
        }
        return null; // not found
    }

    /**
     * <b>Syntax:</b> {@code getGameObjectsOfType(Player.class)}
     * <br><br>
     * Gets a list of all {@link GameObject}s of the specified type in this scene.
     *
     * @param type The class type of the GameObjects to find
     *
     * @return List of all GameObjects of the specified type in this scene
     */
    public List<GameObject> getGameObjectsOfType(Class<? extends GameObject> type)
    {
        List<GameObject> result = new ArrayList<>();
        for (GameObject obj : gameObjects)
        {
            if (type.isInstance(obj)) result.add(obj);
        }
        return result;
    }

    /**
     * <b>Syntax:</b> {@code getFirstWithTag(EnemyTag.class)}
     * <br><br>
     * Gets the first {@link GameObject} with the specified {@link Tag} in this scene, or null if not found.
     *
     * @param tag The class type of the tag to find
     *
     * @return The first GameObject with the specified tag, or null if not found
     */
    public @Nullable GameObject getFirstWithTag(Class<? extends Tag> tag)
    {
        for (GameObject obj : gameObjects)
        {
            if (obj.hasTag(tag)) return obj;
        }
        return null; // not found
    }

    /**
     * <b>Syntax:</b> {@code getAllWithTag(EnemyTag.class)}
     * <br><br>
     * Gets a list of all {@link GameObject}s with the specified {@link Tag} in this scene.
     *
     * @param tag The class type of the tag to find
     *
     * @return List of all GameObjects with the specified tag in this scene
     */
    public List<GameObject> getAllWithTag(Class<? extends Tag> tag)
    {
        List<GameObject> result = new ArrayList<>();
        for (GameObject obj : gameObjects)
        {
            if (obj.hasTag(tag)) result.add(obj);
        }
        return result;
    }

    /**
     * <b>Syntax:</b> {@code removeGameObject(player)}
     * <br><br>
     * Removes the specified {@link GameObject} from this scene.
     *
     * @param gameObject The GameObject to remove
     */
    public void removeGameObject(GameObject gameObject)
    {
        logger.trace("removeGameObject({}) called", gameObject.getClass().getSimpleName());
        gameObjects.remove(gameObject);
        logger.debug("GameObject {} removed from scene", gameObject.getClass().getSimpleName());
    }

    /**
     * <b>Syntax:</b> {@code removeGameObjectsOfType(Player.class)}
     * <br><br>
     * Removes all {@link GameObject}s of the specified type from this scene.
     *
     * @param type The class type of the GameObjects to remove
     */
    public void removeGameObjectsOfType(Class<? extends GameObject> type)
    {
        logger.trace("removeGameObjectsOfType({}) called", type.getSimpleName());
        gameObjects.removeIf(type::isInstance);
        logger.debug("All GameObjects of type {} removed from scene", type.getSimpleName());
    }

    /**
     * <b>Syntax:</b> {@code removeGameObjectsWithTag(EnemyTag.class)}
     * <br><br>
     * Removes all {@link GameObject}s with the specified {@link Tag} from this scene.
     *
     * @param tag The class type of the tag to remove
     */
    public void removeGameObjectsWithTag(Class<? extends Tag> tag)
    {
        logger.trace("removeGameObjectsWithTag({}) called", tag.getSimpleName());
        gameObjects.removeIf(obj -> obj.hasTag(tag));
        logger.debug("All GameObjects with tag {} removed from scene", tag.getSimpleName());
    }

    /**
     * <b>Syntax:</b> {@code removeAllGameObjects()}
     * <br><br>
     * Removes all {@link GameObject}s from this scene.
     */
    public void removeAllGameObjects()
    {
        logger.trace("removeAllGameObjects() called, removing {} objects", gameObjects.size());
        gameObjects.clear();
        logger.debug("All GameObjects removed from scene");
    }

    /* UI LAYER */

    private Region initUILayer()
    {
        Region root = new StackPane(); // just for intellisense

        String sceneName = this.getClass().getSimpleName();

        // FXML
        URL fxmlUrl = Resources.tryGetResource("scenes", sceneName, "layout.fxml");
        if (fxmlUrl == null) logger.error(true, "Missing FXML for: " + sceneName);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setController(this);

        try
        {
            root = loader.load();
        }
        catch (IOException e)
        {
            logger.error(true, "Failed to load FXML: " + sceneName, e);
        }

        logger.debug("UI layer initialized successfully");
        return root;
    }

    /* CANVAS LAYER */

    private Region initWorldLayer()
    {
        StackPane worldLayer = new StackPane(worldCanvas);
        worldLayer.setPickOnBounds(false); // allow clicks to pass through to UI layer
        logger.debug("World layer initialized successfully");
        return worldLayer;
    }

    private Canvas initCanvas()
    {
        Canvas canvas = new Canvas();
        canvas.setFocusTraversable(true);
        return canvas;
    }

    /**
     * Clears the world canvas. When called everything in the frame will be cleared.
     */
    public void clearCanvas()
    {
        getRawContext().clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight());
    }

    private GraphicsContext getRawContext() { return worldCanvas.getGraphicsContext2D(); }

    /**
     * Fetches a {@link GraphicsContext} with the camera transformations applied, then runs the given function with it.
     *
     * @param contextConsumer The function to run with the transformed GraphicsContext
     */
    public void fetchWorldContextAndRun(Consumer<GraphicsContext> contextConsumer)
    {
        GraphicsContext ctx = this.getRawContext();
        ctx.save();

        double zoom = worldCamera.getZoom();
        double canvasW = worldCanvas.getWidth();
        double canvasH = worldCanvas.getHeight();

        // move origin to center of screen
        ctx.translate(canvasW / 2, canvasH / 2);

        // apply zoom to context
        ctx.scale(zoom, zoom);

        // translate context to center at camera position
        ctx.translate(-worldCamera.getPosition().getX(), -worldCamera.getPosition().getY());

        contextConsumer.accept(ctx);

        ctx.restore();
    }

    /* DEBUG */

    private void renderDebug(GraphicsContext ctx)
    {
        if (debugGrid)
        {
            ctx.setStroke(Color.GRAY);
            ctx.setLineWidth(1);

            double zoom = worldCamera.getZoom();

            double canvasW = worldCanvas.getWidth();
            double canvasH = worldCanvas.getHeight();

            // camera center
            double camX = worldCamera.getPosition().getX();
            double camY = worldCamera.getPosition().getY();

            // visible world bounds
            double halfW = canvasW / 2.0 / zoom;
            double halfH = canvasH / 2.0 / zoom;

            double left = camX - halfW;
            double right = camX + halfW;
            double top = camY - halfH;
            double bottom = camY + halfH;

            double gridSize = 100;

            // snap to grid
            double startX = Math.floor(left / gridSize) * gridSize;
            double startY = Math.floor(top / gridSize) * gridSize;

            // vertical lines
            for (double x = startX; x <= right; x += gridSize)
            {
                ctx.strokeLine(x, top, x, bottom);
            }

            // horizontal lines
            for (double y = startY; y <= bottom; y += gridSize)
            {
                ctx.strokeLine(left, y, right, y);
            }
        }
    }

    /* ACTIVE */

    @Override
    public boolean isActive() { return active; }

    @Override
    public GameScene setActive(boolean active)
    {
        logger.trace("setActive({}) called on {}", active, this.getClass().getSimpleName());
        this.active = active;
        if (active)
        {
            logger.trace("Activating scene {}", this.getClass().getSimpleName());
            onActivate();
            logger.debug("Scene activated");
        }
        else
        {
            logger.trace("Deactivating scene {}", this.getClass().getSimpleName());
            onDeactivate();
            logger.debug("Scene deactivated");
        }
        return this;
    }

    /* LAYERS AND CAMERA */

    Region getUILayer() { return uiLayer; }

    public Region getWorldLayer() { return worldLayer; }

    /**
     * Gets the {@link GameWorld} (window) that this scene belongs to.
     *
     * @return The GameWorld (window) that this scene belongs to
     */
    public GameWorld getWindow() { return window; }

    /**
     * Gets the {@link GameCamera} used for rendering the world layer of this scene.
     * <br><br>
     * Use to move the camera, zoom in/out, etc.
     *
     * @return The GameCamera used for rendering the world layer of this scene
     */
    public GameCamera getWorldCamera() { return worldCamera; }

    /* DEBUG */

    /// Gets whether the debug grid is currently shown or not.
    ///
    /// @see GameScene#setDebugGrid(boolean)
    public boolean hasDebugGrid() { return debugGrid; }

    /// **`CHAINABLE`** Sets whether to show the debug grid or not. The debug grid is a simple grid overlay rendered on
    /// the world canvas to help with positioning and debugging.
    public GameScene setDebugGrid(boolean debugGrid)
    {
        this.debugGrid = debugGrid;
        return this;
    }

    /// **`CHAINABLE`** Toggles the debug grid on/off.
    ///
    /// @see GameScene#setDebugGrid(boolean)
    public GameScene toggleDebugGrid() { return setDebugGrid(!debugGrid); }

    /// **`CHAINABLE`** Shows the debug grid.
    ///
    /// @see GameScene#setDebugGrid(boolean)
    public GameScene showDebugGrid() { return setDebugGrid(true); }

    /// **`CHAINABLE`** Hides the debug grid.
    ///
    /// @see GameScene#setDebugGrid(boolean)
    public GameScene hideDebugGrid() { return setDebugGrid(false); }

    /* LIFETIME EVENTS */

    @Override
    public void onInit() { }

    @Override
    public void onUpdate(TimeSpan deltaTime) { }

    @Override
    public void lateUpdate(TimeSpan deltaTime) { }
}
