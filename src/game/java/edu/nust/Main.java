package edu.nust;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.logger.enums.LogLevel;
import edu.nust.game.MainWorld;
import edu.nust.game.scenes.start.StartScene;
import edu.nust.game.systems.audio.Audios;
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
        MainWorld world = new MainWorld(stage);
        world.setScene(new StartScene(world));
        world.start();

        Audios.setManager(world);
    }

    public static void main(String[] ignored)
    {
        initLogger();
        launch();
    }

    private static void initLogger() { GameLogger.setGlobalLevel(LogLevel.INFO); }
}