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

/// Represents a "scene" in the game, which can contain multiple `GameObject`s and has its own UI layout (root).
///
/// Can be used to represent different screen such as:
/// - Main Menu
/// - Game
/// - Settings Menus
///
/// Same as `Scene` in unity
///
/// @see GameObject
/// @see GameWorld
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

    // for world layer
    private final Canvas canvas;
    protected final List<GameObject> gameObjects = new ArrayList<>();
    // in `GameWorld`, when subscene for world layer is created, this is the camera attached to it
    private final GameCamera worldCamera;

    /* DEBUG OPTIONS */

    private boolean debugGrid = false;

    public GameScene(GameWorld window)
    {
        logger.trace("Constructing GameScene: {}", this.getClass().getSimpleName());
        LogProgress initSceneLogger = LogProgress.create("SCENE", logger);
        initSceneLogger.begin("Initializing scene: {}", this.getClass().getSimpleName());

        this.window = window;
        logger.trace("Canvas initialization starting");
        this.canvas = initCanvas();

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
        this.canvas.widthProperty().bind(this.worldLayer.widthProperty());
        this.canvas.heightProperty().bind(this.worldLayer.heightProperty());

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
            this.gameObjects.forEach(obj -> obj.invokeUpdate(deltaTime));
            // late update in reverse order to allow rendering changes to be applied in same frame
            this.gameObjects.forEach(obj -> obj.invokeLateUpdate(deltaTime));
            this.lateUpdate(deltaTime);
        }

        this.clearCanvas();

        fetchWorldContextAndRun((ctx) -> {
            this.gameObjects.forEach(obj -> obj.invokeRender(ctx));

            this.renderDebug(ctx);
        });
    }

    /* GAME OBJECT */

    public GameObject addGameObject(GameObject gameObject)
    {
        logger.trace("addGameObject({}) called", gameObject.getClass().getSimpleName());
        gameObject.setScene(this);
        gameObjects.add(gameObject);
        logger.debug("GameObject {} added to scene", gameObject.getClass().getSimpleName());
        return gameObject;
    }

    public GameObject addGameObject(Supplier<GameObject> gameObject) { return addGameObject(gameObject.get()); }

    public GameObject spawnGameObject(GameObject gameObject, Vector2D position)
    {
        logger.trace("spawnGameObject({}) at position {}", gameObject.getClass().getSimpleName(), position);
        return addGameObject(gameObject).getTransform().setPosition(position).getGameObject();
    }

    public GameObject spawnGameObject(Supplier<GameObject> object, Vector2D pos)
    {
        return spawnGameObject(object.get(), pos);
    }

    public List<GameObject> getAllGameObjects() { return gameObjects; }

    public @Nullable GameObject getFirstOfType(Class<? extends GameObject> type)
    {
        for (GameObject obj : gameObjects)
        {
            if (type.isInstance(obj)) return obj;
        }
        return null; // not found
    }

    public List<GameObject> getGameObjectsOfType(Class<? extends GameObject> type)
    {
        List<GameObject> result = new ArrayList<>();
        for (GameObject obj : gameObjects)
        {
            if (type.isInstance(obj)) result.add(obj);
        }
        return result;
    }

    public @Nullable GameObject getFirstWithTag(Class<? extends Tag> tag)
    {
        for (GameObject obj : gameObjects)
        {
            if (obj.hasTag(tag)) return obj;
        }
        return null; // not found
    }

    public List<GameObject> getAllWithTag(Class<? extends Tag> tag)
    {
        List<GameObject> result = new ArrayList<>();
        for (GameObject obj : gameObjects)
        {
            if (obj.hasTag(tag)) result.add(obj);
        }
        return result;
    }

    public void removeGameObject(GameObject gameObject)
    {
        logger.trace("removeGameObject({}) called", gameObject.getClass().getSimpleName());
        gameObjects.remove(gameObject);
        logger.debug("GameObject {} removed from scene", gameObject.getClass().getSimpleName());
    }

    public void removeGameObjectsOfType(Class<? extends GameObject> type)
    {
        logger.trace("removeGameObjectsOfType({}) called", type.getSimpleName());
        gameObjects.removeIf(type::isInstance);
        logger.debug("All GameObjects of type {} removed from scene", type.getSimpleName());
    }

    public void removeGameObjectsWithTag(Class<? extends Tag> tag)
    {
        logger.trace("removeGameObjectsWithTag({}) called", tag.getSimpleName());
        gameObjects.removeIf(obj -> obj.hasTag(tag));
        logger.debug("All GameObjects with tag {} removed from scene", tag.getSimpleName());
    }

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
        StackPane worldLayer = new StackPane(canvas);
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

    public void clearCanvas()
    {
        getRawContext().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private GraphicsContext getRawContext() { return canvas.getGraphicsContext2D(); }

    /// Fetches a `GraphicsContext` with the camera transformations, then runs the given function with it.
    public void fetchWorldContextAndRun(Consumer<GraphicsContext> contextConsumer)
    {
        GraphicsContext ctx = this.getRawContext();
        ctx.save();

        double zoom = worldCamera.getZoom();
        double canvasW = canvas.getWidth();
        double canvasH = canvas.getHeight();

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

            double canvasW = canvas.getWidth();
            double canvasH = canvas.getHeight();

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

    public GameWorld getWindow() { return window; }

    public Region getUILayer() { return uiLayer; }

    public Region getWorldLayer() { return worldLayer; }

    public GameCamera getWorldCamera() { return worldCamera; }

    /* DEBUG */

    public boolean hasDebugGrid() { return debugGrid; }

    public GameScene setDebugGrid(boolean debugGrid)
    {
        this.debugGrid = debugGrid;
        return this;
    }

    public GameScene toggleDebugGrid() { return setDebugGrid(!debugGrid); }

    public GameScene showDebugGrid() { return setDebugGrid(true); }

    public GameScene hideDebugGrid() { return setDebugGrid(false); }

    /* LIFETIME EVENTS */

    @Override
    public void onInit() { }

    @Override
    public void onUpdate(TimeSpan deltaTime) { }

    @Override
    public void lateUpdate(TimeSpan deltaTime) { }
}
