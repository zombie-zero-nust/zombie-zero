package edu.nust.game.scenes.levelscene.gameobjects.statics;

import java.util.Random;

public class RandomGrass extends StaticObject
{
    public static RandomGrass at(double x, double y)
    {
        return (RandomGrass) new RandomGrass().addTag(StaticTag.class).getTransform().setPosition(x, y).getGameObject();
    }

    @Override
    public Random random() { return new Random(20); }

    @Override
    protected int numImages() { return 25; }

    @Override
    protected String folderName() { return "grass"; }

    @Override
    protected String filename(int index) { return "grass_" + index + ".png"; }
}