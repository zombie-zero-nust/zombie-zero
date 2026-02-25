package edu.nust.engine.core;

import javafx.stage.Stage;

public abstract class GameWorld
{
    private GameScene currentGameScene;
    protected final Stage stage;

    public GameWorld(Stage stage)
    {
        this.stage = stage;
        initStage();
    }

    public void showStage()
    {
        stage.show();
    }

    /* SINGLETON */

    /* HELPERS */

    protected abstract void initStage();

    /* GETTERS AND SETTERS */

    public GameScene getCurrentGameScene()
    {
        return currentGameScene;
    }

    public void setCurrentGameScene(GameScene scene)
    {
        stage.setScene(scene.getScene());
        this.currentGameScene = scene;
    }
}
