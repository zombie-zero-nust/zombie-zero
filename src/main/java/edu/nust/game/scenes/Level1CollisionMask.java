package edu.nust.game.scenes;

import edu.nust.engine.math.Vector2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

/** Read-only walkability mask used to build collision geometry for Level 1. */
public final class Level1CollisionMask
{
    private static final int ROAD_COLOR_DISTANCE_SQ = 2600;
    private static final int COLOR_BIN_SHIFT = 4;
    private static final int COLOR_BIN_SIZE = 1 << COLOR_BIN_SHIFT;

    private final boolean[][] walkable;
    private final int width;
    private final int height;

    private Level1CollisionMask(boolean[][] walkable, int width, int height)
    {
        this.walkable = walkable;
        this.width = width;
        this.height = height;
    }

    static Level1CollisionMask fromImage(Image image)
    {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        boolean[][] walkable = new boolean[height][width];

        PixelReader reader = image.getPixelReader();
        if (reader == null)
            return new Level1CollisionMask(walkable, width, height);

        int[] roadSeed = estimateRoadSeed(reader, width, height);
        int seedR = roadSeed[0];
        int seedG = roadSeed[1];
        int seedB = roadSeed[2];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int argb = reader.getArgb(x, y);
                walkable[y][x] = isPathPixel(argb, seedR, seedG, seedB);
            }
        }

        return new Level1CollisionMask(walkable, width, height);
    }

    public boolean isWalkable(Vector2D worldPos)
    {
        int px = clamp((int) Math.round(worldPos.getX()), 0, width - 1);
        int py = clamp((int) Math.round(worldPos.getY()), 0, height - 1);
        return walkable[py][px];
    }

    private static boolean isPathPixel(int argb, int seedR, int seedG, int seedB)
    {
        int a = (argb >> 24) & 0xFF;
        if (a < 16)
            return false;

        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));

        double brightness = max / 255.0;
        double saturation = max == 0 ? 0.0 : (max - min) / (double) max;

        int dr = r - seedR;
        int dg = g - seedG;
        int db = b - seedB;
        int distanceSq = dr * dr + dg * dg + db * db;

        return distanceSq <= ROAD_COLOR_DISTANCE_SQ && brightness >= 0.55 && saturation <= 0.45;
    }

    private static int[] estimateRoadSeed(PixelReader reader, int width, int height)
    {
        int binsPerChannel = 256 / COLOR_BIN_SIZE;
        int[] bins = new int[binsPerChannel * binsPerChannel * binsPerChannel];

        for (int y = 0; y < height; y += 2)
        {
            for (int x = 0; x < width; x += 2)
            {
                int argb = reader.getArgb(x, y);
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                int max = Math.max(r, Math.max(g, b));
                int min = Math.min(r, Math.min(g, b));
                double brightness = max / 255.0;
                double saturation = max == 0 ? 0.0 : (max - min) / (double) max;

                if (brightness < 0.55 || saturation > 0.45 || !(r >= g && g >= b))
                    continue;

                int rb = r >> COLOR_BIN_SHIFT;
                int gb = g >> COLOR_BIN_SHIFT;
                int bb = b >> COLOR_BIN_SHIFT;
                int idx = (rb * binsPerChannel + gb) * binsPerChannel + bb;
                bins[idx]++;
            }
        }

        int bestIdx = -1;
        int bestCount = -1;
        for (int i = 0; i < bins.length; i++)
        {
            if (bins[i] > bestCount)
            {
                bestCount = bins[i];
                bestIdx = i;
            }
        }

        if (bestIdx >= 0 && bestCount > 0)
        {
            int bb = bestIdx % binsPerChannel;
            int gb = (bestIdx / binsPerChannel) % binsPerChannel;
            int rb = bestIdx / (binsPerChannel * binsPerChannel);
            return new int[] {
                    rb * COLOR_BIN_SIZE + (COLOR_BIN_SIZE / 2),
                    gb * COLOR_BIN_SIZE + (COLOR_BIN_SIZE / 2),
                    bb * COLOR_BIN_SIZE + (COLOR_BIN_SIZE / 2)
            };
        }

        int fallbackArgb = reader.getArgb(width / 2, height / 2);
        return new int[] {
                (fallbackArgb >> 16) & 0xFF,
                (fallbackArgb >> 8) & 0xFF,
                fallbackArgb & 0xFF
        };
    }

    private static int clamp(int value, int min, int max)
    {
        return Math.max(min, Math.min(value, max));
    }
}


