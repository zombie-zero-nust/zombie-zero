package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class Flower extends StaticObject
{
    public Flower(int variant) { super(variant); }

    public Flower(Random random) { super(random); }

    @Override
    protected int numImages() { return 9; }

    @Override
    protected String folderName() { return "flowers"; }

    @Override
    protected String filename(int index) { return "flower_" + index + ".png"; }
}