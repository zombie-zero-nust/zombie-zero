package edu.nust.game.scenes.levelselect;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.start.StartScene;
import edu.nust.game.systems.PlayerSession;
import edu.nust.game.systems.audio.MusicManager;
import javafx.event.ActionEvent;
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

    @Override
    public void onInit()
    {
        if (!MusicManager.isMenuMusicPlaying())
            MusicManager.ensureMenuMusicPlaying();
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
        this.getWorld().setScene(new LevelScene(this.getWorld()));
    }

    @FXML
    private void startLevel1TileClicked(MouseEvent ignored) { startLevel1(); }

    @FXML
    private void goBack(ActionEvent ignored)
    {
        this.getWorld().setScene(new StartScene(this.getWorld()));
    }

    @FXML
    private void goBack(MouseEvent ignored) { this.getWorld().setScene(new StartScene(this.getWorld())); }
}
