package edu.nust.game.scenes;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.components.renderers.BoxRenderer;
import edu.nust.engine.core.components.renderers.CircleRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.gameobjects.MovingObject;
import edu.nust.game.gameobjects.MovingTag;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MainGameScene extends GameScene
{
    @FXML private StackPane pauseOverlay;

    private boolean isPaused = false;

    public MainGameScene(GameWorld world)
    {
        super(world);
    }

    @Override
    protected void onStart()
    {
        this.addGameObject(new MovingObject(
                        new Vector2D(100, 100),
                        new Vector2D(200, 100),
                        TimeSpan.fromSeconds(2),
                        Color.AQUA
                ))
                .addTag(MovingTag.class);

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
                this.addComponent(new CircleRenderer(100, Color.GREEN));
            }

            @Override
            protected void onUpdate(TimeSpan deltaTime)
            {

            }

            @Override
            protected void onRender(GraphicsContext context)
            {

            }
        }).getTransform().setPosition(new Vector2D(0, 0));
    }

    @Override
    protected void onUpdate(TimeSpan deltaTime)
    {
        if (isPaused) return; // skip game updates if paused

        GameObject trackedObject = this.getFirstWithTag(MovingTag.class);
        if (trackedObject == null) return;

        double posX = trackedObject.getTransform().getPosition().getX();
        double posY = trackedObject.getTransform().getPosition().getY();

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
        this.setActive(!newState);
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