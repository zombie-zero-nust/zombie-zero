package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.gameobjects.MovingObject;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;

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
        this.addGameObject(new MovingObject(
                new Vector2D(100, 100),
                new Vector2D(200, 200),
                TimeSpan.fromSeconds(1),
                Color.RED
        ));
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {

    }

    /* FXML Buttons Callbacks */

    @FXML
    private void switchToGameScene()
    {
        this.getWindow().setScene(new MainGameScene(this.getWindow()));
    }

    @FXML
    private void switchToLevelScene()
    {
        this.getWindow().setScene(new LevelScene(this.getWindow()));
    }

    @FXML
    private void exitApplication()
    {
        this.getWindow().stop();
    }
}
