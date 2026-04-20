package edu.nust.game.gameobjects.pathFinder;

public class Node {
    private Node parent;
    private int row,col,hCost,gCost,fCost;
    private boolean start,goal,solid,open,checked;
    public Node(int row,int col){
        this.row = row;
        this.col = col;
    }

    public int gethCost() {
        return hCost;
    }

    public void sethCost(int hCost) {
        this.hCost = hCost;
    }

    public int getgCost() {
        return gCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public int getfCost() {
        return fCost;
    }

    public void setfCost(int fCost) {
        this.fCost = fCost;
    }

    public boolean isStartingNode() {
        return start;
    }

    public void setAsStart(boolean start) {
        this.start = start;
    }

    public boolean isGoalNode() {
        return goal;
    }

    public void setAsGoal(boolean goal) {
        this.goal = goal;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
