package edu.nust.engine.core;

import edu.nust.Main;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

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
    protected final Scene scene;

    private GameScene currentGameScene;

    private final AnimationTimer gameLoop;
    private boolean updatesPaused = false;

    public GameWindow(Stage stage)
    {
        this.stage = stage;
        this.scene = new Scene(new StackPane());
        this.stage.setScene(this.scene);

        URL commonCssUrl = Main.class.getResource("/edu/nust/game/scenes/common.css");
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
                if (lastTime == 0)
                {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                if (currentGameScene != null)
                {
                    if (!updatesPaused) currentGameScene.onUpdate(deltaTime);
                    if (!updatesPaused) currentGameScene.gameObjects.forEach(GameObject::onUpdate);

                    if (currentGameScene.hasCanvas())
                    {
                        GraphicsContext context = initializeContext();
                        currentGameScene.gameObjects.forEach(obj -> obj.onRender(context));
                    }
                }
            }

            private @NotNull GraphicsContext initializeContext()
            {
                Canvas canvas = currentGameScene.getCanvas();
                assert canvas != null; // checked by hasCanvas()

                // canvas config
                canvas.setFocusTraversable(true);
                canvas.setWidth(scene.getWidth());
                canvas.setHeight(scene.getHeight());

                GraphicsContext context = canvas.getGraphicsContext2D();

                context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                context.setFill(Color.BLACK);
                context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                return context;
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

        if (scene.hasCanvas())
        {
            Canvas canvas = scene.getCanvas();
            assert canvas != null; // checked by hasCanvas()
            canvas.requestFocus();
        }

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
