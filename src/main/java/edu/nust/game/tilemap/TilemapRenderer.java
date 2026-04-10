package edu.nust.game.tilemap;

import edu.nust.engine.core.Component;
import edu.nust.engine.logger.GameLogger;
import edu.nust.game.assets.AssetManager;
import edu.nust.game.assets.TilesetAsset;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/** Renders a tilemap using registered tilesets. */
public class TilemapRenderer extends Component
{
    private final GameLogger logger = GameLogger.getLogger(this.getClass());

    private final Tilemap tilemap;
    private final Map<Integer, TilesetAsset> tilesetMap;
    private final Map<Integer, Image> loadedTilesets;

    /** Creates a renderer for a tilemap. */
    public TilemapRenderer(Tilemap tilemap)
    {
        this.tilemap = tilemap;
        this.tilesetMap = new HashMap<>();
        this.loadedTilesets = new HashMap<>();
        logger.debug("TilemapRenderer created for {}x{} tilemap", tilemap.getWidth(), tilemap.getHeight());
    }

    /** Registers a tileset ID mapping. */
    public void registerTileset(int tilesetId, TilesetAsset asset)
    {
        tilesetMap.put(tilesetId, asset);
        logger.debug("Registered tileset ID {} -> {}", tilesetId, asset);
    }

    /** Preloads all registered tilesets. */
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
        if (tilemap == null)
        {
            logger.warn("TilemapRenderer.onRender() - tilemap is NULL!");
            return;
        }

        TileData[][] grid = tilemap.getGrid();
        int tileSize = tilemap.getTileSize();
        int mapWidth = tilemap.getPixelWidth();
        int mapHeight = tilemap.getPixelHeight();

        ctx.setFill(javafx.scene.paint.Color.web("#2d5016")); // Dark green grass
        ctx.fillRect(0, 0, mapWidth, mapHeight);

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

    /** Renders a single tile. */
    private void renderTile(GraphicsContext ctx, TileData tile, double x, double y, int tileSize)
    {
        Image tileset = loadedTilesets.get(tile.getTilesetId());

        if (tileset == null)
        {
            if (tile.getTilesetId() == 1)
            {
                ctx.setFill(javafx.scene.paint.Color.web("#8B4513"));
            }
            else
            {
                ctx.setFill(javafx.scene.paint.Color.web("#2d5016"));
            }
            ctx.fillRect(x, y, tileSize, tileSize);
            return;
        }

        int tileIndex = tile.getTileIndex();
        double sourceX = tileIndex * tileSize;
        double sourceY = 0;

        ctx.drawImage(tileset, sourceX, sourceY, tileSize, tileSize, x, y, tileSize, tileSize);
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
