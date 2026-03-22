package edu.nust.game.scenes;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.gameobjects.MovingObject;
import edu.nust.game.gameobjects.MovingTag;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;

public class MainGameScene extends GameScene
{
    @FXML private StackPane pauseOverlay;

    private boolean isPaused = false;

    public MainGameScene(GameWorld world)
    {
        super(world);
    }

    @Override
    public void onStart()
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
            public void onInit()
            {
                try
                {
                    Image image = Resources.loadImageOrThrow("assets", "images", "test.png");
                    SpriteRenderer sprite = new SpriteRenderer(100, 100, image, 2, 2).tintSelf(Color.LIME)
                            .setAnimationTime(TimeSpan.fromMilliseconds(500))
                            .startAnimation();
                    this.addComponent(sprite);
                }
                catch (FileNotFoundException ignored)
                {
                }
            }

            @Override
            public void onUpdate(TimeSpan deltaTime)
            {

            }

            @Override
            public void onRender(GraphicsContext context)
            {

            }
        }).getTransform().setPosition(new Vector2D(0, 0));
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        if (isPaused) return; // skip game updates if paused

        GameObject trackedObject = this.getFirstWithTag(MovingTag.class);
        if (trackedObject == null) return;

        this.getWorldCamera().setPosition(trackedObject.getTransform().getPosition());
    }

    @Override
    public void onKeyPressed(KeyEvent event)
    {
        if (event.getCode() == KeyCode.ESCAPE)
        {
            setPaused(!isPaused);
        }
        // zoom in with W
        else if (event.getCode() == KeyCode.W)
        {
            this.getWorldCamera().incrementZoom(1.1);
        }
        // zoom out with S
        else if (event.getCode() == KeyCode.S)
        {
            this.getWorldCamera().decrementZoom(1.1);
        }
        // show debug grid with G
        else if (event.getCode() == KeyCode.G)
        {
            this.toggleDebugGrid();
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