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
import javafx.scene.layout.VBox;
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

    private final Canvas canvas;
    private final StackPane uiLayer;    // data form fxml
    private final StackPane worldLayer; // all gameobjects

    protected final List<GameObject> gameObjects = new ArrayList<>();

    // every scene must have one camera
    private final PerspectiveCamera camera;

    public GameScene(GameWindow window)
    {
        this.window = window;

        this.canvas = initCanvas();
        // initialize layers
        this.uiLayer = new StackPane(loadFXMLUI());     // contains the elements defined in FXML
        this.worldLayer = new StackPane(canvas); // contains canvas with game objects rendered on it

        // add CSS
        String sceneName = this.getClass().getSimpleName();
        URL cssUrl = Resources.tryGetResource("scenes", sceneName, "style.css");
        if (cssUrl == null) System.out.println("Missing CSS for: " + sceneName);
        else this.getWindow().getRawScene().getStylesheets().add(cssUrl.toExternalForm());

        // make canvas resize with window
        this.canvas.widthProperty().bind(this.worldLayer.widthProperty());
        this.canvas.heightProperty().bind(this.worldLayer.heightProperty());

        // start the scene
        onStart();
        this.gameObjects.forEach(GameObject::onInit);

        // add events
        this.getWindow().getRawScene().setOnKeyPressed(this::onKeyPressed);

        // initialize camera
        this.camera = new PerspectiveCamera();
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

    public GameObject addGameObject(Supplier<GameObject> gameObject)
    {
        GameObject object = gameObject.get();
        object.setScene(this);
        gameObjects.add(object);
        return object;
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

    private Region loadFXMLUI()
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

    public GraphicsContext getCanvasContext()
    {
        return canvas.getGraphicsContext2D();
    }

    /* EVENTS */

    protected void onKeyPressed(KeyEvent event)
    {
        // Override in subclasses if needed
    }

    /* GETTERS AND SETTERS */

    public GameWindow getWindow()
    {
        return window;
    }

    public StackPane getUILayer()
    {
        return uiLayer;
    }

    public StackPane getWorldLayer()
    {
        return worldLayer;
    }

    public Canvas getCanvas()
    {
        return canvas;
    }

    public PerspectiveCamera getCamera()
    {
        return camera;
    }
}
