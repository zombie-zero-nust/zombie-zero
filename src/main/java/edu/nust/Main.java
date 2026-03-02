package edu.nust;

import edu.nust.game.MainWindow;
import edu.nust.game.scenes.SceneA;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application
{
    @Override
    public void start(Stage stage)
    {
        MainWindow window = new MainWindow(stage);
        window.setCurrentGameScene(new SceneA(window));
        window.start();
    }

    public static void main(String[] args)
    {
        launch();
    }
}