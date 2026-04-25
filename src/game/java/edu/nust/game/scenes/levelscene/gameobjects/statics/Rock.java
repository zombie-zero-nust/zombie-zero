package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class Rock extends StaticObject
{
    public Rock(int variant) { super(variant); }

    public Rock(Random random) { super(random); }

    @Override
    protected int numImages() { return 8; }

    @Override
    protected String folderName() { return "rocks"; }

    @Override
    protected String filename(int index) { return "rock_" + index + ".png"; }

    @Override
    protected boolean rotateRandom() { return true; }
}