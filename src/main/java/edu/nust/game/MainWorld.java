package edu.nust.game;

import edu.nust.engine.core.GameWorld;
import javafx.stage.Stage;

public class MainWorld extends GameWorld
{
    public MainWorld(Stage stage)
    {
        super(stage);
    }

    @Override
    protected void initStage()
    {
        stage.setTitle("Test World");
        stage.setWidth(300);
        stage.setHeight(200);
        stage.centerOnScreen();
    }
}
