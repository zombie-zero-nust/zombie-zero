package edu.nust.game.scenes.levelscene.level_1;

import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class Level1CollisionMask
{
    private static final Rectangle MAP_BOUNDS = Rectangle.fromCorners(0, 0, 3200, 800);

    private static final List<Rectangle> BOUNDARY_RECTS = List.of(
            Rectangle.fromCorners(-50, -50, 3250, 4), // Top
            Rectangle.fromCorners(-50, 4, 4, 794), // Left
            Rectangle.fromCorners(3196, 4, 3250, 794), // Right
            Rectangle.fromCorners(-50, 794, 3250, 850) // Bottom
    );

    private static final List<Rectangle> INNER_COLLISION_RECTS = List.of(
            /// Ground
            // Ground Left-Middle
            Rectangle.fromCorners(4, 108, 68, 661),
            // Ground Top Left 1
            Rectangle.fromCorners(108, 4, 852, 308),
            // Ground Top Left 2
            Rectangle.fromCorners(892, 4, 1444, 308),
            // Ground Right-Middle
            Rectangle.fromCorners(1484, 76, 1600, 724),
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
            // Building 1
            Rectangle.fromCorners(1648, 48, 1664, 256), // outer left
            Rectangle.fromCorners(1664, 48, 2000, 64), // outer top
            Rectangle.fromCorners(1986, 64, 2000, 256), // outer right
            Rectangle.fromCorners(1836, 240, 1986, 256), // outer bottom right
            Rectangle.fromCorners(1664, 240, 1748, 256), // outer bottom left
            Rectangle.fromCorners(1776, 64, 1792, 144), // inner top 1
            Rectangle.fromCorners(1904, 64, 1920, 164), // inner top 2.1
            Rectangle.fromCorners(1920, 96, 1972, 112), // inner top 2.2
            Rectangle.fromCorners(1712, 140, 1728, 240), // inner bottom 1.1
            Rectangle.fromCorners(1728, 176, 1844, 192), // inner bottom 1.2
            // Ground
            Rectangle.fromCorners(1648, 304, 2000, 752),
            // Building 2
            Rectangle.fromCorners(2048, 304, 2064, 340), // outer left top
            Rectangle.fromCorners(2064, 304, 2384, 320), // outer top
            Rectangle.fromCorners(2368, 320, 2384, 496), // outer right
            Rectangle.fromCorners(2048, 480, 2368, 496), // outer bottom
            Rectangle.fromCorners(2048, 428, 2064, 480), // outer left bottom
            Rectangle.fromCorners(2256, 396, 2272, 480), // inner bottom 1.1
            Rectangle.fromCorners(2256, 348, 2272, 370), // inner bottom 1.2
            Rectangle.fromCorners(2076, 400, 2160, 416), // inner left 1.1
            Rectangle.fromCorners(2128, 416, 2144, 448), // inner left 1.2
            // Building 3
            Rectangle.fromCorners(2124, 544, 2384, 560), // outer top
            Rectangle.fromCorners(2368, 560, 2384, 752), // outer right
            Rectangle.fromCorners(2048, 736, 2368, 752), // outer bottom
            Rectangle.fromCorners(2048, 652, 2064, 736), // outer left
            Rectangle.fromCorners(2208, 652, 2224, 736), // inner bottom
            Rectangle.fromCorners(2320, 602, 2336, 644), // inner top 1
            Rectangle.fromCorners(2128, 588, 2144, 656), // inner top 2.1
            Rectangle.fromCorners(2096, 656, 2200, 672) // inner top 2.2
    );

    private static final List<Rectangle> ALL_RECTS = new ArrayList<>();

    static
    {
        ALL_RECTS.addAll(BOUNDARY_RECTS);
        ALL_RECTS.addAll(INNER_COLLISION_RECTS);
    }

    public boolean isWalkable(Vector2D pos)
    {
        for (Rectangle rect : ALL_RECTS)
        {
            if (rect.contains(pos)) return false;
        }

        return true;
    }

    public static List<Rectangle> getInnerCollisionRects(){
        return INNER_COLLISION_RECTS;
    }

    public static Rectangle getMapBounds() { return MAP_BOUNDS; }

    public static void forEachRect(Consumer<Rectangle> action) { ALL_RECTS.forEach(r -> action.accept(r.copy())); }

    public static void forEachBoundaryRect(Consumer<Rectangle> action) { BOUNDARY_RECTS.forEach(r -> action.accept(r.copy())); }

    public static void forEachInnerRect(Consumer<Rectangle> action) { INNER_COLLISION_RECTS.forEach(r -> action.accept(r.copy())); }
}
