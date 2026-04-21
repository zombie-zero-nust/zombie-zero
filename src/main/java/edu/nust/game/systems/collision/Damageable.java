package edu.nust.game.systems.collision;

import edu.nust.game.scenes.levelscene.gameobjects.player.Health;

public interface Damageable
{
    void takeDamage(int damage);

    void setHealth(Health health);

    Health getHealth();

    HitBox getHitbox();

    boolean isDead();
}
