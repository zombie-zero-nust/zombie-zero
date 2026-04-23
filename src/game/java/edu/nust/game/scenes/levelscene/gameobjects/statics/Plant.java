package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

public class Plant extends StaticObject
{
    @Override
    protected int numImages() { return 43; }

    @Override
    protected String folderName() { return "plants"; }

    @Override
    protected String filename(int index) { return "plant_" + index + ".png"; }
}