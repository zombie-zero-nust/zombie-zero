package edu.nust.game.systems.assets;

/**
 * Abstract base class for all game assets. Provides common functionality and properties for asset management.
 * <p>
 * For usage, asset enums should implement {@link AssetEnum} instead of extending this class. This allows for flexible,
 * enum-based asset definitions while maintaining a clean architecture.
 *
 * @see AssetEnum
 * @see AssetType
 */
public abstract class Asset
{
    private final String path;
    private final AssetType type;

    /**
     * Creates an asset with the given path and type
     *
     * @param path The resource path to the asset
     * @param type The type of asset
     */
    protected Asset(String path, AssetType type)
    {
        this.path = path;
        this.type = type;
    }

    /**
     * Gets the full resource path to this asset
     *
     * @return The path relative to assets folder
     */
    public String getPath()
    {
        return path;
    }

    /**
     * Gets the type of this asset
     *
     * @return The asset type enumeration
     */
    public AssetType getType()
    {
        return type;
    }

    /**
     * Gets a unique identifier for this asset
     *
     * @return Combination of type and asset name
     */
    public String getId()
    {
        return type.name() + "_" + this.toString();
    }

    @Override
    public String toString()
    {
        return "Asset{" +
                "path='" + path + '\'' +
                ", type=" + type +
                '}';
    }
}

