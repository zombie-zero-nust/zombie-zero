package edu.nust.game.assets;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton asset manager for efficient loading and caching of game assets.
 * Generic implementation that works with any asset type implementing {@link AssetEnum}.
 * Prevents duplicate image loading and centralizes asset access.
 */
public class AssetManager
{
    private static AssetManager instance;
    private final GameLogger logger = GameLogger.getLogger(this.getClass());
    private final Map<String, Image> imageCache = new HashMap<>();

    private AssetManager()
    {
        logger.debug("AssetManager initialized");
    }

    /**
     * Gets the singleton instance of AssetManager
     */
    public static synchronized AssetManager getInstance()
    {
        if (instance == null)
        {
            instance = new AssetManager();
        }
        return instance;
    }

    /**
     * Generic method to load and cache any asset image.
     * Works with CharacterAsset, EnemyAsset, TilesetAsset, or any AssetEnum implementation
     *
     * @param asset The asset to load (implements AssetEnum)
     * @return The loaded Image, or null if loading failed
     */
        public Image loadAsset(AssetEnum asset)
    {
        String cacheKey = asset.getId();

        if (imageCache.containsKey(cacheKey))
        {
            logger.trace("Retrieved cached image: {} ({})", asset.getType().getDisplayName(), cacheKey);
            return imageCache.get(cacheKey);
        }

        try
        {
            URL assetUrl = Resources.tryGetResource("assets", asset.getPath());
            if (assetUrl == null)
            {
                logger.warn("Asset not found: {} -> {}", cacheKey, asset.getPath());
                return null;
            }

            Image image = new Image(assetUrl.toExternalForm());
            imageCache.put(cacheKey, image);
            logger.debug("Loaded and cached asset: {} ({})", asset.getType().getDisplayName(), cacheKey);
            return image;
        }
        catch (Exception e)
        {
            logger.error(false, "Failed to load asset {} ({}): {}", cacheKey, asset.getPath(), e.getMessage());
            return null;
        }
    }

    /**
     * Convenience method for loading character sprites
     * @param asset The character asset to load
     * @return The loaded Image
     */
    public Image loadCharacter(CharacterAsset asset)
    {
        return loadAsset(asset);
    }

    /**
     * Convenience method for loading enemy sprites
     * @param asset The enemy asset to load
     * @return The loaded Image
     */
    public Image loadEnemy(EnemyAsset asset)
    {
        return loadAsset(asset);
    }

    /**
     * Convenience method for loading tilesets
     * @param asset The tileset asset to load
     * @return The loaded Image
     */
    public Image loadTileset(TilesetAsset asset)
    {
        return loadAsset(asset);
    }

    /**
     * Gets a cached image without trying to load
     */
    public Image getImageFromCache(String assetId)
    {
        return imageCache.get(assetId);
    }

    /**
     * Checks if an asset is already cached
     */
    public boolean isAssetCached(AssetEnum asset)
    {
        return imageCache.containsKey(asset.getId());
    }

    /**
     * Clears all cached images (useful when unloading a level)
     */
    public void clearCache()
    {
        logger.debug("Clearing asset cache ({} images)", imageCache.size());
        imageCache.clear();
    }

    /**
     * Clears cache for a specific asset type
     */
    public void clearCacheByType(AssetType type)
    {
        String prefix = type.name() + "_";
        int removedCount = (int) imageCache.keySet().stream()
                .filter(k -> k.startsWith(prefix))
                .peek(imageCache::remove)
                .count();
        logger.debug("Cleared {} cached images of type {}", removedCount, type.getDisplayName());
    }

    /**
     * Gets cache statistics
     */
    public int getCacheSize()
    {
        return imageCache.size();
    }

    /**
     * Gets a detailed cache report
     */
    public void printCacheReport()
    {
        Map<String, Integer> countByType = new HashMap<>();
        for (String key : imageCache.keySet())
        {
            String type = key.split("_")[0];
            countByType.put(type, countByType.getOrDefault(type, 0) + 1);
        }

        logger.info("=== Asset Cache Report ===");
         logger.info("Total cached images: {}", imageCache.size());
        countByType.forEach((type, count) ->
            logger.info("  {}: {} images", type, count)
        );
    }
}




