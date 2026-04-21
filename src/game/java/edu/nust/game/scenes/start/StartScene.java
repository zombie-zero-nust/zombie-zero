package edu.nust.game.scenes.start;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.core.GameWorld;
import edu.nust.game.scenes.demo.DemoScene;
import edu.nust.game.scenes.highscores.HighScoresScene;
import edu.nust.game.scenes.levelselect.LevelSelectScene;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class StartScene extends GameScene
{
    public StartScene(GameWorld world) { super(world); }

    /* FXML */

    @FXML
    private void switchToGameScene(MouseEvent ignored) { switchScene(new DemoScene(this.getWorld())); }

    @FXML
    private void switchToLevelScene(MouseEvent ignored) { switchScene(new LevelSelectScene(this.getWorld())); }

    @FXML
    private void switchToHighScoresScene(MouseEvent ignored) { switchScene(new HighScoresScene(this.getWorld())); }

    @FXML
    private void exitApplication(MouseEvent ignored) { this.getWorld().stop(); }

    /* HELPERS */

    private void switchScene(GameScene scene) { this.getWorld().setScene(scene); }
}
