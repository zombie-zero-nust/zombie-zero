package edu.nust.engine.core;

import edu.nust.engine.resources.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

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

    protected StackPane root;
    protected Parent uiRoot;
    protected Canvas drawCanvas;

    public final Color backgroundColor;

    protected final List<GameObject> gameObjects = new ArrayList<>();

    public GameScene(GameWindow window, Color backgroundColor)
    {
        this.window = window;
        this.backgroundColor = backgroundColor;

        // initialize layers
        this.uiRoot = initUIRoot();
        this.drawCanvas = initCanvas();

        // add to root
        this.root = new StackPane();
        this.root.getChildren().addAll(drawCanvas, uiRoot);

        // make canvas resize with window
        this.drawCanvas.widthProperty().bind(this.root.widthProperty());
        this.drawCanvas.heightProperty().bind(this.root.heightProperty());

        // start the scene
        onStart();
        gameObjects.forEach(GameObject::onInit);

        // add events
        this.getRoot().setOnKeyPressed(this::onKeyPressed);
    }

    public GameScene(GameWindow window)
    {
        this(window, Color.BLACK);
    }

    /* LIFETIME */

    protected abstract void onStart();

    protected abstract void onUpdate(double deltaTime);

    /* CHILDREN */

    public GameObject addGameObject(Supplier<GameObject> gameObject)
    {
        GameObject object = gameObject.get();
        object.setScene(this);
        gameObjects.add(object);
        return object;
    }

    /* UI LAYER */

    private Parent initUIRoot()
    {
        Parent root;

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

        // CSS
        URL cssUrl = Resources.tryGetResource("scenes", sceneName, "style.css");
        if (cssUrl == null) System.out.println("Missing CSS for: " + sceneName);
        else root.getStylesheets().add(cssUrl.toExternalForm());

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
        GraphicsContext context = getCanvasContext();
        context.clearRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
        context.setFill(backgroundColor);
        context.fillRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
    }

    public GraphicsContext getCanvasContext()
    {
        return drawCanvas.getGraphicsContext2D();
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

    public Parent getRoot()
    {
        return root;
    }

    public Canvas getCanvas()
    {
        return drawCanvas;
    }
}
