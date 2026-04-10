package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.components.renderers.SpriteRenderer;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.engine.resources.Resources;
import edu.nust.game.gameobjects.MovingObject;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileNotFoundException;

public class StartScene extends GameScene
{
    public StartScene(GameWorld world)
    {
        super(world);
    }

    /* LIFETIME */

    @Override
    public void onInit()
    {
        try
        {
            // Load and display start scene background
            Image backgroundImage = Resources.loadImageOrThrow(
                    "assets",
                    "images",
                    "background_start.jpeg"
            );

            // Create background GameObject that spans the screen
            GameObject background = GameObject.create();
            SpriteRenderer bgRenderer = new SpriteRenderer(1280, 768, backgroundImage);
            background.addComponent(bgRenderer);
            background.getTransform().setPosition(640, 384);

            this.addGameObject(background);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Failed to load start scene background: " + e.getMessage());
        }
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {

    }

    /* FXML Buttons Callbacks */

    @FXML
    private void switchToGameScene()
    {
        this.getWorld().setScene(new MainGameScene(this.getWorld()));
    }

    @FXML
    private void switchToLevelScene()
    {
        this.getWorld().setScene(new LevelScene(this.getWorld()));
    }

    @FXML
    private void exitApplication()
    {
        this.getWorld().stop();
    }
}
