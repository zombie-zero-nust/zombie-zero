package edu.nust.game.systems.pathfinder;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.systems.collision.Concrete;
import edu.nust.game.systems.collision.HitBox;

import java.util.ArrayList;

public class MapNodeSetter extends GameObject
{
    private Node[][] nodes;
    private final int mapWidth;
    private final int mapHeight;

    private Vector2D mapPos;
    private final Vector2D mapTopLeftPos;

    private HitBox hitbox;
    private GameScene scene;

    private int xPos;
    private int yPos;

    public MapNodeSetter(int mapWidth, int mapHeight, Vector2D mapCenterPos)
    {
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;

        // Initialize map center position
        this.mapPos = mapCenterPos;

        // Top-left position of the map
        this.mapTopLeftPos = mapPos.subtract(new Vector2D(mapWidth / 2.0, mapHeight / 2.0));

        // Initialize nodes array
        nodes = new Node[mapHeight][mapWidth];

        hitbox = new HitBox(mapTopLeftPos, 1, 1);
        this.addComponent(hitbox);

        setNodes();

        xPos = 0;
        yPos = 0;

        scene = this.getScene();
        traceMap();
    }


    private void setNodes()
    {
        for (int i = 0; i < mapHeight; i++)
        {
            for (int j = 0; j < mapWidth; j++)
            {
                nodes[i][j] = new Node(i, j);
            }
        }
    }

    private void traceMap()
    {

        if (scene == null) return;

        ArrayList<GameObject> gameObjs = (ArrayList<GameObject>) scene.getAllGameObjects();

        for (int i = 0; i < mapHeight; i++)
        {
            for (int j = 0; j < mapWidth; j++)
            {
                // Current world position for this node
                Vector2D currPos = new Vector2D(
                        mapTopLeftPos.getX() + j,
                        mapTopLeftPos.getY() + i
                );

                // Move the object + hitbox to that node position
                this.getTransform().setPosition(currPos);

                xPos = j;
                yPos = i;

                // Check collision with all concrete objects
                boolean isSolid = false;

                for (GameObject obj : gameObjs)
                {
                    if (obj instanceof Concrete && !(obj instanceof Player) && !(obj instanceof BasicEnemy))
                    {
                        if (hitbox.isTouching(((Concrete) obj).getHitbox()))
                        {
                            isSolid = true;
                            break;
                        }
                    }
                }

                nodes[yPos][xPos].setSolid(isSolid);
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