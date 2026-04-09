package edu.nust.game.assets;

/**
 * Enumeration of available tileset images in the PostApocalypse asset pack.
 * Each tileset contains multiple tile variations packed into a single image.
 * Implements {@link AssetEnum} to follow the standard asset management pattern.
 */
public enum TilesetAsset implements AssetEnum
{
    BRICK_WALL("Tiles/Brick-Wall_TileSet.png"),
    GRASS_ON_TOP("Tiles/Grass_On-Top_TileSet.png"),
    IRON_FENCE("Tiles/Iron-Fence_TileSet.png"),
    ROOF("Tiles/Roof_TileSet.png"),
    GARBAGE("Tiles/Garbage_TileSet.png"),
    BACKGROUND_DARK_GREEN("Tiles/Background_Dark-Green_TileSet.png"),
    BACKGROUND_GREEN("Tiles/Background_Green_TileSet.png"),
    BACKGROUND_BLEAK_YELLOW("Tiles/Background_Bleak-Yellow_TileSet.png"),
    GUTTER_AND_DOWNSPOUT("Tiles/Gutter-And-Downspout.png");

    private final String path;

    TilesetAsset(String path)
    {
        this.path = "raw/PostApocalypse/" + path;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public AssetType getType()
    {
        return AssetType.TILESET;
    }

    @Override
    public String getId()
    {
        return getType().name() + "_" + this.name();
    }
}
