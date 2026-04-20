package edu.nust.game.scenes;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.engine.math.TimeSpan;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class StartScene extends GameScene
{
    public StartScene(GameWorld world)
    {
        super(world);
    }

    @Override
    public void onInit()
    {
        // Background is now driven by StartScene CSS.
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
    }

    @FXML
    private void switchToGameScene()
    {
        this.getWorld().setScene(new MainGameScene(this.getWorld()));
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
