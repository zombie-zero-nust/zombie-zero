package edu.nust;

import edu.nust.game.MainWorld;
import edu.nust.game.scene.SceneA;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage stage)
    {
        MainWorld world = new MainWorld(stage);
        world.setCurrentGameScene(new SceneA());
        world.start();
    }

    public static void main(String[] args)
    {
        launch();
    }
}