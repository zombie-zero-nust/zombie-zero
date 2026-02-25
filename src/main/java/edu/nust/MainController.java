package edu.nust;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController
{
    @FXML
    private Label messageLabel;

    private static int clickedTimes = 0;

    @FXML
    private void handleClick()
    {
        messageLabel.setText("Button clicked " + ++clickedTimes + " times.");
    }
}