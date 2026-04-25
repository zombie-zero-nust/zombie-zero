package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class Bush extends StaticObject
{
    public Bush(int variant) { super(variant); }

    public Bush(Random random) { super(random); }

    @Override
    protected int numImages() { return 9; }

    @Override
    protected String folderName() { return "bushes"; }

    @Override
    protected String filename(int index) { return "bush_" + index + ".png"; }

    @Override
    protected boolean rotateRandom() { return true; }
}