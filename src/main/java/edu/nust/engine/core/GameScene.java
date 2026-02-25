package edu.nust.engine.core;

import javafx.scene.Scene;

import java.util.ArrayList;
import java.util.List;

public abstract class GameScene
{
    protected final List<GameObject> gameObjects = new ArrayList<GameObject>();
    protected Scene scene;

    public GameScene()
    {
        initScene();
        if (scene == null)
        {
            throw new RuntimeException("Scene not initialized");
        }
    }

    /* CHILD METHODS */

    protected void initScene()
    {
        throw new RuntimeException("initScene() not implemented");
    }

    /* GETTERS AND SETTERS */

    public Scene getScene()
    {
        return scene;
    }

    /* ABSTRACT METHODS */

    /**
     * <b>TODO:</b> Implement
     * <br>
     * Will be called each frame
     **/
    void update()
    {
    }
}
