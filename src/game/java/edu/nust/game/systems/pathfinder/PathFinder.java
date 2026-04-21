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

    private final ArrayList<Node> openList = new ArrayList<>();
    private final ArrayList<Node> checkedList = new ArrayList<>();

    private boolean goalReached = false;

    private final int nodeSize = 4; // NODE SIZE = 4

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
        nodes[row][col].setAsStart(true);
        start = nodes[row][col];
    }

    public void setGoalNode(int row, int col){
        nodes[row][col].setAsGoal(true);
        goal = nodes[row][col];
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
        goalReached = false;
        openList.clear();
        checkedList.clear();

        updateStatus(enemy);

        current = start;
        search();

        ArrayList<Node> pathNodes = new ArrayList<>();

        if(goalReached) {
            Node temp = goal;
            while(temp != start && temp != null){
                pathNodes.add(temp);
                temp = temp.getParent();
            }
        }

        return pathNodes;
    }

    public void search() {

        setCosts();

        int t = 0;

        while (current != goal && t < 1000) {

            int row = current.getRow();
            int col = current.getCol();

            current.setChecked(true);
            checkedList.add(current);
            current.setOpen(false);
            openList.remove(current);

            // open neighbors
            if (row < maxRow) openNode(row + 1, col);
            if (row > 0) openNode(row - 1, col);
            if (col < maxCol) openNode(row, col + 1);
            if (col > 0) openNode(row, col - 1);

            // if no nodes left, break (no path exists)
            if (openList.isEmpty()) {
                break;
            }

            int bestNodeIndex = 0;
            int bestNodefCost = openList.get(0).getfCost();

            for (int i = 1; i < openList.size(); i++) {

                if (openList.get(i).getfCost() < bestNodefCost) {
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).getfCost();
                }
                else if (openList.get(i).getfCost() == bestNodefCost) {
                    if (openList.get(i).getgCost() < openList.get(bestNodeIndex).getgCost()) {
                        bestNodeIndex = i;
                        bestNodefCost = openList.get(i).getfCost();
                    }
                }
            }

            current = openList.get(bestNodeIndex);

            if (current == goal) {
                goalReached = true;
                break;
            }

            t++;
        }
    }

    public void resetNodes(){
        for(Node checked : checkedList){
            checked.setChecked(false);
        }
    }

    public void openNode(int row, int col){

        Node node = nodes[row][col];

        if(!node.isOpen() && !node.isChecked() && !node.isSolid()){
            node.setOpen(true);
            node.setParent(current);
            openList.add(node);
        }
    }

    public void setCosts(){

        for(Node[] xNodes : nodes){
            for(Node node : xNodes){

                if(node.isStartingNode() || node.isGoalNode()){
                    continue;
                }

                if(start != null){
                    int dist = Math.abs(node.getRow() - start.getRow()) +
                            Math.abs(node.getCol() - start.getCol());
                    node.setgCost(dist);
                }

                if(goal != null){
                    int dist = Math.abs(node.getRow() - goal.getRow()) +
                            Math.abs(node.getCol() - goal.getCol());
                    node.sethCost(dist);
                }

                // FIXED FCOST FORMULA
                node.setfCost(node.getgCost() + node.gethCost());
            }
        }
    }

    public Vector2D getMapTopLeftPos(){
        return mapTopLeftPos;
    }
}