package edu.nust.game.assets;

/**
 * Enumeration of available enemy sprites in the PostApocalypse asset pack.
 * Different zombie variants with unique appearances and sizes.
 * Implements {@link AssetEnum} to follow the standard asset management pattern.
 */
public enum EnemyAsset implements AssetEnum
{
    ZOMBIE_SMALL("Enemies/Zombie_Small"),
    ZOMBIE_BIG("Enemies/Zombie_Big"),
    ZOMBIE_AXE("Enemies/Zombie_Axe"),
    SHOT("Enemies/Shot");

    private final String path;

    EnemyAsset(String path)
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
        return AssetType.ENEMY;
    }

    @Override
    public String getId()
    {
        return getType().name() + "_" + this.name();
    }
}
