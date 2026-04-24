package edu.nust.game.scenes.levelscene.gameobjects.enemy.types;

import edu.nust.engine.core.GameObject;
import edu.nust.game.systems.collision.Damageable;
import edu.nust.game.systems.collision.Damaging;
import edu.nust.game.systems.collision.HitBox;

import java.util.List;

public class AttackObj extends GameObject implements Damaging {

    private int damage;
    private Enemy enemy;
    private HitBox hitBox;
    private double range;
    private List<? extends Damaging> damagingObjs;

    public AttackObj(int damage, double range, Enemy enemy,List<? extends Damaging> damagingObjs){
        this.damage = damage;
        this.range = range;
        this.enemy = enemy;
        this.damagingObjs = damagingObjs;
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
        return List.of(Enemy.class);
    }

}
