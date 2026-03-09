package edu.nust.engine.core;

import edu.nust.engine.resources.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

    protected Parent root;

    protected static final String DRAW_CANVAS_SELECTOR = "#drawCanvas";
    protected @Nullable Canvas drawCanvas;

    protected final List<GameObject> gameObjects = new ArrayList<>();

    public GameScene(GameWindow window)
    {
        this.window = window;

        loadFXMLScene();
        if (root == null) throw new RuntimeException("Root not initialized");

        // try to find a canvas in the scene for drawing
        Node node = root.lookup(DRAW_CANVAS_SELECTOR);

        if (node instanceof Canvas canvas)
        {
            drawCanvas = canvas;
        }

        onStart();
        gameObjects.forEach(GameObject::onInit);
    }

    /* ABSTRACT METHODS */

    protected abstract void onStart();

    protected abstract void onUpdate(double deltaTime);

    /* DRAW CANVAS */

    public boolean hasCanvas()
    {
        return drawCanvas != null;
    }

    public @Nullable Canvas getCanvas()
    {
        return drawCanvas;
    }

    public @Nullable GraphicsContext getCanvasContext()
    {
        return drawCanvas != null ? drawCanvas.getGraphicsContext2D() : null;
    }

    /* CHILDREN */

    public void addGameObject(Supplier<GameObject> gameObject)
    {
        GameObject object = gameObject.get();
        object.setScene(this);
        gameObjects.add(object);
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

    /* FXML AND CSS */

    protected void loadFXMLScene()
    {
        String sceneName = this.getClass().getSimpleName();

        // FXML
        URL fxmlUrl = Resources.tryGetResource("scenes", sceneName, "layout.fxml");
        if (fxmlUrl == null) throw new RuntimeException("Missing FXML for: " + sceneName);

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setController(this);

        try
        {
            this.root = loader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load FXML: " + sceneName, e);
        }

        // CSS
        URL cssUrl = Resources.tryGetResource("scenes", sceneName, "style.css");
        if (cssUrl == null) System.out.println("Missing CSS for: " + sceneName);
        else root.getStylesheets().add(cssUrl.toExternalForm());
    }
}
