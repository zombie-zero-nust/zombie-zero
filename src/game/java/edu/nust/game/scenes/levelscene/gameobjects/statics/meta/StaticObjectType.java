package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.game.scenes.levelscene.gameobjects.statics.*;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public enum StaticObjectType
{
    //@formatter:off
    PLANT        (75),
    TREE         (0),
    FALLEN_TREE  (0),
    BUSH         (0),
    GARBAGE_ITEM (0);
    //@formatter:on

    private final double weight;

    StaticObjectType(double weight) { this.weight = weight; }

    public double getWeight() { return weight; }

    /* RANDOM */

    /// Returns a random type from the provided options. Throws if given list is empty
    public static StaticObjectType random(Random random, List<StaticObjectType> options)
    {
        if (options.isEmpty()) throw new IllegalArgumentException("Options list cannot be empty.");

        // filter
        double totalWeight = options.stream().mapToDouble(StaticObjectType::getWeight).sum();

        // no weights, pick uniformly from available
        if (totalWeight == 0) return options.get(random.nextInt(options.size()));

        double roll = random.nextDouble(totalWeight);
        for (StaticObjectType type : options)
        {
            if (roll < type.getWeight()) return type;
            roll -= type.getWeight();
        }

        return options.getLast(); // fallback
    }

    public static StaticObjectType random(Random random)
    {
        return random(random, List.of(values()));
    }

    /* STATIC HELPERS */

    public static Optional<StaticObjectType> getType(Object object)
    {
        return switch (object)
        {
            case Bush bush -> Optional.of(BUSH);
            case FallenTree fallenTree -> Optional.of(FALLEN_TREE);
            case GarbageItem garbageItem -> Optional.of(GARBAGE_ITEM);
            case Plant plant -> Optional.of(PLANT);
            case Tree tree -> Optional.of(TREE);
            default -> Optional.empty();
        };
    }

    public static List<StaticObjectType> organics()
    {
        return List.of(BUSH, FALLEN_TREE, PLANT, TREE);
    }
}
