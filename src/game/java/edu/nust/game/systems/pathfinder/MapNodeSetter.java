package edu.nust.game.systems.pathfinder;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Rectangle;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.level_1.Level1CollisionMask;

// TODO: Add Logging
public class MapNodeSetter
{

    private static final int NODE_SIZE = 2;
    private final Node[][] nodes;
    private final int mapWidth;
    private final int mapHeight;
    private final Vector2D mapTopLeftPos;
    private final GameScene scene;

    public MapNodeSetter(Vector2D mapPos, int mapWidth, int mapHeight, GameScene scene)
    {
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.scene = scene;

        this.mapTopLeftPos = mapPos.subtract(new Vector2D(mapWidth / 2.0, mapHeight / 2.0));

        // Define as [Row][Column] -> [Y][X] for standard coordinate mapping
        int rows = mapHeight / NODE_SIZE;
        int cols = mapWidth / NODE_SIZE;
        this.nodes = new Node[rows][cols];

        initializeGrid();
        setSolidNodes();
    }

    private void initializeGrid()
    {
        for (int r = 0; r < nodes.length; r++)
        { // rows (Y)
            for (int c = 0; c < nodes[r].length; c++)
            { // cols (X)
                nodes[r][c] = new Node(r, c);
            }
        }
    }

    public void setSolidNodes()
    {
        int i = 0;
        for (Rectangle rectangle : Level1CollisionMask.getInnerCollisionRects())
        {
            // Translate world coordinates to map-relative coordinates
            // Subtract mapTopLeftPos to handle maps not starting at (0,0)
            double relStartX = rectangle.getTopLeft().getX() - mapTopLeftPos.getX();
            double relStartY = rectangle.getTopLeft().getY() - mapTopLeftPos.getY();
            double relEndX = rectangle.getBottomRight().getX() - mapTopLeftPos.getX();
            double relEndY = rectangle.getBottomRight().getY() - mapTopLeftPos.getY();

            // Convert to grid indices
            int startCol = (int) (relStartX / NODE_SIZE);
            int endCol = (int) (relEndX / NODE_SIZE);
            int startRow = (int) (relStartY / NODE_SIZE);
            int endRow = (int) (relEndY / NODE_SIZE);

            // CLAMPING: Prevent IndexOutOfBounds
            startRow = Math.max(0, startRow);
            endRow = Math.min(nodes.length - 1, endRow);
            startCol = Math.max(0, startCol);
            endCol = Math.min(nodes[0].length - 1, endCol);
            // Fill nodes: nodes[row][col]
            for (int r = startRow; r <= endRow; r++)
            {
                for (int c = startCol; c <= endCol; c++)
                {
                    nodes[r][c].setSolid(true);
                    i++;
                }
            }
        }
    }

    public Node[][] getNodes()
    {
        return nodes;
    }

    public Vector2D getMapTopLeftPos()
    {
        return mapTopLeftPos;
    }
}