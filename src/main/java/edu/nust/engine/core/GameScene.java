package edu.nust.engine.core;

import edu.nust.engine.resources.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

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

    protected final StackPane root;
    private final Canvas drawCanvas;

    protected final List<GameObject> gameObjects = new ArrayList<>();

    public GameScene(GameWindow window)
    {
        this.window = window;

        // initialize layers
        Region uiRoot = loadFXMLUI();       // contains the elements defined in FXML
        this.drawCanvas = initCanvas();     // only contains the canvas itself

        // add to root
        this.root = new StackPane();
        this.root.getChildren().addAll(drawCanvas, uiRoot);

        // add CSS
        String sceneName = this.getClass().getSimpleName();
        URL cssUrl = Resources.tryGetResource("scenes", sceneName, "style.css");
        if (cssUrl == null) System.out.println("Missing CSS for: " + sceneName);
        else this.root.getStylesheets().add(cssUrl.toExternalForm());

        // make canvas resize with window
        this.drawCanvas.widthProperty().bind(this.root.widthProperty());
        this.drawCanvas.heightProperty().bind(this.root.heightProperty());

        // start the scene
        onStart();
        gameObjects.forEach(GameObject::onInit);

        // add events
        this.getRoot().setOnKeyPressed(this::onKeyPressed);
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
        GraphicsContext context = getCanvasContext();
        context.clearRect(0, 0, drawCanvas.getWidth(), drawCanvas.getHeight());
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

    public StackPane getRoot()
    {
        return root;
    }

    public Canvas getCanvas()
    {
        return drawCanvas;
    }
}
