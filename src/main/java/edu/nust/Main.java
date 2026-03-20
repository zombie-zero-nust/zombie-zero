package edu.nust;

import edu.nust.game.MainWindow;
import edu.nust.game.scenes.StartScene;
import javafx.application.Application;
import javafx.stage.Stage;

// ------------------------------------------------------------------------- //
//                                DO NOT TOUCH                               //
// ------------------------------------------------------------------------- //

public class Main extends Application
{
    @Override
    public void start(Stage stage)
    {
        MainWindow window = new MainWindow(stage);
        window.setScene(new StartScene(window));
        window.start();
    }

    public static void main(String[] args) { launch(); }
}