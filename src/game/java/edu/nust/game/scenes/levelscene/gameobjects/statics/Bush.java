package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

public class Bush extends StaticObject
{
    @Override
    protected int numImages() { return 7; }

    @Override
    protected String folderName() { return "bushes"; }

    @Override
    protected String filename(int index) { return "bush_" + index + ".png"; }
}