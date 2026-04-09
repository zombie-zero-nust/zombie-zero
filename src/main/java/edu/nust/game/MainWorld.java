package edu.nust.game;

import edu.nust.engine.core.GameWorld;
import edu.nust.game.audio.Audios;
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
        stage.setWidth(1280);
        stage.setHeight(768);
        stage.centerOnScreen();
    }

    @Override
    protected void loadAudios()
    {
        Audios.forEach(this::loadAudioClip);
    }
}
