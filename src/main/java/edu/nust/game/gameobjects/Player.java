package edu.nust.game.gameobjects;


import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import javafx.scene.Camera;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Player extends Character {


    public Player() {
        super(new Vector2D(0.5, 0.5), 100, 16,true);
    }
    public Player(Vector2D pos,int health,int mSpeed,boolean moveable){
        super(pos,health,mSpeed,moveable);
    }

    public void movement(KeyEvent event){
        if(event.getCode()== KeyCode.W){
            this.setY(this.getY()-getMovementSpeed());
        }
        if(event.getCode()== KeyCode.S){
            this.setY(this.getY()+getMovementSpeed());
        }
        if(event.getCode()== KeyCode.A){
            this.setX(this.getX()-getMovementSpeed());
        }
        if(event.getCode()== KeyCode.D){
            this.setX(this.getX()+getMovementSpeed());
        }
    }



}
