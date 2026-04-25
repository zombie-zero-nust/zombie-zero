package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.scenes.levelscene.gameobjects.statics.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public enum StaticObjectType
{
    //@formatter:off
    // weight out of 100
    BUSH         (5),
    FALLEN_TREE  (5),
    FLOWER       (5),
    GARBAGE      (5),
    GRASS        (25),
    GROUND_GRASS (45),
    ROCK         (5),
    STICK        (5),
    TREE         (5),
    TREE_STUMP   (5);
    //@formatter:on

    private final double weight;

    StaticObjectType(double weight) { this.weight = weight; }

    public double getWeight() { return weight; }

    /* FACTORY */

    // In StaticObjectType enum, add:
    public StaticObject create(int variant, @Nullable Player player)
    {
        return switch (this)
        {
            case BUSH -> new Bush(variant);
            case FALLEN_TREE -> new FallenTree(variant);
            case FLOWER -> new Flower(variant);
            case GARBAGE -> new Garbage(variant);
            case GRASS -> new Grass(variant);
            case GROUND_GRASS -> new GroundGrass(variant);
            case ROCK -> new Rock(variant);
            case STICK -> new Stick(variant);
            case TREE -> new Tree(variant, player);
            case TREE_STUMP -> new TreeStump(variant);
        };
    }

    public StaticObject create(Random random, @Nullable Player player)
    {
        return switch (this)
        {
            case BUSH -> new Bush(random);
            case FALLEN_TREE -> new FallenTree(random);
            case FLOWER -> new Flower(random);
            case GARBAGE -> new Garbage(random);
            case GRASS -> new Grass(random);
            case GROUND_GRASS -> new GroundGrass(random);
            case ROCK -> new Rock(random);
            case STICK -> new Stick(random);
            case TREE -> new Tree(random, player);
            case TREE_STUMP -> new TreeStump(random);
        };
    }

    /* RANDOM */

    /// Returns a random type from the provided options. Throws if given list is empty
    public static StaticObjectType randomFrom(Random random, List<StaticObjectType> options)
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

    public static StaticObjectType random(Random random) { return randomFrom(random, List.of(values())); }

    /* STATIC HELPERS */

    public static Optional<StaticObjectType> getType(Object object)
    {
        return switch (object)
        {
            case Bush bush -> Optional.of(BUSH);
            case FallenTree fallenTree -> Optional.of(FALLEN_TREE);
            case Flower flower -> Optional.of(FLOWER);
            case Garbage garbage -> Optional.of(GARBAGE);
            case Grass grass -> Optional.of(GRASS);
            case GroundGrass groundGrass -> Optional.of(GROUND_GRASS);
            case Rock rock -> Optional.of(ROCK);
            case Stick stick -> Optional.of(STICK);
            case Tree tree -> Optional.of(TREE);
            case TreeStump treeStump -> Optional.of(TREE_STUMP);
            default -> Optional.empty();
        };
    }
}
