package edu.nust.engine.core;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

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

    public GameScene(GameWindow window)
    {
        this.window = window;

        loadFXMLScene();
        if (root == null) throw new RuntimeException("Root not initialized");

        onStart();
    }

    /* CHILD METHODS */

    public abstract String getName();

    protected abstract void onStart();

    protected abstract void onUpdate(double deltaTime);

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
        String sceneName = getClass().getSimpleName();
        String basePath = "/edu/nust/game/scenes/" + sceneName + "/";

        // FXML
        URL fxmlUrl = GameScene.class.getResource(basePath + "layout.fxml");
        if (fxmlUrl == null) throw new RuntimeException("Missing FXML: " + basePath + "layout.fxml");

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
        URL cssUrl = GameScene.class.getResource(basePath + "style.css");
        if (cssUrl == null) System.out.println("Missing CSS: " + basePath + "style.css");
        else root.getStylesheets().add(cssUrl.toExternalForm());
    }
}
