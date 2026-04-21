package edu.nust.engine.core;

import edu.nust.engine.core.gameobjects.Tag;
import edu.nust.engine.core.interfaces.Initiable;
import edu.nust.engine.core.interfaces.InputHandler;
import edu.nust.engine.core.interfaces.Updatable;
import edu.nust.engine.debug.DebugEllipse;
import edu.nust.engine.debug.DebugPoint;
import edu.nust.engine.debug.DebugRectangle;
import edu.nust.engine.debug.DebugShape;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.*;
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

    private final List<DebugShape> debugShapes = new ArrayList<>();

    // debug options
    private boolean debugGrid = false;
    private boolean debugMouseLocation = false;

    protected Vector2D mousePosition = Vector2D.zero();

    // Built-in developer console (available to every scene)
    private final Map<String, DevCommand> devCommands = new LinkedHashMap<>();
    private final List<String> consoleSuggestions = new ArrayList<>();
    private final List<String> consoleHistory = new ArrayList<>();
    private int historyIndex = -1;
    private int suggestionIndex = -1;
    private boolean devConsoleOpen = false;

    private final VBox devConsoleContainer = new VBox(6);
    private final Label devConsoleHint = new Label();
    private final TextField devConsoleInput = new TextField();

    @FunctionalInterface
    protected interface DevCommandExecutor
    {
        String execute(List<String> args);
    }

    private record DevCommand(String name, String usage, String description, DevCommandExecutor executor) { }

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
        this.consoleLayer = initConsoleLayer();

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

        registerBuiltInDevCommands();
        setupDevConsole();

        // start the scene
        logger.trace("Calling onStart() for scene setup");
        onInit();
        logger.trace("Initializing all GameObjects");
        this.gameObjects.forEach(GameObject::onInit);

        // add events
        logger.trace("Registering input event handlers");
        this.gameWorld.getRawScene().setOnKeyPressed(this::handleKeyPressed);
        this.gameWorld.getRawScene().setOnKeyReleased(this::handleKeyReleased);
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

        // remove gameobjects
        gameObjects.removeAll(gameObjectsToRemove);
        gameObjectsToRemove.clear();
        gameObjects.addAll(gameObjectsToAdd);
        gameObjectsToAdd.clear();

        this.clearCanvas();

        fetchWorldContextAndRun((ctx) -> {
            // Create a copy of the list to iterate over to avoid ConcurrentModificationException
            this.gameObjects.stream()
                    .sorted(Comparator.comparingInt(GameObject::getRenderLayer))
                    .forEach(obj -> obj.invokeRender(ctx));

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
        gameObjectsToAdd.add(gameObject);
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

    private void setupDevConsole()
    {
        devConsoleContainer.setVisible(false);
        devConsoleContainer.setManaged(false);
        devConsoleContainer.getStyleClass().add("dev-console");
        devConsoleContainer.setMaxWidth(520);
        devConsoleContainer.setPadding(new Insets(10));
        devConsoleContainer.setStyle(
                "-fx-background-color: rgba(20,20,20,0.92); -fx-border-color: rgba(255,255,255,0.22); -fx-border-width: 1;");

        devConsoleHint.setText("Dev Console");
        devConsoleHint.setStyle("-fx-text-fill: #b8e0ff;");

        devConsoleInput.setPromptText("Type a command (e.g. /debugGrid true)");
        devConsoleInput.setFocusTraversable(false);
        devConsoleInput.setStyle("-fx-background-color: #0f0f0f; -fx-text-fill: #eeeeee; -fx-prompt-text-fill: #777777;");

        devConsoleInput.textProperty().addListener((obs, oldValue, newValue) -> updateConsoleAutocomplete());
        devConsoleInput.setOnKeyPressed(this::handleConsoleKeyPressed);

        devConsoleContainer.getChildren().setAll(devConsoleHint, devConsoleInput);
    }

    private void toggleDevConsole()
    {
        devConsoleOpen = !devConsoleOpen;
        devConsoleContainer.setVisible(devConsoleOpen);
        devConsoleContainer.setManaged(devConsoleOpen);

        if (devConsoleOpen)
        {
            devConsoleInput.requestFocus();
            devConsoleInput.positionCaret(devConsoleInput.getText().length());
            updateConsoleAutocomplete();
        }
        else
        {
            getWorld().getRawScene().getRoot().requestFocus();
        }
    }

    private boolean isConsoleToggleShortcut(KeyEvent event)
    {
        if (!event.isShiftDown()) return false;
        return event.getCode() == KeyCode.BACK_QUOTE;
    }

    private void handleKeyPressed(KeyEvent event)
    {
        if (isConsoleToggleShortcut(event))
        {
            toggleDevConsole();
            event.consume();
            return;
        }

        if (devConsoleOpen)
        {
            event.consume();
            return;
        }

        onKeyPressed(event);
    }

    private void handleKeyReleased(KeyEvent event)
    {
        // Let releases flow to scenes so held movement keys can be cleared safely.
        onKeyReleased(event);

        if (devConsoleOpen) event.consume();
    }

    private void handleConsoleKeyPressed(KeyEvent event)
    {
        switch (event.getCode())
        {
            case ENTER ->
            {
                executeConsoleInput();
                event.consume();
            }
            case TAB ->
            {
                applySuggestion(event.isShiftDown() ? -1 : 1);
                event.consume();
            }
            case UP ->
            {
                navigateHistory(-1);
                event.consume();
            }
            case DOWN ->
            {
                navigateHistory(1);
                event.consume();
            }
            case ESCAPE ->
            {
                toggleDevConsole();
                event.consume();
            }
            default ->
            {
            }
        }
    }

    private void executeConsoleInput()
    {
        String input = devConsoleInput.getText().trim();
        if (input.isBlank()) return;

        consoleHistory.add(input);
        historyIndex = consoleHistory.size();

        String result = executeDevCommand(input);
        devConsoleHint.setText(result);

        devConsoleInput.clear();
        updateConsoleAutocomplete();
    }

    private String executeDevCommand(String input)
    {
        if (!input.startsWith("/")) return "Commands must start with '/'";

        String[] tokens = input.split("\\s+");
        String commandName = normalizeCommandName(tokens[0]);
        DevCommand command = devCommands.get(commandName);

        if (command == null)
        {
            List<String> candidates = new ArrayList<>();
            for (String registered : devCommands.keySet())
            {
                if (registered.startsWith(commandName)) candidates.add(registered);
            }

            if (!candidates.isEmpty())
            {
                return "Unknown command. Did you mean " + String.join(", ", candidates) + " ?";
            }
            return "Unknown command: " + tokens[0];
        }

        List<String> args = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++)
        {
            if (!tokens[i].isBlank()) args.add(tokens[i]);
        }

        try
        {
            return command.executor().execute(args);
        }
        catch (Exception e)
        {
            logger.error(false, "Dev command '{}' failed: {}", command.name(), e.getMessage());
            return "Command failed: " + e.getMessage();
        }
    }

    private void navigateHistory(int delta)
    {
        if (consoleHistory.isEmpty()) return;

        historyIndex = Math.clamp(historyIndex + delta, 0, consoleHistory.size());

        if (historyIndex == consoleHistory.size())
        {
            devConsoleInput.clear();
            return;
        }

        devConsoleInput.setText(consoleHistory.get(historyIndex));
        devConsoleInput.positionCaret(devConsoleInput.getText().length());
    }

    private void updateConsoleAutocomplete()
    {
        consoleSuggestions.clear();
        suggestionIndex = -1;

        String input = devConsoleInput.getText().trim();

        if (!input.startsWith("/"))
        {
            devConsoleHint.setText("Dev Console");
            return;
        }

        String commandToken = normalizeCommandName(input.split("\\s+")[0]);
        for (DevCommand command : devCommands.values())
        {
            if (command.name().startsWith(commandToken)) consoleSuggestions.add(command.usage());
        }

        if (consoleSuggestions.isEmpty())
        {
            devConsoleHint.setText("No matching commands");
        }
        else
        {
            suggestionIndex = 0;
            devConsoleHint.setText("Suggestions: " + String.join("  |  ", consoleSuggestions));
        }
    }

    private void applySuggestion(int direction)
    {
        if (consoleSuggestions.isEmpty()) return;

        int count = consoleSuggestions.size();
        suggestionIndex = (suggestionIndex + direction + count) % count;

        String usage = consoleSuggestions.get(suggestionIndex);
        String command = usage.split("\\s+")[0];
        devConsoleInput.setText(command + " ");
        devConsoleInput.positionCaret(devConsoleInput.getText().length());
        devConsoleHint.setText("Auto-complete: " + usage);
    }

    private void registerBuiltInDevCommands()
    {
        registerDevCommand(
                "/debugGrid", "/debugGrid true|false", "Toggle the world debug grid.", args -> {
                    Boolean value = parseBooleanArg(args);
                    if (value == null) return "Usage: /debugGrid true|false";
                    setDebugGrid(value);
                    return "debugGrid set to " + value;
                }
        );

        registerDevCommand(
                "/debugMouseLocation",
                "/debugMouseLocation true|false",
                "Toggle world mouse crosshair + coordinates.",
                args -> {
                    Boolean value = parseBooleanArg(args);
                    if (value == null) return "Usage: /debugMouseLocation true|false";
                    setDebugMouseLocation(value);
                    return "debugMouseLocation set to " + value;
                }
        );
    }

    protected final void registerDevCommand(String commandName, String usage, String description, DevCommandExecutor executor)
    {
        String normalized = normalizeCommandName(commandName);
        devCommands.put(normalized, new DevCommand(normalized, usage, description, executor));
    }

    protected final void unregisterDevCommand(String commandName)
    {
        devCommands.remove(normalizeCommandName(commandName));
    }

    protected final List<String> getRegisteredDevCommands()
    {
        return new ArrayList<>(devCommands.keySet());
    }

    private String normalizeCommandName(String commandName)
    {
        if (commandName == null || commandName.isBlank()) return "/";
        String normalized = commandName.trim().toLowerCase(Locale.ROOT);
        return normalized.startsWith("/") ? normalized : "/" + normalized;
    }

    private static Boolean parseBooleanArg(List<String> args)
    {
        if (args == null || args.isEmpty()) return null;
        String raw = args.getFirst().toLowerCase(Locale.ROOT);
        return switch (raw)
        {
            case "true", "1", "on", "yes" -> true;
            case "false", "0", "off", "no" -> false;
            default -> null;
        };
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

        // if (root instanceof Pane pane)
        // {
        //     StackPane.setAlignment(devConsoleContainer, Pos.TOP_LEFT);
        //     pane.getChildren().add(devConsoleContainer);
        // }
        // else
        // {
        //     StackPane wrapper = new StackPane();
        //     wrapper.getChildren().addAll(root, devConsoleContainer);
        //     StackPane.setAlignment(devConsoleContainer, Pos.TOP_LEFT);
        //     root = wrapper;
        // }

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

    private Region initConsoleLayer()
    {
        StackPane layer = new StackPane();
        layer.setPickOnBounds(false); // allow clicks to pass through when hidden

        StackPane.setAlignment(devConsoleContainer, Pos.TOP_LEFT);
        layer.getChildren().add(devConsoleContainer);

        return layer;
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
        ctx.translate(-worldCamera.getPosition().getX(), -worldCamera.getPosition().getY());

        contextConsumer.accept(ctx);

        ctx.restore();
    }

    /* DEBUG */

    /// Must be called <b>{@code EACH FRAME}</b>
    public void addDebugPoint(Vector2D position, double radius)
    {
        debugShapes.add(DebugPoint.from(position, radius));
        logger.trace("Added debug point at {} with radius {}", position, radius);
    }

    /// Must be called <b>{@code EACH FRAME}</b>
    public void addDebugPoint(Vector2D position) { addDebugPoint(position, DebugPoint.DEFAULT_RADIUS); }

    /// Must be called <b>{@code EACH FRAME}</b>
    public void addDebugRectangle(Rectangle rect)
    {
        debugShapes.add(DebugRectangle.fromSize(rect.getTopLeft(), rect.getSize()));
        logger.trace("Added debug rectangle at {} with size {}", rect.getTopLeft(), rect.getSize());
    }

    /// Must be called <b>{@code EACH FRAME}</b>
    public void addDebugEllipse(Rectangle rect)
    {
        debugShapes.add(DebugEllipse.fromSize(rect.getTopLeft(), rect.getSize()));
        logger.trace("Added debug rectangle at {} with size {}", rect.getTopLeft(), rect.getSize());
    }

    private void renderDebug(GraphicsContext ctx)
    {
        debugShapes.forEach(obj -> {
            obj.setColors(ctx);
            obj.render(ctx);
        });

        drawDebugGrid(ctx);
        drawDebugMouseLocation(ctx);

        debugShapes.clear();
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

        double camX = worldCamera.getPosition().getX();
        double camY = worldCamera.getPosition().getY();

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
}
