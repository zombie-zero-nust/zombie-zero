package edu.nust.engine.core;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.resources.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.PerspectiveCamera;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
/// @see GameWindow
public abstract class GameScene
{
    private final GameWindow window;

    /// Contains all elements etc. loaded from FXML file for thus scene
    private final Region uiLayer;
    /// Contains a canvas that renders all GameObjects
    private final Region worldLayer;

    // for world layer
    private final Canvas canvas;
    protected final List<GameObject> gameObjects = new ArrayList<>();
    // in `GameWindow`, when subscene for world layer is created, this is the camera attached to it
    private final PerspectiveCamera worldCamera;

    public GameScene(GameWindow window)
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

        // initialize camera
        this.worldCamera = new PerspectiveCamera();
    }

    // package-private so classes outside package cannot call it, neither can subclasses override it
    void invokeUpdate(TimeSpan deltaTime)
    {
        if (!window.isUpdatesPaused())
        {
            this.onUpdate(deltaTime);
            this.gameObjects.forEach(obj -> obj.onUpdate(deltaTime));
        }

        this.clearCanvas();
        this.gameObjects.forEach(obj -> obj.onRender(this.getCanvasContext()));
    }

    /* LIFETIME */

    protected abstract void onStart();

    protected abstract void onUpdate(TimeSpan deltaTime);

    /* CHILDREN */

    public GameObject addGameObject(GameObject gameObject)
    {
        gameObject.setScene(this);
        gameObjects.add(gameObject);
        return gameObject;
    }

    public GameObject addGameObject(Supplier<GameObject> gameObject)
    {
        return addGameObject(gameObject.get());
    }

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

    public List<GameObject> getAllGameObjects()
    {
        return gameObjects;
    }

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
        getCanvasContext().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public GraphicsContext getCanvasContext() { return canvas.getGraphicsContext2D(); }

    /* EVENTS */

    protected void onKeyPressed(KeyEvent event)
    {
        // Override in subclasses if needed
    }

    /* LAYERS AND CAMERA */

    public GameWindow getWindow() { return window; }

    public Region getUILayer() { return uiLayer; }

    public Region getWorldLayer() { return worldLayer; }

    public PerspectiveCamera getWorldCamera() { return worldCamera; }
}
