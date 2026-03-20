package edu.nust.engine.core;

import edu.nust.Main;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.resources.Resources;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

/// The core of the game engine
/// - Manages Window
/// - Manages Application lifetime
/// - Manages the all `GameScene`s
///
/// @see GameScene
public abstract class GameWindow
{
    protected final Stage stage;
    // When changing "scenes", we just change root
    private final Scene scene;
    private final StackPane sceneRoot;

    private GameScene currentGameScene;

    private final AnimationTimer gameLoop;
    private boolean updatesPaused = false;

    /// Subclasses **`MUST`** implement in order to initialize the stage `(set title, size, etc.)`
    protected abstract void initStage();

    public GameWindow(Stage stage)
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
        worldScene.setCamera(newScene.getWorldCamera());

        // add to root so `this.scene` is updates
        this.sceneRoot.getChildren().setAll(worldScene, newScene.getUILayer());

        this.setUpdatesPaused(false);
    }

    /// Get the current active [GameScene]
    public GameScene getScene() { return currentGameScene; }

    /// Only used in [edu.nust.engine.core] for internal purposes.
    /// <br>
    /// <br>
    /// Used for adding stylesheets, events, etc.
    protected Scene getRawScene() { return scene; }

    /* STATES */

    public void setUpdatesPaused(boolean state) { updatesPaused = state; }

    public boolean isUpdatesPaused() { return updatesPaused; }

    /* UTILITIES */

    public void setWindowTitle(String title) { stage.setTitle(title); }

    public double getWidth() { return stage.getWidth(); }

    public double getHeight() { return stage.getHeight(); }
}
