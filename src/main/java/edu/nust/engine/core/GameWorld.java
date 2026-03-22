package edu.nust.engine.core;

import edu.nust.Main;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import javafx.animation.AnimationTimer;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.function.Supplier;

/// The core of the game engine, currently:
/// - Manages Window
/// - Manages Application lifetime
/// - Manages the all `GameScene`s
///
/// <br><br>
/// In future will also manage:
/// - Client Server connection
///
/// @see GameScene
public abstract class GameWorld
{
    protected final Stage stage;
    // When changing "scenes", we just change root
    private final Scene scene;
    private final StackPane sceneRoot;

    private GameScene currentGameScene;

    private final AnimationTimer gameLoop;

    /// Subclasses **`MUST`** implement in order to initialize the stage `(set title, size, etc.)`
    protected abstract void initStage();

    public GameWorld(Stage stage)
    {
        this.stage = stage;

        // setup scene
        this.sceneRoot = new StackPane();
        this.scene = new Scene(this.sceneRoot);
        this.stage.setScene(this.scene);

        // bind `root.size` to `stage.size` so that when stage is resized
        this.sceneRoot.prefWidthProperty().bind(this.stage.widthProperty());
        this.sceneRoot.prefHeightProperty().bind(this.stage.heightProperty());

        URL commonCssUrl = Resources.tryGetResource("scenes", "common.css");
        if (commonCssUrl != null)
        {
            this.scene.getStylesheets().add(commonCssUrl.toExternalForm());
        }

        initStage();

        this.gameLoop = new AnimationTimer()
        {
            private long lastTime = 0;

            @Override
            public void handle(long now)
            {
                // if first frame, initialize lastTime and skip update
                if (lastTime == 0)
                {
                    lastTime = now;
                    return;
                }

                // calculate time between this frame and last frame
                long deltaTimeNs = now - lastTime;
                lastTime = now; // update lastTime for next frame

                if (currentGameScene != null) currentGameScene.invokeUpdate(TimeSpan.fromNanoseconds(deltaTimeNs));
            }
        };
    }

    /// Call in program entry point i.e. [Main#start(Stage stage)] Starts the Game Loop
    public void start()
    {
        stage.show();
        gameLoop.start();
    }

    /// Call to exit and close
    public void stop()
    {
        gameLoop.stop();
        stage.close();
    }

    /* SCENE */

    public void setScene(GameScene newScene)
    {
        this.currentGameScene = newScene;

        SubScene worldScene = new SubScene(newScene.getWorldLayer(), this.stage.getWidth(), this.stage.getHeight());
        // bind world scene size to root size
        worldScene.widthProperty().bind(this.sceneRoot.widthProperty());
        worldScene.heightProperty().bind(this.sceneRoot.heightProperty());
        // set camera for world
        // worldScene.setCamera(newScene.getWorldCamera());

        // add to root so `this.scene` is updates
        this.sceneRoot.getChildren().setAll(worldScene, newScene.getUILayer());
    }

    public void setScene(Supplier<GameScene> newScene) { setScene(newScene.get()); }

    /// Get the current active [GameScene]
    public GameScene getScene() { return currentGameScene; }

    /// Only used in [edu.nust.engine.core] for internal purposes.
    /// <br>
    /// <br>
    /// Used for adding stylesheets, events, etc.
    protected Scene getRawScene() { return scene; }

    /* UTILITIES */

    public void centerWindow() { stage.centerOnScreen(); }

    public void setWindowTitle(String title) { stage.setTitle(title); }

    public String getWindowTitle() { return stage.getTitle(); }

    public void setCursor(Cursor cursor) { stage.getScene().setCursor(cursor); }

    public Cursor getCursor() { return stage.getScene().getCursor(); }

    public void setCursorVisible(boolean visible) { if (!visible) setCursor(Cursor.NONE); }

    public boolean isCursorVisible() { return getCursor() != Cursor.NONE; }

    public void toggleCursorVisible() { setCursorVisible(!isCursorVisible()); }

    public void setResizable(boolean resizable) { stage.setResizable(resizable); }

    public void setFullscreen(boolean fullscreen) { stage.setFullScreen(fullscreen); }

    public void toggleFullscreen() { setFullscreen(!stage.isFullScreen()); }

    public void isFullscreen(boolean fullscreen) { stage.setFullScreen(fullscreen); }

    public void setSize(double width, double height)
    {
        stage.setWidth(width);
        stage.setHeight(height);
    }

    public void setWidth(double width) { stage.setWidth(width); }

    public void setHeight(double height) { stage.setHeight(height); }

    public Vector2D getSize() { return new Vector2D(stage.getWidth(), stage.getHeight()); }

    public double getWidth() { return stage.getWidth(); }

    public double getHeight() { return stage.getHeight(); }

}
