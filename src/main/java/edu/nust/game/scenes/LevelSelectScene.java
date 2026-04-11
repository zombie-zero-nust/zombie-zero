package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.PlayerSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LevelSelectScene extends GameScene
{
    @FXML private TextField playerNameField;
    @FXML private Label nameErrorLabel;

    public LevelSelectScene(GameWorld world)
    {
        super(world);
    }

    @FXML
    private void startLevel1()
    {
        String playerName = playerNameField != null ? playerNameField.getText().trim() : "";
        if (playerName.isEmpty())
        {
            if (nameErrorLabel != null)
                nameErrorLabel.setText("Please enter your name first.");
            return;
        }

        if (playerName.contains(","))
        {
            if (nameErrorLabel != null)
                nameErrorLabel.setText("Name cannot contain commas.");
            return;
        }

        PlayerSession.setPlayerName(playerName);
        if (nameErrorLabel != null)
            nameErrorLabel.setText("");
        this.getWorld().setScene(new LevelScene(this.getWorld(), LevelId.LEVEL_1));
    }

    @FXML
    private void backToMainMenu()
    {
        this.getWorld().setScene(new StartScene(this.getWorld()));
    }
}

