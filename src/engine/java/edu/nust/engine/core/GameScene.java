package edu.nust.engine.core;

import edu.nust.engine.core.debug.DebugEllipse;
import edu.nust.engine.core.debug.DebugPoint;
import edu.nust.engine.core.debug.DebugRectangle;
import edu.nust.engine.core.debug.DebugShape;
import edu.nust.engine.core.gameobjects.Tag;
import edu.nust.engine.core.interfaces.Initiable;
import edu.nust.engine.core.interfaces.InputHandler;
import edu.nust.engine.core.interfaces.Updatable;
import edu.nust.engine.core.interfaces.WorldBoundsProvider;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.systems.audio.Audios;
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
import java.util.Comparator;
import java.util.HashSet;
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

    private final GameWorld gameWorld;
    /// Whether to update this scene or not
    private boolean active = true;

    /// Contains all elements etc. loaded from FXML file for thus scene
    private final Region uiLayer;
    /// Contains a canvas that renders all GameObjects
    private final Region worldLayer;
    /// Only contains the console
    private final Region consoleLayer;

    private final GameCamera worldCamera;
    private final Canvas worldCanvas;

    protected final List<GameObject> gameObjects = new ArrayList<>();
    // we need to hold separate lists to add or remove during updates
    protected final List<GameObject> gameObjectsToAdd = new ArrayList<>();
    protected final List<GameObject> gameObjectsToRemove = new ArrayList<>();

    // only one of each type
    private final HashSet<DebugShape> debugShapes = new HashSet<>();

    // debug options
    private boolean debugGrid = false;
    private double debugGridSize = 128;
    private boolean debugMouseLocation = false;

    protected Vector2D mousePosition = Vector2D.zero();

    private final DevConsole devConsole = new DevConsole();
    private int objectsInViewCount = 0;

    public GameScene(GameWorld gameWorld)
    {
        logger.trace("Constructing GameScene: {}", this.getClass().getSimpleName());
        LogProgress initSceneLogger = LogProgress.create("SCENE", logger);
        initSceneLogger.begin("Initializing scene: {}", this.getClass().getSimpleName());

        this.gameWorld = gameWorld;
        logger.trace("Canvas initialization starting");
        this.worldCanvas = initCanvas();

        // initialize layers
        logger.trace("UI Layer initialization starting");
        this.uiLayer = initUILayer();
        logger.trace("World Layer initialization starting");
        this.worldLayer = initWorldLayer();
        this.consoleLayer = devConsole.createLayer();

        // add CSS
        String sceneName = this.getClass().getSimpleName();
        URL cssUrl = Resources.tryGetResource(GameURLs.SCENES_ROOT_DIR, sceneName, GameURLs.SCENE_CSS_FILENAME);
        // Remove previous stylesheets, except common
        this.gameWorld.getRawScene()
                .getStylesheets()
                .removeIf(stylesheet -> !stylesheet.contains(GameURLs.COMMON_CSS_FILENAME));
        if (cssUrl == null)
        {
            logger.warn("Missing CSS for: {}", sceneName);
        }
        else
        {
            logger.trace("Adding CSS stylesheet");
            // add CSS to raw scene to allow overriding
            this.gameWorld.getRawScene().getStylesheets().add(cssUrl.toExternalForm());
        }

        // initialize camera
        logger.trace("Initializing world camera");
        this.worldCamera = new GameCamera();

        // bind canvas size to world layer, which is bound to `window.root`
        logger.trace("Binding canvas dimensions to world layer");
        this.worldCanvas.widthProperty().bind(this.worldLayer.widthProperty());
        this.worldCanvas.heightProperty().bind(this.worldLayer.heightProperty());

        registerDefaultDevCommands();
        logger.trace("All Default DevCommands registered");

        // start the scene
        logger.trace("Calling onStart() for scene setup");
        onInit();

        registerDevCommands();
        logger.trace("All Custom DevCommands registered");
        logger.info("All DevCommands registered");

        logger.trace("Initializing all GameObjects");
        this.gameObjects.forEach(GameObject::onInit);

        // add events
        logger.trace("Registering input event handlers");
        this.gameWorld.getRawScene().setOnKeyPressed(event -> {
            devConsole.handleShouldOpen(event);

            if (!devConsole.isOpen())
            {
                this.onKeyPressed(event);
            }
        });
        this.gameWorld.getRawScene().setOnKeyReleased(this::onKeyReleased);
        this.gameWorld.getRawScene().setOnMousePressed(this::onMousePressed);
        this.gameWorld.getRawScene().setOnMouseReleased(this::onMouseReleased);
        this.gameWorld.getRawScene().setOnMouseMoved(mEv -> {
            onMouseMoved(mEv);
            this.mousePosition = new Vector2D(mEv.getX(), mEv.getY());
        });
        this.gameWorld.getRawScene().setOnMouseDragged(mEv -> {
            onMouseDragged(mEv);
            this.mousePosition = new Vector2D(mEv.getX(), mEv.getY());
        });

        this.devConsole.setFpsSupplier(() -> gameWorld.getFPS());  // assuming getFPS() exists
        this.devConsole.setObjectsInViewSupplier(() -> objectsInViewCount);
        this.devConsole.setTotalObjectsSupplier(gameObjects::size);

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

        this.worldCamera.update(deltaTime.asSeconds());

        // remove gameobjects
        gameObjects.removeAll(gameObjectsToRemove);
        gameObjectsToRemove.clear();
        gameObjects.addAll(gameObjectsToAdd);
        gameObjectsToAdd.clear();

        this.clearCanvas();

        Rectangle visibleBounds = getVisibleWorldBounds();
        fetchWorldContextAndRun((ctx) -> {
            List<GameObject> visibleObjects = this.gameObjects.stream()
                    .sorted(Comparator.comparingInt(GameObject::getRenderLayer))
                    .filter(obj -> shouldRenderInCamera(obj, visibleBounds))
                    .toList();

            visibleObjects.forEach(obj -> obj.invokeRender(ctx));
            this.renderDebug(ctx);
            this.objectsInViewCount = visibleObjects.size();
        });


        // remove debug shapes if times up
        this.debugShapes.removeIf(shape -> shape.isPastDestroyTime(TimeSpan.fromMilliseconds(System.currentTimeMillis())));

        // update dev console stats
        this.devConsole.updateStatsDisplay();
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
        gameObjectsToAdd.add(gameObject);
        gameObject.onInit();
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

    protected void playButtonClickSound()
    {
        Audios.clickNeutralRef().ifPresent(ref -> ref.play());
    }


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
        gameObjectsToRemove.add(gameObject);
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
        gameObjects.forEach(obj -> {
            if (type.isInstance(obj)) gameObjectsToRemove.add(obj);
        });
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
        gameObjects.forEach(obj -> {
            if (obj.hasTag(tag)) gameObjectsToRemove.add(obj);
        });
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
        gameObjectsToRemove.addAll(gameObjects);
        logger.debug("All GameObjects removed from scene");
    }

    /* DEV CONSOLE */

    private void registerDefaultDevCommands()
    {
        // Debug Grid
        devConsole.registerDevCommand(
                "/debugGrid", //
                "/debugGrid true|false|number", //
                "Toggle grid, with given size if number specified", //
                (args) -> {
                    // toggle if no argument provided
                    if (args.isEmpty())
                    {
                        setDebugGrid(!debugGrid);
                        return "debugGrid = " + debugGrid;
                    }

                    // check if first arg is a number
                    try
                    {
                        double arg = Double.parseDouble(args.getFirst());
                        if (arg <= 0) return "Grid size must be greater than 0";
                        setDebugGrid(true);
                        debugGridSize = arg;
                        return "debugGrid = true, size = " + debugGridSize;
                    }
                    // not a number -> treat a boolean
                    catch (Exception ignored)
                    {
                        boolean val = DevConsole.parseBooleanArg(args.getFirst());
                        setDebugGrid(val);
                        return "debugGrid = " + val;
                    }
                }
        );

        // Debug Mouse Location
        devConsole.registerDevCommand(
                "/debugMouseLocation",
                "/debugMouseLocation true|false|empty",
                "Toggle world mouse crosshair + coordinates.",
                (args) -> {
                    if (args.isEmpty())
                    {
                        // toggle if no argument provided
                        setDebugMouseLocation(!debugMouseLocation);
                        return "debugMouseLocation = " + debugMouseLocation;
                    }

                    boolean val = DevConsole.parseBooleanArg(args.getFirst());
                    setDebugMouseLocation(val);
                    return "debugMouseLocation = " + debugMouseLocation;
                }
        );

        // Set Camera Zoom
        devConsole.registerDevCommand(
                "/setCameraZoom",
                "/setCameraZoom <zoom>",
                "Set camera zoom level (default 1.0, must be > 0)",
                (args) -> {
                    try
                    {
                        double zoom = Double.parseDouble(args.getFirst());
                        if (zoom <= 0) return "Zoom must be greater than 0";
                        worldCamera.setZoom(zoom);
                        return "Camera zoom set to " + zoom;
                    }
                    catch (NumberFormatException e)
                    {
                        return "Invalid zoom value: " + args.getFirst();
                    }
                }
        );

        // Toggle scene UI layer visibility (console stays available).
        devConsole.registerDevCommand(
                "/toggleUI",
                "/toggleUI true|false|empty",
                "Toggle scene UI visibility (excluding world and dev console).",
                (args) -> {
                    if (args.isEmpty())
                    {
                        boolean next = !uiLayer.isVisible();
                        uiLayer.setVisible(next);
                        uiLayer.setManaged(next);
                        return "toggleUI = " + next;
                    }

                    boolean val = DevConsole.parseBooleanArg(args.getFirst());
                    uiLayer.setVisible(val);
                    uiLayer.setManaged(val);
                    return "toggleUI = " + val;
                }
        );
    }

    /**
     * Registers a developer console command specific to this scene.
     *
     * @param commandName The name of the command, used to activate the command
     * @param usage       The help text shown
     * @param description A description of what the command does, shown in the list of commands
     * @param executor    The function that is executed when the command is run, taking the command arguments as input
     *                    and returning a string output, that output is shown in the output list
     */
    protected final void registerDevCommand(String commandName, String usage, String description, DevConsole.DevCommandExecutor executor)
    {
        devConsole.registerDevCommand(commandName, usage, description, executor);
    }

    /**
     * Unregisters a developer console command specific to this scene.
     *
     * @param commandName The name of the command to unregister
     */
    protected final void unregisterDevCommand(String commandName)
    {
        devConsole.unregisterDevCommand(commandName);
    }

    /**
     * Gets a list of all registered developer console commands for this scene.
     *
     * @return List of all registered developer console command names for this scene
     */
    protected final List<String> getRegisteredDevCommands()
    {
        return devConsole.getRegisteredDevCommands();
    }

    /* UI LAYER */

    private Region initUILayer()
    {
        Region root = new StackPane(); // just for Intellisense

        String sceneName = this.getClass().getSimpleName();

        // FXML
        URL fxmlUrl = Resources.tryGetResource(GameURLs.SCENES_ROOT_DIR, sceneName, GameURLs.SCENE_FXML_FILENAME);
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
        canvas.getGraphicsContext2D().setImageSmoothing(false);
        return canvas;
    }

    /**
     * Clears the world canvas. When called everything in the frame will be cleared.
     */
    public void clearCanvas() { getRawContext().clearRect(0, 0, worldCanvas.getWidth(), worldCanvas.getHeight()); }

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
        Vector2D renderPosition = worldCamera.getRenderPosition();
        ctx.translate(-renderPosition.getX(), -renderPosition.getY());

        contextConsumer.accept(ctx);

        ctx.restore();
    }

    /* DEBUG TIMED */

    /// Adds a Debug Point visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugPoint#DEFAULT_LIFESPAN}
    public void addTimedDebugPoint(Vector2D position, double radius, TimeSpan lifespan)
    {
        if (debugShapes.add(new DebugPoint(position, radius, lifespan)))
            // logs only on distinct addition
            logger.trace("Added debug point at {} with radius {}", position, radius);
        else logger.trace("WARNING: Duplicate debug point at {} with radius {}", position, radius);
    }

    /// Adds a Debug Point visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugPoint#DEFAULT_LIFESPAN}
    public void addTimedDebugPoint(Vector2D position, double radius)
    {
        addTimedDebugPoint(position, radius, DebugPoint.DEFAULT_LIFESPAN);
    }

    /// Adds a Debug Point visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugPoint#DEFAULT_LIFESPAN}
    public void addTimedDebugPoint(Vector2D position, TimeSpan lifespan)
    {
        addTimedDebugPoint(position, DebugPoint.DEFAULT_RADIUS, lifespan);
    }

    /// Adds a Debug Point visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugPoint#DEFAULT_LIFESPAN}
    public void addTimedDebugPoint(Vector2D position)
    {
        addTimedDebugPoint(position, DebugPoint.DEFAULT_RADIUS, DebugPoint.DEFAULT_LIFESPAN);
    }

    // RECTANGLE

    /// Adds a Debug Rectangle visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugRectangle#DEFAULT_LIFESPAN}
    public void addTimedDebugRectangle(Rectangle rect, TimeSpan lifespan)
    {
        if (debugShapes.add(new DebugRectangle(rect.getTopLeft(), rect.getBottomRight(), lifespan)))
            // logs only on distinct addition
            logger.trace("Added debug rectangle at {} with size {}", rect.getTopLeft(), rect.getSize());
        else logger.trace("WARNING: Duplicate debug rectangle at {} with size {}", rect.getTopLeft(), rect.getSize());
    }

    /// Adds a Debug Rectangle visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugRectangle#DEFAULT_LIFESPAN}
    public void addTimedDebugRectangle(Rectangle rect)
    {
        addTimedDebugRectangle(rect, DebugShape.DEFAULT_LIFESPAN);
    }

    /// Adds a Debug Rectangle visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugRectangle#DEFAULT_LIFESPAN}
    public void addTimedDebugRectangle(double sx, double sy, double ex, double ey, TimeSpan lifespan)
    {
        addTimedDebugRectangle(Rectangle.fromCorners(sx, sy, ex, ey), lifespan);
    }

    /// Adds a Debug Rectangle visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugRectangle#DEFAULT_LIFESPAN}
    public void addTimedDebugRectangle(double sx, double sy, double ex, double ey)
    {
        addTimedDebugRectangle(Rectangle.fromCorners(sx, sy, ex, ey), DebugShape.DEFAULT_LIFESPAN);
    }

    // ELLIPSE

    /// Adds a Debug Ellipse visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugEllipse#DEFAULT_LIFESPAN}
    public void addTimedDebugEllipse(Rectangle rect, TimeSpan lifespan)
    {
        if (debugShapes.add(new DebugEllipse(rect.getTopLeft(), rect.getBottomRight(), lifespan)))
            // logs only on distinct addition
            logger.trace("Added debug ellipse at {} with size {}", rect.getTopLeft(), rect.getSize());
        else logger.trace("WARNING: Duplicate debug ellipse at {} with size {}", rect.getTopLeft(), rect.getSize());
    }

    /// Adds a Debug Ellipse visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugEllipse#DEFAULT_LIFESPAN}
    public void addTimedDebugEllipse(Rectangle rect) { addTimedDebugEllipse(rect, DebugShape.DEFAULT_LIFESPAN); }

    /// Adds a Debug Ellipse visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugEllipse#DEFAULT_LIFESPAN}
    public void addTimedDebugEllipse(double sx, double sy, double ex, double ey, TimeSpan lifespan)
    {
        addTimedDebugEllipse(Rectangle.fromCorners(sx, sy, ex, ey), lifespan);
    }

    /// Adds a Debug Ellipse visible for this the given {@link TimeSpan}
    /// <br><br>
    /// Must be called <b>{@code ONCE ONLY}</b>
    /// <br><br>
    /// Default lifespan is {@link DebugEllipse#DEFAULT_LIFESPAN}
    public void addTimedDebugEllipse(double sx, double sy, double ex, double ey)
    {
        addTimedDebugEllipse(Rectangle.fromCorners(sx, sy, ex, ey), DebugShape.DEFAULT_LIFESPAN);
    }

    /* DEBUG FRAME */

    // POINT

    /// Adds a Debug Point visible for this frame only
    /// <br><br>
    /// Must be called <b>{@code EACH FRAME}</b>
    public void addFrameDebugPoint(Vector2D position, double radius)
    {
        if (debugShapes.add(new DebugPoint(position, radius, true)))
            // logs only on distinct addition
            logger.trace("Added debug point at {} with radius {}", position, radius);
        else logger.trace("WARNING: Duplicate debug point at {} with radius {}", position, radius);
    }

    /// Adds a Debug Point visible for this frame only
    /// <br><br>
    /// Must be called <b>{@code EACH FRAME}</b>
    public void addFrameDebugPoint(Vector2D position) { addFrameDebugPoint(position, DebugPoint.DEFAULT_RADIUS); }

    // RECTANGLE

    /// Adds a Debug Rectangle visible for this frame only
    /// <br><br>
    /// Must be called <b>{@code EACH FRAME}</b>
    public void addFrameDebugRectangle(Rectangle rect)
    {
        if (debugShapes.add(new DebugRectangle(rect.getTopLeft(), rect.getBottomRight(), true)))
            // logs only on distinct addition
            logger.trace("Added debug rectangle at {} with size {}", rect.getTopLeft(), rect.getSize());
        else logger.trace("WARNING: Duplicate debug rectangle at {} with size {}", rect.getTopLeft(), rect.getSize());
    }

    /// Adds a Debug Rectangle visible for this frame only
    /// <br><br>
    /// Must be called <b>{@code EACH FRAME}</b>
    public void addFrameDebugRectangle(double sx, double sy, double ex, double ey)
    {
        addFrameDebugRectangle(Rectangle.fromCorners(sx, sy, ex, ey));
    }

    // ELLIPSE

    /// Adds a Debug Ellipse visible for this frame only
    /// <br><br>
    /// Must be called <b>{@code EACH FRAME}</b>
    public void addFrameDebugEllipse(Rectangle rect)
    {
        if (debugShapes.add(new DebugEllipse(rect.getTopLeft(), rect.getBottomRight(), true)))
            // logs only on distinct addition
            logger.trace("Added debug ellipse at {} with size {}", rect.getTopLeft(), rect.getSize());
        else logger.trace("WARNING: Duplicate debug ellipse at {} with size {}", rect.getTopLeft(), rect.getSize());
    }

    /// Adds a Debug Ellipse visible for this frame only
    /// <br><br>
    /// Must be called <b>{@code EACH FRAME}</b>
    public void addFrameDebugEllipse(double sx, double sy, double ex, double ey)
    {
        addFrameDebugEllipse(Rectangle.fromCorners(sx, sy, ex, ey));
    }

    /* DEBUG DRAW */

    private void renderDebug(GraphicsContext ctx)
    {
        debugShapes.forEach(obj -> {
            obj.setColors(ctx);
            obj.render(ctx);
        });

        drawDebugGrid(ctx);
        drawDebugMouseLocation(ctx);

        ctx.restore();
    }

    private void drawDebugGrid(GraphicsContext ctx)
    {
        if (!debugGrid) return;

        ctx.setStroke(Color.GRAY);
        ctx.setLineWidth(1);

        double zoom = worldCamera.getZoom();

        double canvasW = worldCanvas.getWidth();
        double canvasH = worldCanvas.getHeight();

        // camera center
        Vector2D renderPosition = worldCamera.getRenderPosition();
        double camX = renderPosition.getX();
        double camY = renderPosition.getY();

        // visible world bounds
        double halfW = canvasW / 2.0 / zoom;
        double halfH = canvasH / 2.0 / zoom;

        double left = camX - halfW;
        double right = camX + halfW;
        double top = camY - halfH;
        double bottom = camY + halfH;

        // snap to grid
        double startX = Math.floor(left / debugGridSize) * debugGridSize;
        double startY = Math.floor(top / debugGridSize) * debugGridSize;

        // vertical lines
        for (double x = startX; x <= right; x += debugGridSize)
        {
            ctx.strokeLine(x, top, x, bottom);
        }

        // horizontal lines
        for (double y = startY; y <= bottom; y += debugGridSize)
        {
            ctx.strokeLine(left, y, right, y);
        }
    }

    private void drawDebugMouseLocation(GraphicsContext ctx)
    {
        if (!debugMouseLocation) return;

        Vector2D worldPos = screenToWorld(mousePosition);

        // clamp to integer
        worldPos.setX(Math.floor(worldPos.getX()));
        worldPos.setY(Math.floor(worldPos.getY()));

        double zoom = worldCamera.getZoom();
        double canvasW = worldCanvas.getWidth();
        double canvasH = worldCanvas.getHeight();

        Vector2D renderPosition = worldCamera.getRenderPosition();
        double camX = renderPosition.getX();
        double camY = renderPosition.getY();

        double halfW = canvasW / 2.0 / zoom;
        double halfH = canvasH / 2.0 / zoom;

        double left = camX - halfW;
        double right = camX + halfW;
        double top = camY - halfH;
        double bottom = camY + halfH;

        ctx.setStroke(Color.RED);
        ctx.setLineWidth(1);

        // vertical line
        ctx.strokeLine(worldPos.getX(), top, worldPos.getX(), bottom);

        // horizontal line
        ctx.strokeLine(left, worldPos.getY(), right, worldPos.getY());

        // draw coordinates
        ctx.setFill(Color.WHITE);
        ctx.fillText(
                String.format("(%.2f, %.2f)", worldPos.getX(), worldPos.getY()),
                worldPos.getX() + 5,
                worldPos.getY() - 5
        );
    }

    /* CULLING */

    private Rectangle getVisibleWorldBounds()
    {
        double zoom = worldCamera.getZoom();
        double canvasW = worldCanvas.getWidth();
        double canvasH = worldCanvas.getHeight();

        Vector2D renderPosition = worldCamera.getRenderPosition();
        double camX = renderPosition.getX();
        double camY = renderPosition.getY();

        double halfW = canvasW / 2.0 / zoom;
        double halfH = canvasH / 2.0 / zoom;

        return Rectangle.fromCorners(camX - halfW, camY - halfH, camX + halfW, camY + halfH);
    }

    private boolean shouldRenderInCamera(GameObject obj, Rectangle visibleBounds)
    {
        if (!obj.isVisible()) return false;

        Rectangle renderBounds = getRenderableBounds(obj);
        if (renderBounds == null) return true;

        return renderBounds.intersects(visibleBounds);
    }

    private @Nullable Rectangle getRenderableBounds(GameObject obj)
    {
        for (Component component : obj.getAllComponents())
        {
            if (!component.isVisible()) continue;
            if (component instanceof WorldBoundsProvider provider)
            {
                Rectangle bounds = provider.getWorldBounds();
                if (bounds != null) return bounds;
            }
        }
        return null;
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

    Region getConsoleLayer() { return consoleLayer; }

    public Region getWorldLayer() { return worldLayer; }

    /**
     * Gets the {@link GameWorld} (window) that this scene belongs to.
     *
     * @return The GameWorld (window) that this scene belongs to
     */
    public GameWorld getWorld() { return gameWorld; }

    /**
     * Gets the {@link GameCamera} used for rendering the world layer of this scene.
     * <br><br>
     * Use to move the camera, zoom in/out, etc.
     *
     * @return The GameCamera used for rendering the world layer of this scene
     */
    public GameCamera getWorldCamera() { return worldCamera; }

    /// <b>{@code INTERNAL}</b>
    private Vector2D screenToWorld(Vector2D screen)
    {
        double zoom = worldCamera.getZoom();

        double canvasW = worldCanvas.getWidth();
        double canvasH = worldCanvas.getHeight();

        // move origin to center
        double x = screen.getX() - canvasW / 2.0;
        double y = screen.getY() - canvasH / 2.0;

        // undo zoom
        x /= zoom;
        y /= zoom;

        // add camera position
        x += worldCamera.getPosition().getX();
        y += worldCamera.getPosition().getY();

        return new Vector2D(x, y);
    }

    /* DEBUG */

    /// Gets the size of the debug grid squares.
    ///
    /// @see GameScene#setDebugGrid(boolean)
    public double getDebugGridSize() { return debugGridSize; }

    /// **`CHAINABLE`** Sets the size of the debug grid squares.
    ///
    /// @see GameScene#setDebugGrid(boolean)
    public GameScene setDebugGridSize(double debugGridSize)
    {
        this.debugGridSize = debugGridSize;
        return this;
    }

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

    /// Gets whether to show debug mouse coordinates or not
    ///
    /// @see GameScene#setDebugGrid(boolean)
    public boolean hasDebugMouseLocation() { return debugMouseLocation; }

    /// **`CHAINABLE`** Sets whether to show debug mouse location or not.
    public GameScene setDebugMouseLocation(boolean debugMouseLocation)
    {
        this.debugMouseLocation = debugMouseLocation;
        return this;
    }

    /// **`CHAINABLE`** Toggles the debug mouse location on/off.
    ///
    /// @see GameScene#setDebugMouseLocation(boolean)
    public GameScene toggleDebugMouseLocation() { return setDebugMouseLocation(!debugMouseLocation); }

    /// **`CHAINABLE`** Shows the debug mouse location.
    ///
    /// @see GameScene#setDebugMouseLocation(boolean)
    public GameScene showDebugMouseLocation() { return setDebugMouseLocation(true); }

    /// **`CHAINABLE`** Hides the debug mouse location.
    ///
    /// @see GameScene#setDebugMouseLocation(boolean)
    public GameScene hideDebugMouseLocation() { return setDebugMouseLocation(false); }

    /* LIFETIME EVENTS */

    @Override
    public void onInit() { }

    @Override
    public void onUpdate(TimeSpan deltaTime) { }

    @Override
    public void lateUpdate(TimeSpan deltaTime) { }

    /**
     * Optional hook for scene-specific developer console commands.
     * <br><br>
     * Override in a scene and call {@link #registerDevCommand(String, String, String, DevConsole.DevCommandExecutor)}.
     */
    protected void registerDevCommands() { }
}
