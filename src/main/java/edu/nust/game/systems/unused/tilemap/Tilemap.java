package edu.nust.game.systems.unused.tilemap;

import edu.nust.engine.logger.GameLogger;

/**
 * 2D grid-based tilemap container.
 */
public class Tilemap
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private final int width;
    private final int height;
    private final int tileSize;
    private final TileData[][] grid;

    /**
     * Creates a new tilemap.
     */
    public Tilemap(int width, int height, int tileSize)
    {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.grid = new TileData[height][width];

        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                grid[row][col] = new TileData();
            }
        }

        logger.debug("Created tilemap: {}x{} (tile size: {}px)", width, height, tileSize);
    }

    /**
     * Sets a tile at grid position.
     */
    public void setTile(int col, int row, TileData tile)
    {
        if (isValidPosition(col, row))
        {
            grid[row][col] = tile;
        }
        else
        {
            logger.warn("Attempted to set tile outside tilemap bounds: ({}, {})", col, row);
        }
    }

    /**
     * Gets a tile at grid position.
     */
    public TileData getTile(int col, int row)
    {
        if (isValidPosition(col, row))
        {
            return grid[row][col];
        }
        return new TileData();
    }

    /**
     * Gets a tile from world coordinates.
     */
    public TileData getTileAtWorldPos(double worldX, double worldY)
    {
        int col = (int) (worldX / tileSize);
        int row = (int) (worldY / tileSize);
        return getTile(col, row);
    }

    /**
     * Fills the full map with a tile.
     */
    public void fillAll(TileData tile)
    {
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                grid[row][col] = tile;
            }
        }
        logger.debug("Filled entire tilemap with tile: {}", tile);
    }

    /**
     * Fills a rectangular region.
     */
    public void fillRect(int startCol, int startRow, int width, int height, TileData tile)
    {
        for (int row = startRow; row < startRow + height; row++)
        {
            for (int col = startCol; col < startCol + width; col++)
            {
                if (isValidPosition(col, row))
                {
                    grid[row][col] = tile;
                }
            }
        }
    }

    /**
     * Returns true when a grid position is inside bounds.
     */
    private boolean isValidPosition(int col, int row)
    {
        return col >= 0 && col < width && row >= 0 && row < height;
    }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getTileSize() { return tileSize; }

    public int getPixelWidth() { return width * tileSize; }

    public int getPixelHeight() { return height * tileSize; }

    public TileData[][] getGrid() { return grid; }
}

