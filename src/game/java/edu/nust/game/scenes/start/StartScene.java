package edu.nust.game.scenes.start;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.scenes.demo.DemoScene;
import edu.nust.game.scenes.highscores.HighscoresScene;
import edu.nust.game.scenes.levelselect.LevelSelectScene;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class StartScene extends GameScene
{
    public StartScene(GameWorld world) { super(world); }

    @FXML
    private void switchToGameScene()
    {
        this.getWorld().setScene(new DemoScene(this.getWorld()));
    }

    @FXML
    private void switchToLevelScene()
    {
        this.getWorld().setScene(new LevelSelectScene(this.getWorld()));
    }

    @FXML
    private void switchToHighscoresScene()
    {
        this.getWorld().setScene(new HighscoresScene(this.getWorld()));
    }

    @FXML
    private void exitApplication()
    {
        this.getWorld().stop();
    }

    @FXML
    private void switchToGameSceneTileClicked(MouseEvent ignored)
    {
        switchToGameScene();
    }

    @FXML
    private void switchToLevelSceneTileClicked(MouseEvent ignored)
    {
        switchToLevelScene();
    }

    @FXML
    private void switchToHighscoresSceneTileClicked(MouseEvent ignored)
    {
        switchToHighscoresScene();
    }

    @FXML
    private void exitApplicationTileClicked(MouseEvent ignored)
    {
        exitApplication();
    }
}
