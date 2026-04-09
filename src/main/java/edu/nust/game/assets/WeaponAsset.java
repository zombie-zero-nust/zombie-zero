package edu.nust.game.assets;

/**
 * Enumeration of available weapon sprites in the PostApocalypse asset pack.
 * Includes pickable weapon items and character-held weapon variants.
 * Implements {@link AssetEnum} to follow the standard asset management pattern.
 */
public enum WeaponAsset implements AssetEnum
{
    // Pickable weapons (from Objects/Pickable/)
    GUN_PICKABLE("Objects/Pickable"),
    PISTOL_PICKABLE("Objects/Pickable"),
    SHOTGUN_PICKABLE("Objects/Pickable"),
    BAT_PICKABLE("Objects/Pickable"),

    // Character held weapons (from Character/Guns/)
    GUN("Character/Guns/Gun"),
    PISTOL("Character/Guns/Pistol"),
    SHOTGUN("Character/Guns/Shotgun");

    private final String path;

    WeaponAsset(String path)
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
        return AssetType.EFFECT;
    }

    @Override
    public String getId()
    {
        return getType().name() + "_" + this.name();
    }
}

