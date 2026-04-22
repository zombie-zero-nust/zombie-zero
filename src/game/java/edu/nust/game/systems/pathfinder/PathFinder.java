package edu.nust.game.systems.pathfinder;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;

import java.util.*;

public class PathFinder {

    private Node[][] nodes;
    private Node start;
    private Node current;
    private Node goal;
    private final GameScene scene;

    private int maxRow;
    private int maxCol;
    private Vector2D mapTopLeftPos;

    // Use PriorityQueue for O(log n) efficiency and HashSet for fast lookups
    private final PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getfCost));
    private final HashSet<Node> checkedList = new HashSet<>();

    private boolean goalReached = false;
    private final int nodeSize = 4;

    public PathFinder(GameScene scene){
        this.scene = scene;
        getMap(scene, 3200, 800);
    }

    public void getMap(GameScene scene, int mapWidth, int mapHeight){
        MapNodeSetter nodeSetter = new MapNodeSetter(new Vector2D(0,0), mapWidth, mapHeight, scene);

        this.maxCol = (mapWidth / nodeSize) - 1;
        this.maxRow = (mapHeight / nodeSize) - 1;

        nodes = nodeSetter.getNodes();
        mapTopLeftPos = nodeSetter.getMapTopLeftPos();
    }

    public void setStartNode(int row, int col){
        if (row >= 0 && row <= maxRow && col >= 0 && col <= maxCol) {
            nodes[row][col].setAsStart(true);
            start = nodes[row][col];
        }
    }

    public void setGoalNode(int row, int col){
        if (row >= 0 && row <= maxRow && col >= 0 && col <= maxCol) {
            nodes[row][col].setAsGoal(true);
            goal = nodes[row][col];
        }
    }

    public void updateStatus(BasicEnemy enemy){
        Player player = (Player) scene.getFirstOfType(Player.class);

        if(player != null){
            Vector2D playerPos = player.getTransform().getPosition().subtract(mapTopLeftPos);
            int row = (int)(playerPos.getY() / nodeSize);
            int col = (int)(playerPos.getX() / nodeSize);
            setStartNode(row, col);
        }

        if(enemy != null){
            Vector2D enemyPos = enemy.getTransform().getPosition().subtract(mapTopLeftPos);
            int row = (int)(enemyPos.getY() / nodeSize);
            int col = (int)(enemyPos.getX() / nodeSize);
            setGoalNode(row, col);
        }
    }

    public ArrayList<Node> getPath(BasicEnemy enemy){
        resetNodes();
        updateStatus(enemy);

        if (start == null || goal == null) return new ArrayList<>();

        search();

        ArrayList<Node> pathNodes = new ArrayList<>();
        if(goalReached) {
            Node temp = goal;
            while(temp != start && temp != null){
                pathNodes.add(temp);
                temp = temp.getParent();
            }
            // Reverse so the first element is the first step the enemy should take
            Collections.reverse(pathNodes);
        }

        return pathNodes;
    }

    public void search() {
        // Initial setup for the start node
        start.setgCost(0);
        start.sethCost(calculateHeuristic(start));
        start.setfCost(start.getgCost() + start.gethCost());
        openList.add(start);

        int iterations = 0;
        // Max iterations prevents infinite loops in complex or blocked maps
        while (!openList.isEmpty() && iterations < 1500) {
            current = openList.poll();

            if (current == goal) {
                goalReached = true;
                break;
            }

            current.setChecked(true);
            checkedList.add(current);

            int row = current.getRow();
            int col = current.getCol();

            // Check neighbors
            if (row < maxRow) evaluateNeighbor(nodes[row + 1][col]);
            if (row > 0) evaluateNeighbor(nodes[row - 1][col]);
            if (col < maxCol) evaluateNeighbor(nodes[row][col + 1]);
            if (col > 0) evaluateNeighbor(nodes[row][col - 1]);

            iterations++;
        }
    }

    private void evaluateNeighbor(Node neighbor) {
        if (neighbor.isSolid() || checkedList.contains(neighbor)) return;

        // Current distance + 1 for adjacent movement
        int totalGCost = current.getgCost() + 1;

        if (!neighbor.isOpen() || totalGCost < neighbor.getgCost()) {
            neighbor.setParent(current);
            neighbor.setgCost(totalGCost);
            neighbor.sethCost(calculateHeuristic(neighbor));
            neighbor.setfCost(neighbor.getgCost() + neighbor.gethCost());

            if (!neighbor.isOpen()) {
                neighbor.setOpen(true);
                openList.add(neighbor);
            }
        }
    }

    private int calculateHeuristic(Node node) {
        // Manhattan distance: Row difference + Col difference
        return Math.abs(node.getRow() - goal.getRow()) + Math.abs(node.getCol() - goal.getCol());
    }

    public void resetNodes(){
        // Reset only the nodes that were touched to keep it fast
        for (Node node : checkedList) {
            node.setChecked(false);
            node.setOpen(false);
        }
        for (Node node : openList) {
            node.setOpen(false);
        }

        openList.clear();
        checkedList.clear();
        goalReached = false;

        // Clear start/goal states if your Node class tracks them
        if (start != null) start.setAsStart(false);
        if (goal != null) goal.setAsGoal(false);
    }

    public Vector2D getMapTopLeftPos(){
        return mapTopLeftPos;
    }
}
