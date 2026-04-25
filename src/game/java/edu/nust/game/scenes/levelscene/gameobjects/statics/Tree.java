package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.components.SeeThroughComponent;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;

import java.util.Random;

public class Tree extends StaticObject
{
    private final Player player;

    public Tree(int variant, Player player)
    {
        super(variant);
        this.player = player;
    }

    public Tree(Random random, Player player)
    {
        super(random);
        this.player = player;
    }

    @Override
    public void onInit()
    {
        this.addComponent(new SeeThroughComponent().setPlayer(player));
    }

    @Override
    protected int renderLayer() { return 2; }

    @Override
    protected int numImages() { return 2; }

    @Override
    protected String folderName() { return "trees"; }

    @Override
    protected String filename(int index) { return "tree_" + index + ".png"; }
}
