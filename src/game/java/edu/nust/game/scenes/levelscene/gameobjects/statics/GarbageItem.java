package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

public class GarbageItem extends StaticObject
{
    @Override
    protected int numImages() { return 15; }

    @Override
    protected String folderName() { return "garbage"; }

    @Override
    protected String filename(int index) { return "object_" + index + ".png"; }
}