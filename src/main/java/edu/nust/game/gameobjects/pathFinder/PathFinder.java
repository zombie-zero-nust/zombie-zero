package edu.nust.game.gameobjects.pathFinder;

import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.gameobjects.Enemy.Enemy;
import edu.nust.game.gameobjects.Player.Player;

import java.util.ArrayList;

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

    public PathFinder(GameScene scene){
        this.scene = scene;
    }

    public void getMap(GameScene scene,int mapWidth,int mapHeight){
        MapNodeSetter nodeSetter = new MapNodeSetter(mapWidth,mapHeight);
        this.maxCol = mapWidth -1;
        this.maxRow = mapHeight -1;
        nodes = nodeSetter.getNodes();
        mapTopLeftPos = nodeSetter.getMapTopLeftPos();
    }


    public void setStartNode(int row,int col){
        nodes[row][col].setAsStart(true);
        start = nodes[row][col];
    }

    public void setGoalNode(int row, int col){
        nodes[row][col].setAsGoal(true);
        goal = nodes[row][col];
    }

    public void updateStatus(Enemy enemy){
        Player player = (Player) scene.getGameObjectsOfType(Player.class);
        if(player != null){
            Vector2D playerPos = player.getTransform().getPosition().subtract(mapTopLeftPos);
            setStartNode((int)playerPos.getY(),(int)playerPos.getX());

        }
        if(enemy != null){
            Vector2D enemyPos = enemy.getTransform().getPosition().subtract(mapTopLeftPos);
            setGoalNode((int)enemyPos.getY(),(int)enemyPos.getX());
        }
    }

    public ArrayList<Node> getPath(Enemy enemy){
        if(!goalReached) return null;
        updateStatus(enemy);
        search();
        ArrayList<Node> pathNodes = new ArrayList<>();
        ArrayList<Node> directedPath = new ArrayList<>();
        Node current = goal;
        while(current != start){
            pathNodes.add(current);
            current = current.getParent();
        }
        //reversing the arraylist pathNodes
        for(int i = pathNodes.size()-1;i>=0;i++){
            directedPath.add(pathNodes.get(i));
        }
        return directedPath;

    }



    public void search(){
        setCosts();
        int bestNodeIndex = 0;
        int bestNodefCost = 999999;
        int row,col;
        int t =0;
        while(current != goal && t < 1000){
            row = current.getRow();
            col = current.getCol();

            current.setChecked(true);
            checkedList.add(current);
            openList.remove(current);

            //open the down node
            if(row < maxRow){
                openNode(row+1,col);
            }
            //open the up node
            if(row >0){
                openNode(row-1,col);
            }
            //open the right node
            if(col< maxCol){
                openNode(row,col+1);
            }
            //open the left node
            if(col> 0){
                openNode(row,col -1);
            }

            for(int i =0;i<openList.size();i++){
                if(openList.get(i).getfCost()<bestNodefCost){
                    bestNodeIndex = i;
                    bestNodefCost = openList.get(i).getfCost();
                }
                else if(openList.get(i).getfCost() == bestNodefCost){
                    if(openList.get(i).getgCost() < openList.get(bestNodeIndex).getgCost()){
                        bestNodeIndex = i;
                        bestNodefCost = openList.get(i).getfCost();
                    }
                }
            }
            current = openList.get(bestNodeIndex);
            t++;
            if(current == goal) goalReached = true;
        }
    }

    public void openNode(int row,int col){
        Node node = nodes[row][col];
        if(!node.isOpen() && !node.isChecked() && !node.isSolid()){
            node.setOpen(true);
            node.setParent(current);
            openList.add(node);
        }
    }


    public void setCosts(){
        for(Node[] xNodes : nodes){
            for(Node node: xNodes){
                if(node.isStartingNode() || node.isGoalNode()){
                    continue;
                }
                if(start != null){
                    int dist = Math.abs(node.getRow()- start.getRow()) + Math.abs(node.getCol()-start.getCol());
                    node.setgCost(dist);
                }
                if(goal != null){
                    int dist = Math.abs(node.getRow()- goal.getRow()) + Math.abs(node.getCol()-goal.getCol());
                    node.sethCost(dist);
                }
                node.setfCost(node.getgCost()+node.getfCost());
            }
        }
    }
}
