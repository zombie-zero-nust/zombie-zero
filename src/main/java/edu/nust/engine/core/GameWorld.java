package edu.nust.engine.core;

import edu.nust.Main;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

/// The core of the game engine
/// - Manages Window
/// - Manages Application lifetime
/// - Manages the all `GameScene`s
///
/// @see GameScene
public abstract class GameWorld
{
    protected final Stage stage;
    // When changing "scenes", we just change root
    protected final Scene scene;

    private GameScene currentGameScene;

    private final AnimationTimer gameLoop;

    public GameWorld(Stage stage)
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
                    currentGameScene.onUpdate(deltaTime);
                }
            }
        };
    }

    public void start()
    {
        stage.show();
        gameLoop.start();
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
    }
}
