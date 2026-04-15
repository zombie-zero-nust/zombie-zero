package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.math.Vector2D;

public abstract class Character extends GameObject {
    private Vector2D spawnPos;
    private Vector2D prePos;
    private Vector2D movePos;
    private int health=100;
    private int movementSpeed = 40;
    private boolean moveable;


    public Character(Vector2D pos, int health, int mSpeed,boolean moveable){
        spawnPos = pos;
        prePos = pos;
        this.health = health;
        movementSpeed = mSpeed;
        this.moveable=moveable;
        movePos = pos;
    }

    public double getY(){
        return this.movePos.getY();
    }

    public double getX(){
        return this.movePos.getX();
    }

    public void setX(double X){
        this.movePos.setX(X);
    }

    public void setY(double Y){
        this.movePos.setY(Y);
    }



    public Character(Vector2D pos,boolean moveable){
        prePos =pos;
        this.moveable = moveable;
    }

    public void setPrePos(Vector2D pos){ prePos = pos;}
    public Vector2D getPrePos(){return prePos;}

    public Vector2D getMovePos(){return this.movePos;}
    public void setMovePos(Vector2D movepos){this.movePos = movepos;}

    public Vector2D getSpawnPos(){ return spawnPos; }

    public void setHealth(int health){
        if(health < 0){ health = 0;}
        else this.health = health;
    }

    public void setMovementSpeed(int mSpeed){ movementSpeed = mSpeed;}
    public int getMovementSpeed(){ return movementSpeed; }

    public boolean isMoveable() {
        return moveable;
    }
    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

}
