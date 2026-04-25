package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class Garbage extends StaticObject
{
    public Garbage(int variant) { super(variant); }

    public Garbage(Random random) { super(random); }

    @Override
    protected int numImages() { return 17; }

    @Override
    protected String folderName() { return "garbage"; }

    @Override
    protected String filename(int index) { return "item_" + index + ".png"; }

    @Override
    protected boolean rotateRandom() { return true; }
}