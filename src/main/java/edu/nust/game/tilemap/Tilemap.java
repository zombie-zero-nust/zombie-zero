package edu.nust.game.tilemap;

import edu.nust.engine.logger.GameLogger;

/**
 * Represents a 2D grid-based tilemap for efficient level rendering.
 * Each cell contains a TileData reference pointing to a specific tileset and tile.
 */
public class Tilemap
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private final int width;
    private final int height;
    private final int tileSize;
    private final TileData[][] grid;

    /**
     * Creates a new tilemap
     * @param width Number of tiles wide
     * @param height Number of tiles tall
     * @param tileSize Pixel size of each tile
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
     * Sets a tile at the given grid position
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
     * Gets the tile at the given grid position
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
     * Gets the tile at world position (converts pixels to grid coordinates)
     */
    public TileData getTileAtWorldPos(double worldX, double worldY)
    {
        int col = (int) (worldX / tileSize);
        int row = (int) (worldY / tileSize);
        return getTile(col, row);
    }

    /**
     * Fills the entire tilemap with a single tile type
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
     * Fills a rectangular region with a tile type
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
     * Checks if a grid position is within tilemap bounds
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





