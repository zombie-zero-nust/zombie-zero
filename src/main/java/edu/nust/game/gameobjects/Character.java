package edu.nust.game.gameobjects;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.math.Vector2D;

public abstract class Character extends GameObject {
    private Vector2D position;
    private int health=100;
    private int movementSpeed = 100;
    private boolean moveable;

    public Character(Vector2D pos, int health, int mSpeed,boolean moveable){
        position = pos;
        this.health = health;
        movementSpeed = mSpeed;
        this.moveable=moveable;
    }

    public double getY(){
        return this.position.getY();
    }

    public double getX(){
        return this.position.getX();
    }

    public void setX(double X){
        this.position.setX(X);
    }

    public void setY(double Y){
        this.position.setY(Y);
    }



    public Character(Vector2D pos,boolean moveable){
        position =pos;
        this.moveable = moveable;
    }
    public void setPos(Vector2D pos){ position = pos;}
    public Vector2D getPos(){return position;}
    public void setHealth(int health){
        if(health < 0){ health = 0;}
        else this.health = health;
    }
    public int getHealth(){ return health; }
    public void setMovementSpeed(int mSpeed){ movementSpeed = mSpeed;}
    public int getMovementSpeed(){ return movementSpeed; }

    public boolean isMoveable() {
        return moveable;
    }

    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

}
