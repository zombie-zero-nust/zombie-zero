package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class TreeStump extends StaticObject
{
    public TreeStump(int variant) { super(variant); }

    public TreeStump(Random random) { super(random); }

    @Override
    protected int numImages() { return 2; }

    @Override
    protected String folderName() { return "tree_stumps"; }

    @Override
    protected String filename(int index) { return "stump_" + index + ".png"; }
}