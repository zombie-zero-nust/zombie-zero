package edu.nust.game.systems.assets;

/**
 * Interface contract for all asset enums. Any asset enum (CharacterAsset, EnemyAsset, TilesetAsset, etc.) should
 * implement this interface to follow the standard asset management pattern.
 */
public interface AssetEnum
{
    /**
     * Gets the resource path to this asset
     *
     * @return The path relative to the assets folder
     */
    String getPath();

    /**
     * Gets the type category of this asset
     *
     * @return The asset type
     */
    AssetType getType();

    /**
     * Gets a unique identifier for this asset
     *
     * @return Unique cache key combining type and asset name
     */
    String getId();
}
