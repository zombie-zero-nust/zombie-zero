package edu.nust.game.scenes.levelscene.gameobjects.enemy.types;


import edu.nust.engine.math.Vector2D;
import edu.nust.game.systems.assets.EnemyAsset;

public class Boss extends Enemy
{
    Boss(Vector2D pos, double speed, int health, double height, double width, double damage)
    {
        super(pos, speed, health, height, width, damage, EnemyAsset.ZOMBIE_BIG);
    }

    @Override
    public void loadSprites(EnemyAsset enemyType)
    {

    }

    @Override
    public void updateSprite(double dx, double dy)
    {

    }

    @Override
    public void attack()
    {

    }

}
