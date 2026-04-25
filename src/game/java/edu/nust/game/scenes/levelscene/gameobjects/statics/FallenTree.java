package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class FallenTree extends StaticObject
{
    public FallenTree(int variant) { super(variant); }

    public FallenTree(Random random) { super(random); }

    @Override
    protected int numImages() { return 3; }

    @Override
    protected String folderName() { return "fallen_trees"; }

    @Override
    protected String filename(int index) { return "tree_" + index + ".png"; }

    @Override
    protected boolean rotateRandom() { return true; }
}