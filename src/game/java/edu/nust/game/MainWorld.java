package edu.nust.game;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.resources.Resources;
import edu.nust.game.systems.audio.Audios;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class MainWorld extends GameWorld
{
    public MainWorld(Stage stage) { super(stage); }

    @Override
    protected void initStage()
    {
        stage.setTitle("Test World");
        stage.setWidth(1280);
        stage.setHeight(768);
        stage.centerOnScreen();
        try
        {
            stage.getIcons().add((Resources.loadImageOrThrow("icon.png")));
        }
        catch (FileNotFoundException ignored) { }
    }

    @Override
    protected void loadAudios()
    {
        Audios.forEachSoundEffect(this::loadSoundEffect);
        Audios.forEachMusicTrack(this::loadMusicTrack);
    }
}
