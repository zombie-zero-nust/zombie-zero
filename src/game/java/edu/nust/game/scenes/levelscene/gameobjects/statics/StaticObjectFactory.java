package edu.nust.game.scenes.levelscene.gameobjects.statics;

import edu.nust.game.scenes.levelscene.gameobjects._tags.StaticTag;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObject;
import edu.nust.game.scenes.levelscene.gameobjects.statics.meta.StaticObjectType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class StaticObjectFactory
{
    private static final int PLANT_WEIGHT = 75;
    private static final int TREE_WEIGHT = 0;
    private static final int BUSH_WEIGHT = 10;
    private static final int GARBAGE_WEIGHT = 10;
    private static final int FALLEN_TREE_WEIGHT = 5;

    // BUSH

    public static Bush bushAt(double x, double y, int variant) { return applyPosAndTag(new Bush(variant), x, y); }

    public static Bush bushAt(double x, double y, Random random) { return applyPosAndTag(new Bush(random), x, y); }

    // FALLEN TREE

    public static FallenTree fallenTreeAt(double x, double y, int variant)
    {
        return applyPosAndTag(new FallenTree(variant), x, y);
    }

    public static FallenTree fallenTreeAt(double x, double y, Random random)
    {
        return applyPosAndTag(new FallenTree(random), x, y);
    }

    // GARBAGE ITEM

    public static GarbageItem garbageItemAt(double x, double y, int variant)
    {
        return applyPosAndTag(new GarbageItem(variant), x, y);
    }

    public static GarbageItem garbageItemAt(double x, double y, Random random)
    {
        return applyPosAndTag(new GarbageItem(random), x, y);
    }

    // PLANT

    public static Plant plantAt(double x, double y, int variant) { return applyPosAndTag(new Plant(variant), x, y); }

    public static Plant plantAt(double x, double y, Random random)
    {
        return applyPosAndTag(new Plant(random), x, y);
    }

    // TREE

    public static Tree treeAt(double x, double y, int variant, @Nullable Player player)
    {
        return applyPosAndTag(new Tree(variant, player), x, y);
    }

    public static Tree treeAt(double x, double y, Random random, @Nullable Player player)
    {
        return applyPosAndTag(new Tree(random, player), x, y);
    }

    public static StaticObject staticAt(double x, double y, int variant, StaticObjectType type, @Nullable Player player)
    {
        return switch (type)
        {
            //@formatter:off
            case BUSH         -> bushAt        (x, y, variant);
            case FALLEN_TREE  -> fallenTreeAt  (x, y, variant);
            case GARBAGE_ITEM -> garbageItemAt (x, y, variant);
            case PLANT        -> plantAt       (x, y, variant);
            case TREE         -> treeAt        (x, y, variant, player);
            //@formatter:on
        };
    }

    public static StaticObject staticAt(double x, double y, Random random, StaticObjectType type, @Nullable Player player)
    {
        return switch (type)
        {
            //@formatter:off
            case BUSH         -> bushAt        (x, y, random);
            case FALLEN_TREE  -> fallenTreeAt  (x, y, random);
            case GARBAGE_ITEM -> garbageItemAt (x, y, random);
            case PLANT        -> plantAt       (x, y, random);
            case TREE         -> treeAt        (x, y, random, player);
            //@formatter:on
        };
    }

    /* RANDOM */

    public static StaticObjectType randomType(Random random)
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

    public static StaticObject randomStaticAt(double x, double y, @Nullable Player player, Random random)
    {
        return staticAt(x, y, random, randomType(random), player);
    }

    /* HELPERS */

    @SuppressWarnings("unchecked")
    private static <T extends StaticObject> T applyPosAndTag(T instance, double x, double y)
    {
        return (T) instance.addTag(StaticTag.class).getTransform().setPosition(x, y).getGameObject();
    }
}