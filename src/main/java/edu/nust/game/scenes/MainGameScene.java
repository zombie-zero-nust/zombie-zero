package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWindow;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.gameobjects.MovingObject;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class MainGameScene extends GameScene
{
    @FXML private StackPane pauseOverlay;

    private boolean isPaused = false;

    public MainGameScene(GameWindow world)
    {
        super(world);
    }

    @Override
    protected void onStart()
    {
        this.addGameObject(MovingObject::new).getTransform().setPosition(new Vector2D(400, 400));
    }

    @Override
    protected void onUpdate(double deltaTime)
    {
        if (isPaused) return; // skip game updates if paused


    }

    @Override
    protected void onKeyPressed(KeyEvent event)
    {
        if (event.getCode() == KeyCode.ESCAPE)
        {
            setPaused(!isPaused);
        }
    }

    private void setPaused(boolean newState)
    {
        this.isPaused = newState;
        pauseOverlay.setVisible(newState);
        pauseOverlay.setManaged(newState);
        this.getWindow().setUpdatesPaused(newState);
    }

    /* FXML Button Callbacks */

    @FXML
    private void resumeGame()
    {
        setPaused(false);
    }

    @FXML
    private void exitToMainMenu()
    {
        this.getWindow().setCurrentGameScene(new StartScene(this.getWindow()));
    }
}