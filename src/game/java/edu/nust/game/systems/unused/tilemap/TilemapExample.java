package edu.nust.game.systems.unused.tilemap;

/**
 * Example showing how to create and use a tilemap in your game. Copy this pattern when creating new levels.
 */
public class TilemapExample
{
    /**
     * Example: Creates a simple test tilemap (20x15 tiles, 64px each)
     */
    public static Tilemap createTestLevel()
    {
        Tilemap tilemap = new Tilemap(20, 15, 64);

        final int GRASS_TILESET = 0;
        final int BRICK_TILESET = 1;

        tilemap.fillAll(new TileData(GRASS_TILESET, 0));
        tilemap.fillRect(8, 7, 4, 2, new TileData(BRICK_TILESET, 0));

        return tilemap;
    }

    /**
     * Usage example in GameScene:
     * <pre>
     * Tilemap level = TilemapExample.createTestLevel();
     * GameObject tilemapObject = GameObject.create(go -> {
     *     TilemapRenderer renderer = new TilemapRenderer(level);
     *     renderer.registerTileset(0, TilesetAsset.GRASS_ON_TOP);
     *     renderer.registerTileset(1, TilesetAsset.BRICK_WALL);
     *     renderer.preloadTilesets();
     *     go.addComponent(renderer);
     * });
     * scene.addGameObject(tilemapObject);
     * </pre>
     */
}

