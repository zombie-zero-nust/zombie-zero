package edu.nust.game;

import edu.nust.engine.core.GameWindow;
import javafx.stage.Stage;

public class MainWindow extends GameWindow
{
    public MainWindow(Stage stage)
    {
        super(stage);
    }

    @Override
    protected void initStage()
    {
        stage.setTitle("Test World");
        stage.setWidth(1280);
        stage.setHeight(768);
        stage.centerOnScreen();
    }
}
