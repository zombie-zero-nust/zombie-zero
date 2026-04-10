package edu.nust.game.tilemap;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.assets.TilesetAsset;

/** Builder for constructing tile-based levels and play-area bounds. */
public class LevelBuilder
{
    private final Tilemap tilemap;
    private final TilemapRenderer renderer;

    private static final int BACKGROUND_TILESET = 0;
    private static final int BOUNDARY_TILESET = 1;

    private int playAreaBoundaryThickness = 3;
    private double playAreaMinX;
    private double playAreaMaxX;
    private double playAreaMinY;
    private double playAreaMaxY;

    /** Creates a level builder. */
    public LevelBuilder(int widthTiles, int heightTiles, int tileSize)
    {
        this.tilemap = new Tilemap(widthTiles, heightTiles, tileSize);
        this.renderer = new TilemapRenderer(tilemap);
    }

    /** Fills the full map with a background tile. */
    public LevelBuilder fillBackground(TilesetAsset backgroundAsset)
    {
        renderer.registerTileset(BACKGROUND_TILESET, backgroundAsset);
        tilemap.fillAll(new TileData(BACKGROUND_TILESET, 0));
        return this;
    }

    /** Adds boundary walls and computes the playable inner bounds. */
    public LevelBuilder addBoundaries()
    {
        renderer.registerTileset(BOUNDARY_TILESET, TilesetAsset.BRICK_WALL);

        int width = tilemap.getWidth();
        int height = tilemap.getHeight();

        this.playAreaMinX = playAreaBoundaryThickness * tilemap.getTileSize();
        this.playAreaMaxX = (width - playAreaBoundaryThickness) * tilemap.getTileSize();
        this.playAreaMinY = playAreaBoundaryThickness * tilemap.getTileSize();
        this.playAreaMaxY = (height - playAreaBoundaryThickness) * tilemap.getTileSize();

        tilemap.fillRect(0, 0, width, playAreaBoundaryThickness, new TileData(BOUNDARY_TILESET, 0));
        tilemap.fillRect(0, height - playAreaBoundaryThickness, width, playAreaBoundaryThickness, new TileData(BOUNDARY_TILESET, 0));

        tilemap.fillRect(0, 0, playAreaBoundaryThickness, height, new TileData(BOUNDARY_TILESET, 0));
        tilemap.fillRect(width - playAreaBoundaryThickness, 0, playAreaBoundaryThickness, height, new TileData(BOUNDARY_TILESET, 0));

        return this;
    }

    /** Adds an interior wall rectangle. */
    public LevelBuilder addInteriorWall(int startCol, int startRow, int tileWidth, int tileHeight)
    {
        tilemap.fillRect(startCol, startRow, tileWidth, tileHeight, new TileData(BOUNDARY_TILESET, 0));
        return this;
    }

    /** Preloads registered tilesets. */
    public LevelBuilder preloadAssets()
    {
        renderer.preloadTilesets();
        return this;
    }

    /** Builds a GameObject with the tilemap renderer attached. */
    public GameObject build()
    {
        GameObject gameObject = GameObject.create();
        gameObject.setVisible(true);
        gameObject.setActive(true);
        renderer.setVisible(true);
        gameObject.addComponent(renderer);
        gameObject.getTransform().setPosition(0, 0);
        return gameObject;
    }

    /** Returns the playable inner bounds. */
    public PlayAreaBounds getPlayAreaBounds()
    {
        return new PlayAreaBounds(playAreaMinX, playAreaMaxX, playAreaMinY, playAreaMaxY);
    }

    /** Returns the underlying tilemap. */
    public Tilemap getTilemap()
    {
        return tilemap;
    }

    /** Returns the tilemap renderer. */
    public TilemapRenderer getRenderer()
    {
        return renderer;
    }

    /** Immutable play-area bounds helper. */
    public static class PlayAreaBounds
    {
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;

        public PlayAreaBounds(double minX, double maxX, double minY, double maxY)
        {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        /** Clamps a world position to the allowed area. */
        public Vector2D clampPosition(Vector2D position)
        {
            double clampedX = Math.max(minX, Math.min(position.getX(), maxX));
            double clampedY = Math.max(minY, Math.min(position.getY(), maxY));
            return new Vector2D(clampedX, clampedY);
        }

        public double getMinX() { return minX; }
        public double getMaxX() { return maxX; }
        public double getMinY() { return minY; }
        public double getMaxY() { return maxY; }
    }
}

