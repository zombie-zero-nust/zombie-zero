package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

public final class Level1BackgroundLoader
{
    private static final String[] LEVEL_1_IMAGE_PATH = {"assets", "images", "Level1BackGround.png"};

    private Level1BackgroundLoader() { }

    public static Image loadOrThrow() throws FileNotFoundException
    {
        return Resources.loadImageOrThrow(LEVEL_1_IMAGE_PATH);
    }
}

