package edu.nust.game.scenes.levelselect;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.scenes.levelscene.LevelID;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.systems.PlayerSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

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
        this.getWorld().setScene(new LevelScene(this.getWorld(), LevelID.LEVEL_1));
    }

    @FXML
    private void startLevel1TileClicked(MouseEvent ignored)
    {
        startLevel1();
    }
}
