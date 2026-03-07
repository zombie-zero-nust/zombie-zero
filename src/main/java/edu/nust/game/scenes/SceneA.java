package edu.nust.game.scenes;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWindow;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SceneA extends GameScene
{
    private int clickedTimes = 0;

    /* FXML Elements */

    @FXML private Label messageLabel;

    /* CONSTRUCTOR */

    public SceneA(GameWindow world)
    {
        super(world);
    }

    /* LIFETIME */

    @Override
    protected void onStart()
    {
        this.addGameObject(new GameObject(this));
    }

    @Override
    protected void onUpdate(double deltaTime)
    {

    }

    /* FXML Buttons Callbacks */

    @FXML
    private void handleClick()
    {
        messageLabel.setText("Button clicked " + ++clickedTimes + " times.");
    }

    @FXML
    private void switchToSceneB()
    {
        this.getWindow().setCurrentGameScene(new SceneB(this.getWindow()));
    }
}
