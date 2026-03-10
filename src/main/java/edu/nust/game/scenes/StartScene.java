package edu.nust.game.scenes;

import edu.nust.engine.core.GameWindow;
import edu.nust.game.gameobjects.MovingObject;
import javafx.fxml.FXML;

public class StartScene extends edu.nust.engine.core.GameScene
{
    public StartScene(GameWindow world)
    {
        super(world);
    }

    /* LIFETIME */

    @Override
    protected void onStart()
    {
        this.addGameObject(MovingObject::new);
    }

    @Override
    protected void onUpdate(double deltaTime)
    {

    }

    /* FXML Buttons Callbacks */

    @FXML
    private void switchToGameScene()
    {
        this.getWindow().setCurrentGameScene(new MainGameScene(this.getWindow()));
    }

    @FXML
    private void exitApplication()
    {
        this.getWindow().stop();
    }
}
