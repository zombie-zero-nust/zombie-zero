package edu.nust.game.scenes;

import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;

final class Level1BackgroundLoader
{
    private static final String[] LEVEL_1_IMAGE_PATH = {"assets", "images", "Level1BackGround.jpeg"};

    private Level1BackgroundLoader() { }

    static Image loadOrThrow() throws FileNotFoundException
    {
        return Resources.loadImageOrThrow(LEVEL_1_IMAGE_PATH);
    }
}

