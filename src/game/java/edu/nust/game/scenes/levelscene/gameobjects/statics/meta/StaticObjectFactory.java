package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.game.scenes.levelscene.gameobjects._tags.StaticTag;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class StaticObjectFactory
{
    public static StaticObject staticAt(double x, double y, int variant, StaticObjectType type, @Nullable Player player)
    {
        return applyPosAndTag(type.create(variant, player), x, y);
    }

    public static StaticObject staticAt(double x, double y, Random random, StaticObjectType type, @Nullable Player player)
    {
        return applyPosAndTag(type.create(random, player), x, y);
    }

    /* RANDOM */

    public static StaticObject randomStaticAt(double x, double y, @Nullable Player player, Random random)
    {
        return staticAt(x, y, random, StaticObjectType.random(random), player);
    }

    public static StaticObject randomStaticAt(double x, double y, List<StaticObjectType> options, @Nullable Player player, Random random)
    {
        return staticAt(x, y, random, StaticObjectType.randomFrom(random, options), player);
    }

    /* HELPERS */

    @SuppressWarnings("unchecked")
    private static <T extends StaticObject> T applyPosAndTag(T instance, double x, double y)
    {
        return (T) instance.addTag(StaticTag.class).getTransform().setPosition(x, y).getGameObject();
    }
}