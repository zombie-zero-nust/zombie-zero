package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class Grass extends StaticObject
{
    public Grass(int variant) { super(variant); }

    public Grass(Random random) { super(random); }

    @Override
    protected int numImages() { return 15; }

    @Override
    protected String folderName() { return "grass"; }

    @Override
    protected String filename(int index) { return "grass_" + index + ".png"; }

    @Override
    protected boolean rotateRandom() { return true; }
}