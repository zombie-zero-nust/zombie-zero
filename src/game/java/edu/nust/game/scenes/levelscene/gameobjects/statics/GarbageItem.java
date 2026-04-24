package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class GarbageItem extends StaticObject
{
    public GarbageItem(int variant) { super(variant); }

    public GarbageItem(Random random) { super(random); }

    @Override
    protected int numImages() { return 15; }

    @Override
    protected String folderName() { return "garbage"; }

    @Override
    protected String filename(int index) { return "object_" + index + ".png"; }

    @Override
    protected boolean rotateRandom() { return true; }
}