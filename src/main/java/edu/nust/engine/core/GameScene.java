package edu.nust.engine.core;

import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

public abstract class GameScene
{
    protected final List<GameObject> gameObjects = new ArrayList<GameObject>();
    protected Parent root;

    public GameScene()
    {
        initScene();
        if (root == null)
        {
            throw new RuntimeException("Root not initialized");
        }
    }

    /* CHILD METHODS */

    protected void initScene()
    {
        throw new RuntimeException("initScene() not implemented");
    }

    /* GETTERS AND SETTERS */

    public Parent getRoot()
    {
        return root;
    }

    /* ABSTRACT METHODS */

    public abstract String getName();

    /**
     * <b>TODO:</b> Implement
     * <br>
     * Will be called each frame
     **/
    void update()
    {
    }
}
