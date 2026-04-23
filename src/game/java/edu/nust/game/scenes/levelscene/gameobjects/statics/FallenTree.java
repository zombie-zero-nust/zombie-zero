package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

public class FallenTree extends StaticObject
{
    @Override
    protected int numImages() { return 3; }

    @Override
    protected String folderName() { return "fallen_trees"; }

    @Override
    protected String filename(int index) { return "tree_" + index + ".png"; }
}