package edu.nust.game.systems.pathfinder;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;

import java.util.*;

public class PathFinder
{
    private Node[][] nodes;

    private Node start;
    private Node goal;

    private final GameScene scene;

    private int maxRow;
    private int maxCol;

    private Vector2D mapTopLeftPos;

    private boolean goalReached = false;

    private final PriorityQueue<Node> openQueue =
            new PriorityQueue<>(Comparator.comparingInt(Node::getfCost));

    private final Set<Node> closedSet = new HashSet<>();
    private final Set<Node> openSet = new HashSet<>();

    public PathFinder(LevelScene scene)
    {
        this.scene = scene;

        getMap(scene, (int) scene.getWorldWidth(), (int) scene.getWorldHeight(), new Vector2D(scene.getWorldWidth() / 2.0, scene.getWorldHeight() / 2.0));
    }

    public void getMap(LevelScene scene, int mapWidth, int mapHeight, Vector2D mapCenterPos)
    {
        MapNodeSetter nodeSetter = new MapNodeSetter(mapWidth, mapHeight, mapCenterPos);

        this.maxCol = mapWidth - 1;
        this.maxRow = mapHeight - 1;

        this.nodes = nodeSetter.getNodes();
        this.mapTopLeftPos = nodeSetter.getMapTopLeftPos();
    }

    private boolean isNotWithinBounds(int row, int col)
    {
        return row < 0 || col < 0 || row >= nodes.length || col >= nodes[row].length;
    }

    public void setStartNode(int row, int col)
    {
        if (isNotWithinBounds(row, col)) return;
        start = nodes[row][col];
    }

    public void setGoalNode(int row, int col)
    {
        if (isNotWithinBounds(row, col)) return;
        goal = nodes[row][col];
    }

    private void updateStatus(BasicEnemy enemy)
    {
        Player player = (Player) scene.getFirstOfType(Player.class);

        if (enemy != null)
        {
            Vector2D enemyPos = enemy.getTransform().getPosition().subtract(mapTopLeftPos);
            setStartNode((int) enemyPos.getY(), (int) enemyPos.getX());
        }

        if (player != null)
        {
            Vector2D playerPos = player.getTransform().getPosition().subtract(mapTopLeftPos);
            setGoalNode((int) playerPos.getY(), (int) playerPos.getX());
        }
    }

    private int heuristic(Node a, Node b)
    {
        return Math.abs(a.getRow() - b.getRow()) +
                Math.abs(a.getCol() - b.getCol());
    }

    public ArrayList<Node> getPath(BasicEnemy enemy)
    {
        ArrayList<Node> path = new ArrayList<>();

        if (nodes == null) return path;

        updateStatus(enemy);

        if (start == null || goal == null) return path;
        if (start.isSolid() || goal.isSolid()) return path;

        openQueue.clear();
        closedSet.clear();
        openSet.clear();

        // reset only important fields (avoid full reset spam)
        start.setParent(null);
        start.setgCost(0);
        start.sethCost(heuristic(start, goal));
        start.setfCost(start.gethCost());

        openQueue.add(start);
        openSet.add(start);
        int i = 0;
        while (!openQueue.isEmpty() || i<1500)
        {
            Node current = openQueue.poll();


            if (closedSet.contains(current))
                continue;

            openSet.remove(current);
            closedSet.add(current);

            if (current == goal)
            {
                goalReached = true;
                break;
            }

            int r = current.getRow();
            int c = current.getCol();

            tryNeighbor(r + 1, c, current);
            tryNeighbor(r - 1, c, current);
            tryNeighbor(r, c + 1, current);
            tryNeighbor(r, c - 1, current);
            i++;
        }

        if (goalReached)
        {
            Node temp = goal;
            while (temp != null && temp != start)
            {
                path.add(temp);
                temp = temp.getParent();
            }
            Collections.reverse(path);
        }

        return path;
    }

    private void tryNeighbor(int row, int col, Node parent)
    {
        if (isNotWithinBounds(row, col)) return;

        Node neighbor = nodes[row][col];

        if (neighbor.isSolid() || closedSet.contains(neighbor))
            return;

        int newG = parent.getgCost() + 1;

        if (!openSet.contains(neighbor) || newG < neighbor.getgCost())
        {
            neighbor.setParent(parent);
            neighbor.setgCost(newG);
            neighbor.sethCost(heuristic(neighbor, goal));
            neighbor.setfCost(neighbor.getgCost() + neighbor.gethCost());

            if (!openSet.contains(neighbor))
            {
                openQueue.add(neighbor);
                openSet.add(neighbor);
            }
        }
    }
}