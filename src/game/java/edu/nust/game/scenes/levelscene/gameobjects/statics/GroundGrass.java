package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class GroundGrass extends StaticObject
{
    public GroundGrass(int variant) { super(variant); }

    public GroundGrass(Random random) { super(random); }

    @Override
    protected int numImages() { return 13; }

    @Override
    protected String folderName() { return "ground_grass"; }

    @Override
    protected String filename(int index) { return "grass_" + index + ".png"; }
}