package edu.nust.engine.core;

import edu.nust.Main;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.LogProgress;
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
    protected final GameLogger logger = GameLogger.getLogger(this.getClass());

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
            logger.debug("Loaded common.css");
        }
        else
        {
            logger.warn("\"common.css\" not found, ensure 'edu/nust/game/scenes/common.css' if not intentional.");
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

        logger.success("Game World initialized successfully");
    }

    /// Call in program entry point i.e. [Main#start(Stage stage)] Starts the Game Loop
    public void start()
    {
        logger.success("Game started");
        stage.show();
        gameLoop.start();
    }

    /// Call to exit and close
    public void stop()
    {
        gameLoop.stop();
        stage.close();
        logger.success("Game stopped");
    }

    /* SCENE */

    public GameWorld setScene(GameScene newScene)
    {
        LogProgress sceneSwitchLogger = LogProgress.create("SCENE_SWITCH", logger);
        sceneSwitchLogger.begin("Switching Scene to {}", newScene.getClass().getSimpleName());

        this.currentGameScene = newScene;

        SubScene worldScene = new SubScene(newScene.getWorldLayer(), this.stage.getWidth(), this.stage.getHeight());
        // bind world scene size to root size
        worldScene.widthProperty().bind(this.sceneRoot.widthProperty());
        worldScene.heightProperty().bind(this.sceneRoot.heightProperty());

        // add to root so `this.scene` is updated
        this.sceneRoot.getChildren().setAll(worldScene, newScene.getUILayer());

        sceneSwitchLogger.end("Switched to Scene {} successfully", newScene.getClass().getSimpleName());

        return this;
    }

    public GameWorld setScene(Supplier<GameScene> newScene) { return setScene(newScene.get()); }

    /// Get the current active [GameScene]
    public GameScene getScene() { return currentGameScene; }

    /// Only used in [edu.nust.engine.core] for internal purposes.
    /// <br>
    /// <br>
    /// Used for adding stylesheets, events, etc.
    protected Scene getRawScene() { return scene; }

    /* UTILITIES */

    public Vector2D getSize() { return new Vector2D(stage.getWidth(), stage.getHeight()); }

    public double getWidth() { return stage.getWidth(); }

    public double getHeight() { return stage.getHeight(); }

    public String getWindowTitle() { return stage.getTitle(); }

    public Cursor getCursor() { return stage.getScene().getCursor(); }

    public boolean isCursorVisible() { return getCursor() != Cursor.NONE; }

    public boolean isFullscreen() { return stage.isFullScreen(); }

    public GameWorld setSize(double width, double height)
    {
        stage.setWidth(width);
        stage.setHeight(height);
        return this;
    }

    public GameWorld setWidth(double width)
    {
        stage.setWidth(width);
        return this;
    }

    public GameWorld setHeight(double height)
    {
        stage.setHeight(height);
        return this;
    }

    public GameWorld setFullscreen(boolean fullscreen)
    {
        stage.setFullScreen(fullscreen);
        return this;
    }

    public GameWorld toggleFullscreen()
    {
        setFullscreen(!stage.isFullScreen());
        return this;
    }

    public GameWorld setWindowTitle(String title)
    {
        stage.setTitle(title);
        return this;
    }

    public GameWorld setCursor(Cursor cursor)
    {
        stage.getScene().setCursor(cursor);
        return this;
    }

    public GameWorld setCursorVisible(boolean visible)
    {
        if (!visible) setCursor(Cursor.NONE);
        return this;
    }

    public GameWorld toggleCursorVisible()
    {
        setCursorVisible(!isCursorVisible());
        return this;
    }

    public GameWorld centerWindow()
    {
        stage.centerOnScreen();
        return this;
    }

    public GameWorld setResizable(boolean resizable)
    {
        stage.setResizable(resizable);
        return this;
    }
}
