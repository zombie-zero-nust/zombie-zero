package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class Stick extends StaticObject
{
    public Stick(int variant) { super(variant); }

    public Stick(Random random) { super(random); }

    @Override
    protected int numImages() { return 2; }

    @Override
    protected String folderName() { return "sticks"; }

    @Override
    protected String filename(int index) { return "stick_" + index + ".png"; }

    @Override
    protected boolean rotateRandom() { return true; }
}