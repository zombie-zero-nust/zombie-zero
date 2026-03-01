package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SceneA extends GameScene
{
    private int clickedTimes = 0;

    /* FXML Elements */

    @FXML private Label messageLabel;

    /* CONSTRUCTOR */

    public SceneA(GameWorld world)
    {
        super(world);
    }

    @Override
    public String getName()
    {
        return "Scene A";
    }

    /* LIFETIME */

    @Override
    protected void onStart()
    {

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
        this.getWorld().setCurrentGameScene(new SceneB(this.getWorld()));
    }
}
