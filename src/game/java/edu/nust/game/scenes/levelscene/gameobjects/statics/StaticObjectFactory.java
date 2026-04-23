package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.engine.core.GameObject;
import edu.nust.game.scenes.levelscene.components.SeeThroughComponent;
import edu.nust.game.scenes.levelscene.gameobjects._tags.StaticTag;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObjectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class StaticObjectFactory
{
    private static final int PLANT_WEIGHT = 75;
    private static final int TREE_WEIGHT = 0;
    private static final int BUSH_WEIGHT = 10;
    private static final int GARBAGE_WEIGHT = 10;
    private static final int FALLEN_TREE_WEIGHT = 5;

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

    public static GameObject staticAt(double x, double y, @NotNull StaticObjectType type, @Nullable Player player)
    {
        return switch (type)
        {
            case BUSH -> bushAt(x, y);
            case FALLEN_TREE -> fallenTreeAt(x, y);
            case GARBAGE_ITEM -> garbageItemAt(x, y);
            case PLANT -> plantAt(x, y);
            case TREE -> treeAt(x, y, player);
        };
    }

    /* RANDOM */

    public static StaticObjectType randomType(@NotNull Random random)
    {
        final int totalWeight = BUSH_WEIGHT + FALLEN_TREE_WEIGHT + GARBAGE_WEIGHT + PLANT_WEIGHT + TREE_WEIGHT;

        int roll = random.nextInt(totalWeight);

        if (roll < BUSH_WEIGHT) { return StaticObjectType.BUSH; }
        roll -= BUSH_WEIGHT;

        if (roll < FALLEN_TREE_WEIGHT) { return StaticObjectType.FALLEN_TREE; }
        roll -= FALLEN_TREE_WEIGHT;

        if (roll < GARBAGE_WEIGHT) { return StaticObjectType.GARBAGE_ITEM; }
        roll -= GARBAGE_WEIGHT;

        if (roll < PLANT_WEIGHT) { return StaticObjectType.PLANT; }

        return StaticObjectType.TREE;
    }

    public static GameObject randomStaticAt(double x, double y, @Nullable Player player, Random random)
    {
        return staticAt(x, y, randomType(random), player);
    }
}