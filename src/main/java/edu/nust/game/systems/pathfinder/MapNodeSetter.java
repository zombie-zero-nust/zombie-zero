package edu.nust.game.systems.pathfinder;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.Enemy;
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
    private HitBox hitbox;
    private GameScene scene;
    private final Vector2D mapTopLeftPos;
    private int xPos;
    private int yPos;
    private ArrayList<Node> checkedNodes;
    private ArrayList<Node> openNodes;

    public MapNodeSetter(int mapWidth, int mapHeight)
    {
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        mapTopLeftPos = mapPos.subtract(new Vector2D(mapWidth / 2, mapHeight / 2));
        hitbox = new HitBox(mapTopLeftPos, 1, 1);
        this.addComponent(hitbox);
        setNodes();
        xPos = (int) mapTopLeftPos.subtract(mapTopLeftPos).getX();
        yPos = (int) mapTopLeftPos.subtract(mapTopLeftPos).getY();
    }


    @Override
    public void onInit()
    {
        traceMap();
    }

    @Override
    public void onUpdate(TimeSpan deltaTime)
    {
        xPos = (int) this.getTransform().getPosition().subtract(mapTopLeftPos).getX();
        yPos = (int) this.getTransform().getPosition().subtract(mapTopLeftPos).getY();
    }

    public void setMapNode()
    {
        ArrayList<GameObject> gameObjs = (ArrayList<GameObject>) this.scene.getAllGameObjects();
        for (GameObject obj : gameObjs)
        {
            if (obj instanceof Concrete && !(obj instanceof Player) && !(obj instanceof Enemy))
            {
                nodes[xPos][yPos].setSolid(hitbox.isTouching(((Concrete) obj).getHitbox()));
            }
        }
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
        Vector2D currPos = new Vector2D(0, 0);
        for (int i = 0; i < mapHeight; i++)
        {
            for (int j = 0; j < mapWidth; j++)
            {
                this.getTransform().setPosition(currPos);
                setMapNode();
                currPos.add(j, i);
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
