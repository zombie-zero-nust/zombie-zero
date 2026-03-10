package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWindow;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.gameobjects.MovingObject;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

public class MainGameScene extends GameScene
{
    @FXML private Canvas drawCanvas;
    @FXML private StackPane pauseOverlay;

    private boolean isPaused = false;

    public MainGameScene(GameWindow world)
    {
        super(world);
    }

    @Override
    protected void onStart()
    {
        this.addGameObject(MovingObject::new).getTransform().setPosition(new Vector2D(0, 0));
    }

    @Override
    protected void onUpdate(double deltaTime)
    {
        if (isPaused)
        {
            // skip game updates if paused
            return;
        }

        // Game rendering logic here using drawCanvas.getGraphicsContext2D()
    }

    @Override
    protected void onKeyPressed(KeyEvent event)
    {
        if (event.getCode() == KeyCode.ESCAPE)
        {
            isPaused = !isPaused;
            pauseOverlay.setVisible(isPaused);
            pauseOverlay.setManaged(isPaused);
        }
    }

    /* FXML Button Callbacks */

    @FXML
    private void backToMainMenu()
    {
        this.getWindow().setCurrentGameScene(new StartScene(this.getWindow()));
    }
}