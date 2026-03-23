package edu.nust.engine.core;

import edu.nust.engine.core.gameobjects.Tag;
import edu.nust.engine.core.interfaces.InputHandler;
import edu.nust.engine.core.interfaces.Updatable;
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
public abstract class GameScene implements Updatable<GameScene>, InputHandler
{
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
        this.window = window;
        this.canvas = initCanvas();

        // initialize layers
        this.uiLayer = initUILayer();
        this.worldLayer = initWorldLayer();

        // add CSS
        String sceneName = this.getClass().getSimpleName();
        URL cssUrl = Resources.tryGetResource("scenes", sceneName, "style.css");
        if (cssUrl == null) System.out.println("Missing CSS for: " + sceneName);
        else this.window.getRawScene().getStylesheets().add(cssUrl.toExternalForm());
        // add CSS to raw scene to allow overriding

        // bind canvas size to world layer, which is bound to `window.root`
        this.canvas.widthProperty().bind(this.worldLayer.widthProperty());
        this.canvas.heightProperty().bind(this.worldLayer.heightProperty());

        // start the scene
        onStart();
        this.gameObjects.forEach(GameObject::onInit);

        // add events
        this.window.getRawScene().setOnKeyPressed(this::onKeyPressed);
        this.window.getRawScene().setOnKeyReleased(this::onKeyReleased);
        this.window.getRawScene().setOnMousePressed(this::onMousePressed);
        this.window.getRawScene().setOnMouseReleased(this::onMouseReleased);
        this.window.getRawScene().setOnMouseMoved(this::onMouseMoved);

        // initialize camera
        this.worldCamera = new GameCamera();
    }

    // package-private so classes outside package cannot call it, neither can subclasses override it
    void invokeUpdate(TimeSpan deltaTime)
    {
        if (active)
        {
            this.onUpdate(deltaTime);
            this.gameObjects.forEach(obj -> {
                if (!obj.isActive()) return;

                obj.onUpdate(deltaTime);
                obj.updateComponents(deltaTime);
            });
        }

        this.clearCanvas();

        fetchWorldContextAndRun((ctx) -> {
            this.gameObjects.forEach(obj -> {
                if (!obj.isVisible()) return;

                obj.onRender(ctx);
                obj.renderComponents(ctx);
            });

            this.renderDebug(ctx);
        });
    }

    /* LIFETIME */

    protected abstract void onStart();

    @Override
    public abstract void onUpdate(TimeSpan deltaTime);

    /* CHILDREN */

    public GameObject addGameObject(GameObject gameObject)
    {
        gameObject.setScene(this);
        gameObjects.add(gameObject);
        return gameObject;
    }

    public GameObject addGameObject(Supplier<GameObject> gameObject) { return addGameObject(gameObject.get()); }

    public GameObject spawnGameObject(GameObject gameObject, Vector2D position)
    {
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

    public void removeGameObject(GameObject gameObject) { gameObjects.remove(gameObject); }

    public void removeGameObjectsOfType(Class<? extends GameObject> type)
    {
        gameObjects.removeIf(type::isInstance);
    }

    public void removeGameObjectsWithTag(Class<? extends Tag> tag)
    {
        gameObjects.removeIf(obj -> obj.hasTag(tag));
    }

    public void removeAllGameObjects() { gameObjects.clear(); }

    /* UI LAYER */

    private Region initUILayer()
    {
        Region root;

        String sceneName = this.getClass().getSimpleName();

        // FXML
        URL fxmlUrl = Resources.tryGetResource("scenes", sceneName, "layout.fxml");
        if (fxmlUrl == null) throw new RuntimeException("Missing FXML for: " + sceneName);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setController(this);

        try
        {
            root = loader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load FXML: " + sceneName, e);
        }

        return root;
    }

    /* CANVAS LAYER */

    private Region initWorldLayer()
    {
        StackPane worldLayer = new StackPane(canvas);
        worldLayer.setPickOnBounds(false); // allow clicks to pass through to UI layer
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
        this.active = active;
        if (active) onActivate();
        else onDeactivate();
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

    public GameScene toggleDebugGrid()
    {
        setDebugGrid(!debugGrid);
        return this;
    }

    public GameScene showDebugGrid()
    {
        this.debugGrid = true;
        return this;
    }

    public GameScene hideDebugGrid()
    {
        this.debugGrid = false;
        return this;
    }
}
