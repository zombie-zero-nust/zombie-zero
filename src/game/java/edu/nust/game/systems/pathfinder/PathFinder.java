package edu.nust.game.systems.pathfinder;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;

import java.util.*;

// TODO: Add Logging
public class PathFinder
{

    private Node[][] nodes;
    private Node start, current, goal;
    private final GameScene scene;
    private Vector2D mapTopLeftPos;
    private final MapNodeSetter nodeSetter;

    private final PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getfCost));
    private final HashSet<Node> checkedList = new HashSet<>();

    private boolean goalReached = false;
    private final int nodeSize = 2;

    public PathFinder(LevelScene scene)
    {
        this.scene = scene;
        this.nodeSetter = scene.getNodeSetter();
        this.nodes = nodeSetter.getNodes();
        this.mapTopLeftPos = nodeSetter.getMapTopLeftPos();
    }

    public void updateStatus(Enemy enemy)
    {
        Player player = (Player) scene.getFirstOfType(Player.class);

        if (enemy != null)
        {
            Vector2D enemyPos = enemy.getTransform().getPosition().subtract(mapTopLeftPos);
            int row = clamp((int) Math.floor(enemyPos.getY() / nodeSize), 0, nodes.length - 1);
            int col = clamp((int) Math.floor(enemyPos.getX() / nodeSize), 0, nodes[0].length - 1);
            start = nodes[row][col];
        }

        if (player != null)
        {
            Vector2D playerPos = player.getTransform().getPosition().subtract(mapTopLeftPos);
            int row = clamp((int) (playerPos.getY() / nodeSize), 0, nodes.length - 1);
            int col = clamp((int) (playerPos.getX() / nodeSize), 0, nodes[0].length - 1);
            goal = nodes[row][col];
        }
    }

    public ArrayList<Node> getPath(Enemy enemy)
    {
        if (enemy == null || nodes == null) return new ArrayList<>();

        resetNodes();
        updateStatus(enemy);


        if (start == null || goal == null || start == goal || goal.isSolid())
        {
            System.out.println("triggered");


            return new ArrayList<>();
        }

        search();


        ArrayList<Node> pathNodes = new ArrayList<>();
        if (goalReached)
        {
            Node temp = goal;
            while (temp != start && temp != null)
            {
                pathNodes.add(temp);
                temp = temp.getParent();
            }
            Collections.reverse(pathNodes);
        }
        return pathNodes;
    }

    public void search()
    {
        start.setgCost(0);
        start.sethCost(calculateHeuristic(start));
        start.setfCost(start.getgCost() + start.gethCost());
        start.setOpen(true);
        openList.add(start);

        int iterations = 0;
        while (!openList.isEmpty() && iterations < 10000)
        {
            current = openList.poll();

            if (current == goal)
            {
                goalReached = true;
                break;
            }

            current.setChecked(true);
            checkedList.add(current);

            int r = current.getRow();
            int c = current.getCol();

            if (r > 0) evaluateNeighbor(nodes[r - 1][c]); // Up
            if (r < nodes.length - 1) evaluateNeighbor(nodes[r + 1][c]); // Down
            if (c > 0) evaluateNeighbor(nodes[r][c - 1]); // Left
            if (c < nodes[0].length - 1) evaluateNeighbor(nodes[r][c + 1]); // Right

            iterations++;
        }
    }

    private void evaluateNeighbor(Node neighbor)
    {
        if (neighbor.isSolid() || neighbor.isChecked()) return;

        int totalGCost = current.getgCost() + 1;

        if (!neighbor.isOpen() || totalGCost < neighbor.getgCost())
        {
            neighbor.setParent(current);
            neighbor.setgCost(totalGCost);
            neighbor.sethCost(calculateHeuristic(neighbor));
            neighbor.setfCost(neighbor.getgCost() + neighbor.gethCost());

            if (!neighbor.isOpen())
            {
                neighbor.setOpen(true);
                openList.add(neighbor);
            }
        }
    }

    private int calculateHeuristic(Node node)
    {
        return Math.abs(node.getRow() - goal.getRow()) + Math.abs(node.getCol() - goal.getCol());
    }

    public void resetNodes()
    {

        for (Node[] row : nodes)
        {
            for (Node n : row)
            {
                clearNode(n);
            }
        }
        for (Node n : openList)
        {
            clearNode(n);
        }

        openList.clear();
        checkedList.clear();


        goalReached = false;
    }

    private void clearNode(Node n)
    {
        n.setOpen(false);
        n.setChecked(false);
        n.setParent(null);
        n.setgCost(0);
        n.sethCost(0);
        n.setfCost(0);
    }

    private int clamp(int val, int min, int max)
    {
        return Math.max(min, Math.min(max, val));
    }

    public Vector2D getMapTopLeftPos()
    {
        return mapTopLeftPos;
    }
}