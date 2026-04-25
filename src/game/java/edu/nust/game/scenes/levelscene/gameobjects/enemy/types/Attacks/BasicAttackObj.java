package edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Attacks;

import edu.nust.engine.core.GameObject;
import edu.nust.engine.core.GameScene;
import edu.nust.engine.math.TimeSpan;
import edu.nust.engine.math.Vector2D;
import edu.nust.game.scenes.levelscene.gameobjects.enemy.types.Enemy;
import edu.nust.game.scenes.levelscene.gameobjects.player.Player;
import edu.nust.game.systems.collision.Damageable;
import edu.nust.game.systems.collision.Damaging;
import edu.nust.game.systems.collision.HitBox;

import java.util.List;


public class BasicAttackObj extends GameObject implements Damaging {

    private int damage;
    private Enemy enemy;
    private HitBox hitBox;
    private double height;
    private double width;
    private double range;
    private final List<Class<? extends Damageable>> notDamageObj;
    private boolean isMoving;
    private TimeSpan lastsFor;
    private Vector2D currentPos;
    private double movedDistance = 0;
    private double movementSpeed;
    private Vector2D direction;
    private double elapsed;

    public BasicAttackObj(int damage, Enemy enemy, double width, double height, double range,
                          List<Class<? extends Damageable>> notDamageObj, TimeSpan lastsFor,boolean isMoving){
        this.damage = damage;
        this.enemy = enemy;
        this.width = width;
        this.height = height;
        this.notDamageObj = notDamageObj;
        this.range = range;
        this.lastsFor = lastsFor;
        this.isMoving = false;
        this.isMoving = isMoving;
    }


    @Override
    public void onInit(){
        hitBox = new HitBox(enemy.getTransform().getPosition(),height/2,width/2);
        GameScene scene = enemy.getScene();
        Player player = (Player) scene.getFirstOfType(Player.class);
        if(isMoving) {
            if (player != null) {
                direction = (player.getTransform().getPosition().subtract(enemy.getTransform().getPosition())).normalize();
            }
            if (direction != null) {
                currentPos = new Vector2D(direction.getX() * width / 2, direction.getY() * height / 2);
            }
        }
    }

    @Override
    public void onUpdate(TimeSpan deltaTime){
        if(isMoving) move(deltaTime);
        else {
            elapsed += deltaTime.asMilliseconds();
            if (elapsed >= lastsFor.asMilliseconds()) {
                destroyThis();
            }
        }

    }

    public void move(TimeSpan deltaTime){
        if(movedDistance >= range) {
            this.destroyThis();
            return;
        }
        double distance = movementSpeed * deltaTime.asSeconds();
        currentPos = currentPos.add(direction.multiply(distance));
        movedDistance += distance;
        this.getTransform().setPosition(currentPos);
    }

    @Override
    public int getDamage(){
        return damage;
    }

    @Override
    public boolean isDestroyable(){
        return true;
    }

    @Override
    public HitBox getHitbox(){
        return hitBox;
    }

    @Override
    public void destroyThis(){
        this.destroy();
    }

    @Override
    public List<Class<? extends Damageable>> notDamageObj(){

        return notDamageObj;
    }

    public double getRange() {
        return range;
    }
}
