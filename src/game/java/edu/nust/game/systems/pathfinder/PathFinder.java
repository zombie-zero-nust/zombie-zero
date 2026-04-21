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

    // PriorityQueue is much faster than scanning openList every time
    private final PriorityQueue<Node> openQueue =
            new PriorityQueue<>(Comparator.comparingInt(Node::getfCost));

    // Track visited nodes so we reset only those (fast)
    private final ArrayList<Node> visitedNodes = new ArrayList<>();

    public PathFinder(LevelScene scene)
    {
        this.scene = scene;

        getMap(scene,
                (int) scene.getWorldWidth(),
                (int) scene.getWorldHeight(),
                new Vector2D(scene.getWorldWidth() / 2.0, scene.getWorldHeight() / 2.0));
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
        start.setAsStart(true);
    }

    public void setGoalNode(int row, int col)
    {
        if (isNotWithinBounds(row, col)) return;

        goal = nodes[row][col];
        goal.setAsGoal(true);
    }

    private void updateStatus(BasicEnemy enemy)
    {
        if (nodes == null || mapTopLeftPos == null) return;

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

    private void resetVisitedNodes()
    {
        for (Node node : visitedNodes)
        {
            node.setAsStart(false);
            node.setAsGoal(false);
            node.setOpen(false);
            node.setChecked(false);
            node.setParent(null);
            node.setgCost(0);
            node.sethCost(0);
            node.setfCost(0);
        }
        visitedNodes.clear();
    }

    private int heuristic(Node a, Node b)
    {
        // Manhattan Distance
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private void openNode(Node neighbor, Node parent)
    {
        if (neighbor.isChecked() || neighbor.isSolid())
            return;

        int newGCost = parent.getgCost() + 1;

        if (!neighbor.isOpen() || newGCost < neighbor.getgCost())
        {
            neighbor.setParent(parent);
            neighbor.setgCost(newGCost);
            neighbor.sethCost(heuristic(neighbor, goal));
            neighbor.setfCost(neighbor.getgCost() + neighbor.gethCost());

            if (!neighbor.isOpen())
            {
                neighbor.setOpen(true);
                openQueue.add(neighbor);
                visitedNodes.add(neighbor);
            }
            else
            {
                // Reinsert to update priority
                openQueue.remove(neighbor);
                openQueue.add(neighbor);
            }
        }
    }

    public ArrayList<Node> getPath(BasicEnemy enemy)
    {
        ArrayList<Node> pathNodes = new ArrayList<>();

        if (nodes == null || mapTopLeftPos == null) return pathNodes;

        resetVisitedNodes();
        openQueue.clear();
        goalReached = false;

        updateStatus(enemy);

        if (start == null || goal == null) return pathNodes;
        if (start.isSolid() || goal.isSolid()) return pathNodes;

        start.setgCost(0);
        start.sethCost(heuristic(start, goal));
        start.setfCost(start.getgCost() + start.gethCost());

        start.setOpen(true);
        openQueue.add(start);
        visitedNodes.add(start);

        while (!openQueue.isEmpty())
        {
            Node current = openQueue.poll();

            current.setChecked(true);

            if (current == goal)
            {
                goalReached = true;
                break;
            }

            int row = current.getRow();
            int col = current.getCol();

            // Down
            if (row < maxRow) openNode(nodes[row + 1][col], current);

            // Up
            if (row > 0) openNode(nodes[row - 1][col], current);

            // Right
            if (col < maxCol) openNode(nodes[row][col + 1], current);

            // Left
            if (col > 0) openNode(nodes[row][col - 1], current);
        }

        if (goalReached)
        {
            Node temp = goal;
            while (temp != null && temp != start)
            {
                pathNodes.add(temp);
                temp = temp.getParent();
            }
            Collections.reverse(pathNodes);
        }

        return pathNodes;
    }
}