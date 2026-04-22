package edu.nust.game.systems.pathfinder;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.LevelScene;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.BasicEnemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;

import java.util.*;

public class PathFinder {

    private Node[][] nodes;
    private Node start, current, goal;
    private final GameScene scene;
    private int maxRow, maxCol;
    private Vector2D mapTopLeftPos;
    private MapNodeSetter nodeSetter;

    private final PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getfCost));
    private final HashSet<Node> checkedList = new HashSet<>();

    private boolean goalReached = false;
    private final int nodeSize = 4;

    public PathFinder(LevelScene scene){
        this.scene = scene;
        this.nodeSetter = scene.getNodeSetter();
        getMap(scene, 3200, 800);
    }

    public void getMap(GameScene scene, int mapWidth, int mapHeight){
        this.maxCol = (mapWidth / nodeSize) - 1;
        this.maxRow = (mapHeight / nodeSize) - 1;
        nodes = nodeSetter.getNodes();
        mapTopLeftPos = nodeSetter.getMapTopLeftPos();
    }

    public void updateStatus(BasicEnemy enemy){
        Player player = (Player) scene.getFirstOfType(Player.class);
        if(player != null){
            Vector2D playerPos = player.getTransform().getPosition().subtract(mapTopLeftPos);
            int row = Math.min(maxRow, Math.max(0, (int)(playerPos.getY() / nodeSize)));
            int col = Math.min(maxCol, Math.max(0, (int)(playerPos.getX() / nodeSize)));
            start = nodes[row][col];
        }
        if(enemy != null){
            Vector2D enemyPos = enemy.getTransform().getPosition().subtract(mapTopLeftPos);
            int row = Math.min(maxRow, Math.max(0, (int)(enemyPos.getY() / nodeSize)));
            int col = Math.min(maxCol, Math.max(0, (int)(enemyPos.getX() / nodeSize)));
            goal = nodes[row][col];
        }
    }

    public ArrayList<Node> getPath(BasicEnemy enemy){
        resetNodes(); // Must clear EVERYTHING before starting
        updateStatus(enemy);

        if (start == null || goal == null || start == goal) return new ArrayList<>();

        search();

        ArrayList<Node> pathNodes = new ArrayList<>();
        if(goalReached) {
            Node temp = goal;
            while(temp != start && temp != null){
                pathNodes.add(temp);
                temp = temp.getParent();
            }
            Collections.reverse(pathNodes);
        }
        return pathNodes;
    }

    public void search() {
        start.setgCost(0);
        start.sethCost(calculateHeuristic(start));
        start.setfCost(start.getgCost() + start.gethCost());
        start.setOpen(true); // Mark as open so it's not re-added
        openList.add(start);

        int iterations = 0;
        while (!openList.isEmpty() && iterations < 2000) {
            current = openList.poll();

            if (current == goal) {
                goalReached = true;
                break;
            }

            current.setChecked(true);
            checkedList.add(current);

            int r = current.getRow();
            int c = current.getCol();

            if (r < maxRow) evaluateNeighbor(nodes[r + 1][c]);
            if (r > 0) evaluateNeighbor(nodes[r - 1][c]);
            if (c < maxCol) evaluateNeighbor(nodes[r][c + 1]);
            if (c > 0) evaluateNeighbor(nodes[r][c - 1]);

            iterations++;
        }
    }

    private void evaluateNeighbor(Node neighbor) {
        if (neighbor.isSolid() || checkedList.contains(neighbor)) return;

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
        return Math.abs(node.getRow() - goal.getRow()) + Math.abs(node.getCol() - goal.getCol());
    }

    public void resetNodes() {
        // You MUST reset every single node in the entire grid if they are shared
        for (int i = 0; i <= maxRow; i++) {
            for (int j = 0; j <= maxCol; j++) {
                Node n = nodes[i][j];
                n.setOpen(false);
                n.setChecked(false);
                n.setParent(null);
                n.setgCost(0);
                n.sethCost(0);
                n.setfCost(0);
            }
        }
        openList.clear();
        checkedList.clear();
        goalReached = false;
    }

    public Vector2D getMapTopLeftPos(){ return mapTopLeftPos; }
}
