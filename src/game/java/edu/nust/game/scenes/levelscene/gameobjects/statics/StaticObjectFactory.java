package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.engine.core.GameObject;
import edu.nust.game.scenes.levelscene.components.SeeThroughComponent;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects._tags.StaticTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class StaticObjectFactory
{
    public static Bush bushAt(double x, double y)
    {
        return (Bush) new Bush().addTag(StaticTag.class).getTransform().setPosition(x, y).getGameObject();
    }

    public static Bush bushAt(double x, double y, @NotNull Random random)
    {
        return (Bush) bushAt(x, y).setRandom(random);
    }

    public static FallenTree fallenTreeAt(double x, double y)
    {
        return (FallenTree) new FallenTree().addTag(StaticTag.class).getTransform().setPosition(x, y).getGameObject();
    }

    public static FallenTree fallenTreeAt(double x, double y, @NotNull Random random)
    {
        return (FallenTree) fallenTreeAt(x, y).setRandom(random);
    }

    public static GarbageItem garbageItemAt(double x, double y)
    {
        return (GarbageItem) new GarbageItem().addTag(StaticTag.class).getTransform().setPosition(x, y).getGameObject();
    }

    public static GarbageItem garbageItemAt(double x, double y, @NotNull Random random)
    {
        return (GarbageItem) garbageItemAt(x, y).setRandom(random);
    }

    public static Plant plantAt(double x, double y)
    {
        return (Plant) new Plant().addTag(StaticTag.class).getTransform().setPosition(x, y).getGameObject();
    }

    public static Plant plantAt(double x, double y, @NotNull Random random)
    {
        return (Plant) plantAt(x, y).setRandom(random);
    }

    public static Tree treeAt(double x, double y, @Nullable Player player)
    {
        return (Tree) new Tree().addTag(StaticTag.class)
                .getTransform()
                .setPosition(x, y)
                .getGameObject()
                .getFirstOrAddComponent(new SeeThroughComponent().setPlayer(player))
                .getGameObject();
    }

    public static Tree treeAt(double x, double y, @Nullable Player player, @NotNull Random random)
    {
        return (Tree) treeAt(x, y, player).setRandom(random);
    }

    public static GameObject randomStaticAt(double x, double y, @Nullable Player player, Random random)
    {
        // weights
        final int plantWeight = 75;
        final int treeWeight = 0;
        final int bushWeight = 10;
        final int garbageWeight = 10;
        final int fallenTreeWeight = 5;

        final int totalWeight = bushWeight + fallenTreeWeight + garbageWeight + plantWeight + treeWeight;

        int roll = random.nextInt(totalWeight);

        if (roll < bushWeight) { return bushAt(x, y, random); }
        roll -= bushWeight;

        if (roll < fallenTreeWeight) { return fallenTreeAt(x, y, random); }
        roll -= fallenTreeWeight;

        if (roll < garbageWeight) { return garbageItemAt(x, y, random); }
        roll -= garbageWeight;

        if (roll < plantWeight) { return plantAt(x, y, random); }

        return treeAt(x, y, player, random);
    }
}