package edu.nust.game.tilemap;

/**
 * Represents a single tile in a tilemap.
 * Stores the tileset reference and tile index within that tileset.
 */
public class TileData
{
    public static final int EMPTY_TILE = -1;

    private final int tilesetId;
    private final int tileIndex;

    /**
     * Creates an empty tile (no tile rendered)
     */
    public TileData()
    {
        this.tilesetId = EMPTY_TILE;
        this.tileIndex = EMPTY_TILE;
    }

    /**
     * Creates a tile with the given tileset and index
     * @param tilesetId Reference to the tileset ID
     * @param tileIndex Index of the tile within the tileset
     */
    public TileData(int tilesetId, int tileIndex)
    {
        this.tilesetId = tilesetId;
        this.tileIndex = tileIndex;
    }

    public int getTilesetId()
    {
        return tilesetId;
    }

    public int getTileIndex()
    {
        return tileIndex;
    }

    public boolean isEmpty()
    {
        return tilesetId == EMPTY_TILE;
    }

    @Override
    public String toString()
    {
        return "TileData{" +
                "tilesetId=" + tilesetId +
                ", tileIndex=" + tileIndex +
                '}';
    }
}

