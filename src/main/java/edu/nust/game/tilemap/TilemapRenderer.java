package edu.nust.game.tilemap;

import edu.nust.engine.core.Component;
import edu.nust.engine.core.GameObject;
import edu.nust.engine.logger.GameLogger;
import edu.nust.game.assets.AssetManager;
import edu.nust.game.assets.TilesetAsset;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Component for rendering a tilemap efficiently.
 * Caches tileset images and renders only visible tiles.
 */
public class TilemapRenderer extends Component
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private final Tilemap tilemap;
    private final Map<Integer, TilesetAsset> tilesetMap;
    private final Map<Integer, Image> loadedTilesets;

    /**
     * Creates a tilemap renderer
     * @param tilemap The tilemap to render
     */
    public TilemapRenderer(Tilemap tilemap)
    {
        this.tilemap = tilemap;
        this.tilesetMap = new HashMap<>();
        this.loadedTilesets = new HashMap<>();
        logger.debug("TilemapRenderer created for {}x{} tilemap", tilemap.getWidth(), tilemap.getHeight());
    }

    /**
     * Registers a tileset with an ID for use in the tilemap
     * @param tilesetId The ID to associate with this tileset
     * @param asset The tileset asset to use
     */
    public void registerTileset(int tilesetId, TilesetAsset asset)
    {
        tilesetMap.put(tilesetId, asset);
        logger.debug("Registered tileset ID {} -> {}", tilesetId, asset);
    }

    /**
     * Pre-loads all registered tilesets for faster rendering
     */
    public void preloadTilesets()
    {
        AssetManager assetManager = AssetManager.getInstance();
        for (Map.Entry<Integer, TilesetAsset> entry : tilesetMap.entrySet())
        {
            Image tileset = assetManager.loadTileset(entry.getValue());
            loadedTilesets.put(entry.getKey(), tileset);
        }
        logger.debug("Preloaded {} tilesets", loadedTilesets.size());
    }

    @Override
    public void onRender(GraphicsContext ctx)
    {
        if (!visible || tilemap == null)
            return;

        TileData[][] grid = tilemap.getGrid();
        int tileSize = tilemap.getTileSize();

        for (int row = 0; row < tilemap.getHeight(); row++)
        {
            for (int col = 0; col < tilemap.getWidth(); col++)
            {
                TileData tile = grid[row][col];

                if (!tile.isEmpty())
                {
                    renderTile(ctx, tile, col * tileSize, row * tileSize, tileSize);
                }
            }
        }
    }

    /**
     * Renders a single tile
     */
    private void renderTile(GraphicsContext ctx, TileData tile, double x, double y, int tileSize)
    {
        Image tileset = loadedTilesets.get(tile.getTilesetId());

        if (tileset == null)
        {
            logger.warn("Tileset not loaded for ID: {}", tile.getTilesetId());
            return;
        }

        ctx.drawImage(tileset, x, y, tileSize, tileSize);
    }

    @Override
    public TilemapRenderer setVisible(boolean visible)
    {
        super.setVisible(visible);
        return this;
    }

    public Tilemap getTilemap()
    {
        return tilemap;
    }
}
