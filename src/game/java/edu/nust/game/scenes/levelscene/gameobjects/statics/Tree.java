package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

public class Tree extends StaticObject
{
    @Override
    protected int renderLayer() { return 2; }

    @Override
    protected int numImages() { return 4; }

    @Override
    protected String folderName() { return "trees"; }

    @Override
    protected String filename(int index) { return "tree_" + index + ".png"; }
}
