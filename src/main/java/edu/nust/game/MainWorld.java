package edu.nust.game;

import edu.nust.engine.core.GameWorld;
import javafx.stage.Stage;
import org.jetbrains.annotations.Nullable;

public class MainWorld extends GameWorld
{
    /* SINGLETON START */
    private static @Nullable MainWorld instance;

    public static @Nullable MainWorld getInstance()
    {
        return instance;
    }

    /* SINGLETON  END  */

    public MainWorld(Stage stage)
    {
        super(stage);
        instance = this;
    }

    @Override
    protected void initStage()
    {
        stage.setTitle("Test World");
        stage.setWidth(300);
        stage.setHeight(300);
        stage.centerOnScreen();
    }
}
