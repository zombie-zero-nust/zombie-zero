package edu.nust.engine.core;

import javafx.stage.Stage;

public abstract class GameWorld
{
    private GameScene currentScene;

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

    /* HELPERS */

    protected abstract void initStage();

    /* GETTERS AND SETTERS */

    public GameScene getCurrentScene()
    {
        return currentScene;
    }

    public void setCurrentScene(GameScene scene)
    {
        stage.setScene(scene.getScene());
        this.currentScene = scene;
    }
}
