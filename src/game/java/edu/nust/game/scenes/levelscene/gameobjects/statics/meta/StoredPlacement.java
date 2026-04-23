package edu.nust.game.scenes.levelscene.gameobjects.statics.meta;

import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;

public record StoredPlacement(Rectangle rect, Vector2D position, StaticObjectType type)
{
    public String getArrayCodeLine()
    {
        return "new StoredPlacement(Rectangle.fromCorners(" + rect.getLeft() + ", " + rect.getTop() + ", " + rect.getRight() + ", " + rect.getBottom() + "), new Vector2D(" + position.getX() + ", " + position.getY() + "), StaticObjectType." + type.name() + "),";
    }
}