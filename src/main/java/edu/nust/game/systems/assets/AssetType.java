package edu.nust.game.systems.assets;

/**
 * Enumeration of all asset types supported by the asset management system. Used for categorizing and managing different
 * asset categories.
 */
public enum AssetType
{
    CHARACTER("Character"),
    ENEMY("Enemy"),
    TILESET("Tileset"),
    UI("UI"),
    EFFECT("Effect"),
    AUDIO("Audio"),
    ANIMATION("Animation"),
    PARTICLE("Particle");

    private final String displayName;

    AssetType(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * Gets the human-readable display name for this asset type
     *
     * @return The display name
     */
    public String getDisplayName()
    {
        return displayName;
    }
}
