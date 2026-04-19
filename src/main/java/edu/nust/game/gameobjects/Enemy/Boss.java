package edu.nust.game.gameobjects.Enemy;


import edu.nust.engine.math.Vector2D;
import edu.nust.game.assets.EnemyAsset;

public class Boss extends Enemy{
    Boss(Vector2D pos, double speed, int health, double height , double width,double damage){
        super(pos,speed,health,height,width,damage,EnemyAsset.ZOMBIE_BIG);
    }
}
