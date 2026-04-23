package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.game.scenes.levelscene.gameobjects.statics.*;

import java.util.Optional;

public enum StaticObjectType
{
    BUSH,
    FALLEN_TREE,
    GARBAGE_ITEM,
    PLANT,
    TREE;

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
}
