package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;

public final class Level1CollisionMask
{
    private static final Rectangle MAP_BOUNDS = Rectangle.fromCorners(0, 0, 3200, 800);

    private static final Rectangle[] COLLISION_RECTS = new Rectangle[]{
            /// Boundary
            Rectangle.fromCorners(-50, -50, 3250, 4), // Top
            Rectangle.fromCorners(-50, 4, 4, 794), // Left
            Rectangle.fromCorners(3196, 4, 3250, 794), // Right
            Rectangle.fromCorners(-50, 794, 3250, 850), // Bottom
            /// Ground
            Rectangle.fromCorners(4, 108, 68, 661), // Ground Left-Middle
            Rectangle.fromCorners(108, 4, 852, 308), // Ground Top Left 1
            Rectangle.fromCorners(892, 4, 1444, 308), // Ground Top Left 2
            Rectangle.fromCorners(1484, 76, 1596, 724), // Ground Right-Middle
            // Ground Bottom Left 1
            Rectangle.fromCorners(108, 364, 204, 660), // left
            Rectangle.fromCorners(204, 364, 612, 794), // right (rest)
            Rectangle.fromCorners(612, 364, 660, 644), // bottom
            // Ground Bottom Left 2
            Rectangle.fromCorners(700, 364, 876, 644), // left
            Rectangle.fromCorners(876, 364, 1060, 794), // middle
            Rectangle.fromCorners(1060, 364, 1444, 564), // above path
            Rectangle.fromCorners(1060, 604, 1444, 794), // below path
            /// City
    };

    public boolean isWalkable(Vector2D pos)
    {
        for (Rectangle rect : COLLISION_RECTS)
        {
            if (rect.contains(pos)) return false;
        }

        return true;
    }

    public static Rectangle getMapBounds() { return MAP_BOUNDS; }
}


