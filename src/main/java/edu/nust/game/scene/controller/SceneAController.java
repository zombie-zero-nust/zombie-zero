package edu.nust.game.scene.controller;

import edu.nust.game.MainWorld;
import edu.nust.game.scene.SceneB;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SceneAController
{
    @FXML
    private Label messageLabel;

    private static int clickedTimes = 0;

    @FXML
    private void handleClick()
    {
        messageLabel.setText("Button clicked " + ++clickedTimes + " times.");
    }

    @FXML
    private void switchToSceneB()
    {
        MainWorld world = MainWorld.getInstance();
        if (world == null) return;

        world.setCurrentGameScene(new SceneB());
    }
}