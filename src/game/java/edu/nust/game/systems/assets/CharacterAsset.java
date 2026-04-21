package edu.nust.game.systems.assets;

/**
 * Enumeration of available character sprites in the PostApocalypse asset pack. Each character has multiple animation
 * frames stored in subdirectories. Implements {@link AssetEnum} to follow the standard asset management pattern.
 */
public enum CharacterAsset implements AssetEnum
{
    MAIN("Character/Main"),
    HELMET("Character/Helmet"),
    GUNS("Character/Guns"),
    BAT("Character/Bat");

    private final String path;

    CharacterAsset(String path)
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
        return AssetType.CHARACTER;
    }

    @Override
    public String getId()
    {
        return getType().name() + "_" + this.name();
    }
}
