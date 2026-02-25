package edu.nust;

import edu.nust.engine.core.GameWorld;
import edu.nust.game.MainWorld;
import edu.nust.game.scenes.TestScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        GameWorld world = new MainWorld(stage);
        world.setCurrentScene(new TestScene());
        world.showStage();
    }

    public static void main(String[] args)
    {
        launch();
    }
}