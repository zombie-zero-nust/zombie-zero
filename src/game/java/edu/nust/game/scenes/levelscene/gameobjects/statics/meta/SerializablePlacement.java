package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;

/// Used to store the necessary information for serializing and deserializing static objects in the level.
public record SerializablePlacement(Rectangle rect, Vector2D position, StaticObjectType type, int variant) { }