package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import javafx.fxml.FXML;

public class LevelSelectScene extends GameScene
{
    public LevelSelectScene(GameWorld world)
    {
        super(world);
    }

    @FXML
    private void startLevel1()
    {
        this.getWorld().setScene(new LevelScene(this.getWorld(), LevelId.LEVEL_1));
    }

    @FXML
    private void backToMainMenu()
    {
        this.getWorld().setScene(new StartScene(this.getWorld()));
    }
}

