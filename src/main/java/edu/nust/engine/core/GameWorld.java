package edu.nust.engine.core;

import edu.nust.Main;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;

public abstract class GameWorld
{
    protected final Stage stage;
    /// When changing "scenes", we just change root
    protected final Scene scene;

    private GameScene currentGameScene;

    public GameWorld(Stage stage)
    {
        this.stage = stage;
        this.scene = new Scene(new StackPane());
        this.stage.setScene(this.scene);

        URL commonCssUrl = Main.class.getResource("/edu/nust/game/scene/common.css");
        if (commonCssUrl != null)
        {
            this.scene.getStylesheets().add(commonCssUrl.toExternalForm());
        }

        initStage();
    }

    public void start()
    {
        stage.show();
    }

    /* HELPERS */

    protected abstract void initStage();

    /* GETTERS AND SETTERS */

    public GameScene getCurrentGameScene()
    {
        return currentGameScene;
    }

    public void setCurrentGameScene(GameScene scene)
    {
        this.currentGameScene = scene;
        this.scene.setRoot(scene.getRoot());
        this.stage.setTitle(scene.getName());
    }
}
