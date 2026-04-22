package edu.nust.game.systems.pathfinder;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.systems.collision.Concrete;
import edu.nust.game.systems.collision.HitBox;

import java.util.ArrayList;

public class MapNodeSetter extends GameObject {

    private static final int NODE_SIZE = 4;                    // FIX #5: node size

    private Node[][] nodes;
    private final int mapWidth;
    private final int mapHeight;
    private final Vector2D mapTopLeftPos;
    private HitBox hitbox;
    private final GameScene scene;                              // FIX #1: injected, not null
    private int xPos;
    private int yPos;

    public MapNodeSetter(Vector2D mapPos, int mapWidth, int mapHeight, GameScene scene) {
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.scene = scene;                                     // FIX #1: assigned here
        mapTopLeftPos = mapPos.subtract(new Vector2D(mapWidth / 2.0, mapHeight / 2.0));
        hitbox = new HitBox(mapTopLeftPos, NODE_SIZE, NODE_SIZE); // FIX #5: size 4
        this.addComponent(hitbox);
        nodes = new Node[mapWidth][mapHeight];
        setNodes();
        xPos = 0;                                               // FIX #3: was subtract(itself)
        yPos = 0;
    }

    @Override
    public void onInit() {
        traceMap();
    }

    @Override
    public void onUpdate(TimeSpan deltaTime) {
        Vector2D currentPos = this.getTransform().getPosition().subtract(mapTopLeftPos);
        xPos = (int) currentPos.getX();
        yPos = (int) currentPos.getY();
    }

    public void setMapNode() {
        if (xPos >= 0 && xPos < mapWidth && yPos >= 0 && yPos < mapHeight) {
            ArrayList<GameObject> gameObjs = (ArrayList<GameObject>) this.scene.getAllGameObjects();
            for (GameObject obj : gameObjs) {
                if (obj instanceof Concrete && !(obj instanceof Player) && !(obj instanceof Enemy)) {
                    nodes[xPos][yPos].setSolid(hitbox.isTouching(((Concrete) obj).getHitbox()));
                }
            }
        }
    }

    private void setNodes() {
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                nodes[j][i] = new Node(j, i);                  // FIX #2: was nodes[i][j]
            }
        }
    }

    private void traceMap() {
        Vector2D currPos = new Vector2D(0, 0);
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                this.getTransform().setPosition(mapTopLeftPos.add(currPos)); // FIX #4: offset added
                setMapNode();
                currPos = currPos.add(new Vector2D(NODE_SIZE, 0)); // FIX #6: step by 4, not 1
            }
            currPos = new Vector2D(0, currPos.getY() + NODE_SIZE); // FIX #6: step by 4
        }
    }

    public Node[][] getNodes() {
        return nodes;
    }

    public Vector2D getMapTopLeftPos() {
        return mapTopLeftPos;
    }
}