package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;

public record StoredPlacement(Rectangle rect, Vector2D position, StaticObjectType type, int variant) { }