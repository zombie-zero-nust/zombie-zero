package edu.nust.game.scene.controller;

import edu.nust.game.MainWorld;
import edu.nust.game.scene.SceneA;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SceneBController
{
    @FXML
    private Label messageLabel;

    private int clickedTimes = 0;

    @FXML
    private void handleClick()
    {
        messageLabel.setText("Button clicked " + ++clickedTimes + " times.");
    }

    @FXML
    private void switchToSceneA()
    {
        MainWorld world = MainWorld.getInstance();
        if (world == null) return;

        world.setCurrentGameScene(new SceneA());
    }
}