package edu.nust.game.systems.unused.tilemap;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.level_1.Level1CollisionMask;
import edu.nust.game.systems.assets.TilesetAsset;
import edu.nust.game.systems.collision.ConcreteWall;

/**
 * Builder for constructing tile-based levels and play-area bounds.
 */
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

    /**
     * Creates a level builder.
     */
    public LevelBuilder(int widthTiles, int heightTiles, int tileSize)
    {
        this.tilemap = new Tilemap(widthTiles, heightTiles, tileSize);
        this.renderer = new TilemapRenderer(tilemap);
    }

    /**
     * Fills the full map with a background tile.
     */
    public LevelBuilder fillBackground(TilesetAsset backgroundAsset)
    {
        renderer.registerTileset(BACKGROUND_TILESET, backgroundAsset);
        tilemap.fillAll(new TileData(BACKGROUND_TILESET, 0));
        return this;
    }

    /**
     * Adds boundary walls and computes the playable inner bounds.
     */
    public LevelBuilder addBoundaries()
    {
        return addBoundaries(TilesetAsset.BRICK_WALL);
    }

    /**
     * Adds boundary walls and computes bounds using the specified boundary tileset.
     */
    public LevelBuilder addBoundaries(TilesetAsset boundaryAsset)
    {
        renderer.registerTileset(BOUNDARY_TILESET, boundaryAsset);

        int width = tilemap.getWidth();
        int height = tilemap.getHeight();

        this.playAreaMinX = playAreaBoundaryThickness * tilemap.getTileSize();
        this.playAreaMaxX = (width - playAreaBoundaryThickness) * tilemap.getTileSize();
        this.playAreaMinY = playAreaBoundaryThickness * tilemap.getTileSize();
        this.playAreaMaxY = (height - playAreaBoundaryThickness) * tilemap.getTileSize();

        tilemap.fillRect(0, 0, width, playAreaBoundaryThickness, new TileData(BOUNDARY_TILESET, 0));
        tilemap.fillRect(
                0,
                height - playAreaBoundaryThickness,
                width,
                playAreaBoundaryThickness,
                new TileData(BOUNDARY_TILESET, 0)
        );

        tilemap.fillRect(0, 0, playAreaBoundaryThickness, height, new TileData(BOUNDARY_TILESET, 0));
        tilemap.fillRect(
                width - playAreaBoundaryThickness,
                0,
                playAreaBoundaryThickness,
                height,
                new TileData(BOUNDARY_TILESET, 0)
        );

        return this;
    }

    /**
     * Adds an interior wall rectangle.
     */
    public LevelBuilder addInteriorWall(int startCol, int startRow, int tileWidth, int tileHeight)
    {
        tilemap.fillRect(startCol, startRow, tileWidth, tileHeight, new TileData(BOUNDARY_TILESET, 0));
        return this;
    }

    /**
     * Preloads registered tilesets.
     */
    public LevelBuilder preloadAssets()
    {
        renderer.preloadTilesets();
        return this;
    }

    /**
     * Builds a GameObject with the tilemap renderer attached.
     */
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

    /**
     * Returns the playable inner bounds.
     */
    public PlayAreaBounds getPlayAreaBounds()
    {
        return new PlayAreaBounds(playAreaMinX, playAreaMaxX, playAreaMinY, playAreaMaxY);
    }

    /**
     * Returns the underlying tilemap.
     */
    public Tilemap getTilemap()
    {
        return tilemap;
    }

    /**
     * Returns the tilemap renderer.
     */
    public TilemapRenderer getRenderer()
    {
        return renderer;
    }

    /**
     * Builds and adds concrete boundary walls to the scene.
     */
    public void buildConcreteBoundaryWalls(GameScene scene, double worldWidth, double worldHeight)
    {
        PlayAreaBounds bounds = getPlayAreaBounds();
        if (bounds == null || worldWidth <= 0 || worldHeight <= 0)
            return;

        double minX = bounds.getMinX();
        double maxX = bounds.getMaxX();
        double minY = bounds.getMinY();
        double maxY = bounds.getMaxY();

        double leftWidth = minX;
        double rightWidth = worldWidth - maxX;
        double topHeight = minY;
        double bottomHeight = worldHeight - maxY;
        double innerWidth = Math.max(0, maxX - minX);

        if (leftWidth > 0)
            scene.addGameObject(new ConcreteWall(
                    new Vector2D(leftWidth / 2.0, worldHeight / 2.0),
                    leftWidth,
                    worldHeight
            ));

        if (rightWidth > 0)
            scene.addGameObject(new ConcreteWall(
                    new Vector2D(maxX + rightWidth / 2.0, worldHeight / 2.0),
                    rightWidth,
                    worldHeight
            ));

        if (topHeight > 0 && innerWidth > 0)
            scene.addGameObject(new ConcreteWall(
                    new Vector2D(minX + innerWidth / 2.0, topHeight / 2.0),
                    innerWidth,
                    topHeight
            ));

        if (bottomHeight > 0 && innerWidth > 0)
            scene.addGameObject(new ConcreteWall(
                    new Vector2D(minX + innerWidth / 2.0, maxY + bottomHeight / 2.0),
                    innerWidth,
                    bottomHeight
            ));
    }

    /**
     * Builds and adds concrete boundary walls for Level1 (image-based levels).
     */
    public void buildConcreteBoundaryWallsForLevel1(GameScene scene, PlayAreaBounds bounds, double worldWidth, double worldHeight)
    {
        if (bounds == null || worldWidth <= 0 || worldHeight <= 0)
            return;

        double minX = bounds.getMinX();
        double maxX = bounds.getMaxX();
        double minY = bounds.getMinY();
        double maxY = bounds.getMaxY();

        double leftWidth = minX;
        double rightWidth = worldWidth - maxX;
        double topHeight = minY;
        double bottomHeight = worldHeight - maxY;
        double innerWidth = Math.max(0, maxX - minX);

        if (leftWidth > 0)
            scene.addGameObject(new ConcreteWall(
                    new Vector2D(leftWidth / 2.0, worldHeight / 2.0),
                    leftWidth,
                    worldHeight
            ));

        if (rightWidth > 0)
            scene.addGameObject(new ConcreteWall(
                    new Vector2D(maxX + rightWidth / 2.0, worldHeight / 2.0),
                    rightWidth,
                    worldHeight
            ));

        if (topHeight > 0 && innerWidth > 0)
            scene.addGameObject(new ConcreteWall(
                    new Vector2D(minX + innerWidth / 2.0, topHeight / 2.0),
                    innerWidth,
                    topHeight
            ));

        if (bottomHeight > 0 && innerWidth > 0)
            scene.addGameObject(new ConcreteWall(
                    new Vector2D(minX + innerWidth / 2.0, maxY + bottomHeight / 2.0),
                    innerWidth,
                    bottomHeight
            ));
    }

    /**
     * Builds and adds internal concrete walls for Level1 based on collision mask.
     */
    public void buildLevel1InternalConcreteWalls(GameScene scene, Level1CollisionMask level1CollisionMask, double worldWidth, double worldHeight)
    {
        if (level1CollisionMask == null || worldWidth <= 0 || worldHeight <= 0)
            return;

        final int CELL_SIZE = 8;
        int cols = (int) (worldWidth / CELL_SIZE);
        int rows = (int) (worldHeight / CELL_SIZE);
        if (cols <= 0 || rows <= 0)
            return;

        boolean[][] blocked = new boolean[rows][cols];
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                blocked[row][col] = isBlockedTerrainCell(
                        level1CollisionMask,
                        col,
                        row,
                        CELL_SIZE,
                        worldWidth,
                        worldHeight
                );
            }
        }

        boolean[][] visited = new boolean[rows][cols];
        for (int row = 0; row < rows; row++)
        {
            for (int col = 0; col < cols; col++)
            {
                if (!blocked[row][col] || visited[row][col])
                    continue;

                int rectWidthCells = 0;
                while (col + rectWidthCells < cols && blocked[row][col + rectWidthCells] && !visited[row][col + rectWidthCells])
                    rectWidthCells++;

                int rectHeightCells = 1;
                boolean canGrow = true;
                while (row + rectHeightCells < rows && canGrow)
                {
                    for (int dx = 0; dx < rectWidthCells; dx++)
                    {
                        if (!blocked[row + rectHeightCells][col + dx] || visited[row + rectHeightCells][col + dx])
                        {
                            canGrow = false;
                            break;
                        }
                    }

                    if (canGrow)
                        rectHeightCells++;
                }

                for (int dy = 0; dy < rectHeightCells; dy++)
                {
                    for (int dx = 0; dx < rectWidthCells; dx++)
                    {
                        visited[row + dy][col + dx] = true;
                    }
                }

                double startX = col * CELL_SIZE;
                double startY = row * CELL_SIZE;
                double rectWidth = rectWidthCells * CELL_SIZE;
                double rectHeight = rectHeightCells * CELL_SIZE;
                Vector2D center = new Vector2D(startX + rectWidth / 2.0, startY + rectHeight / 2.0);
                scene.addGameObject(new ConcreteWall(center, rectWidth, rectHeight));
            }
        }
    }

    /**
     * Checks if a terrain cell is blocked based on collision mask.
     */
    private static boolean isBlockedTerrainCell(Level1CollisionMask level1CollisionMask, int col, int row, int cellSize, double worldWidth, double worldHeight)
    {
        double startX = col * cellSize;
        double startY = row * cellSize;
        double endX = startX + cellSize;
        double endY = startY + cellSize;

        Vector2D center = new Vector2D(startX + cellSize / 2.0, startY + cellSize / 2.0);
        if (level1CollisionMask.isWalkable(center))
            return false;

        int margin = Math.max(1, cellSize / 4);
        Vector2D[] samples = new Vector2D[]{
                center,
                new Vector2D(startX + margin, startY + margin),
                new Vector2D(endX - margin, startY + margin),
                new Vector2D(startX + margin, endY - margin),
                new Vector2D(endX - margin, endY - margin)
        };

        int blockedCount = 0;
        for (Vector2D sample : samples)
        {
            if (!level1CollisionMask.isWalkable(sample))
                blockedCount++;
        }

        // Majority rule avoids tiny anti-aliased seams creating walk-through gaps.
        return blockedCount >= 3;
    }

    /**
     * Immutable play-area bounds helper.
     */
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

        /**
         * Clamps a world position to the allowed area.
         */
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

