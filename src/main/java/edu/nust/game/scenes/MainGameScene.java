package edu.nust.game.scenes;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWindow;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.core.components.renderers.CircleRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.gameobjects.MovingObject;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import javax.sound.sampled.BooleanControl;

public class MainGameScene extends GameScene
{
    @FXML private StackPane pauseOverlay;

    private MovingObject tracked;

    private boolean isPaused = false;

    public MainGameScene(GameWindow world)
    {
        super(world);
    }

    @Override
    protected void onStart()
    {
        tracked = (MovingObject) this.addGameObject(new MovingObject(
                new Vector2D(100, 100),
                new Vector2D(200, 100),
                TimeSpan.fromSeconds(2),
                Color.AQUA
        ));
        tracked.getTransform().setPosition(new Vector2D(400, 400));

        this.addGameObject(new MovingObject(
                new Vector2D(300, 300),
                new Vector2D(300, 400),
                TimeSpan.fromSeconds(1),
                Color.ORANGE
        ));

        this.addGameObject(new GameObject()
        {
            @Override
            protected void onInit()
            {
                super.onInit();
                this.addComponent(new BoxRenderer(100, 100, Color.GREEN));
            }
        }).getTransform().setPosition(new Vector2D(0, 0));
    }

    @Override
    protected void onUpdate(TimeSpan deltaTime)
    {
        if (isPaused) return; // skip game updates if paused

        double posX = tracked.getTransform().getPosition().getX();
        double posY = tracked.getTransform().getPosition().getY();

        this.getWorldCamera().setTranslateX(posX - this.getWindow().getWidth() / 2);
        this.getWorldCamera().setTranslateY(posY - this.getWindow().getHeight() / 2);
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
        this.getWindow().setScene(new StartScene(this.getWindow()));
    }
}