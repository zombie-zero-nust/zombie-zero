package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class Plant extends StaticObject
{
    public Plant(int variant) { super(variant); }

    public Plant(Random random) { super(random); }

    @Override
    protected int numImages() { return 40; }

    @Override
    protected String folderName() { return "plants"; }

    @Override
    protected String filename(int index) { return "plant_" + index + ".png"; }
}