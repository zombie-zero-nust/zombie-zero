package edu.nust.game.scene.controller;

import edu.nust.engine.core.GameWorld;
import edu.nust.game.scene.SceneA;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SceneBController
{
    private GameWorld world;

    private int clickedTimes = 0;

    public void setWorld(GameWorld world)
    {
        this.world = world;
    }

    /* FXML Elements */

    @FXML
    private Label messageLabel;

    /* FXML Buttons */

    @FXML
    private void handleClick()
    {
        messageLabel.setText("Button clicked " + ++clickedTimes + " times.");
    }

    @FXML
    private void switchToSceneA()
    {
        this.world.setCurrentGameScene(new SceneA(this.world));
    }
}