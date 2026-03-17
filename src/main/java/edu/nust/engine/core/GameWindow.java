package edu.nust.engine.core;

import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.resources.Resources;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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

    private GameScene currentGameScene;

    private final AnimationTimer gameLoop;
    private boolean updatesPaused = false;

    public GameWindow(Stage stage)
    {
        this.stage = stage;
        this.scene = new Scene(new StackPane());
        this.stage.setScene(this.scene);

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

    public void start()
    {
        stage.show();
        gameLoop.start();
    }

    public void stop()
    {
        gameLoop.stop();
        stage.close();
    }

    /* ABSTRACT */

    protected abstract void initStage();

    /* HELPERS */

    public void setWindowTitle(String title)
    {
        stage.setTitle(title);
    }

    /* GETTERS AND SETTERS */

    public GameScene getCurrentGameScene()
    {
        return currentGameScene;
    }

    public void setCurrentGameScene(GameScene scene)
    {
        this.currentGameScene = scene;
        this.scene.setRoot(scene.getRoot());

        Canvas canvas = scene.getCanvas();
        canvas.requestFocus();

        this.setUpdatesPaused(false);
    }

    /* PAUSE */

    public void setUpdatesPaused(boolean state)
    {
        updatesPaused = state;
    }

    public boolean isUpdatesPaused()
    {
        return updatesPaused;
    }
}
