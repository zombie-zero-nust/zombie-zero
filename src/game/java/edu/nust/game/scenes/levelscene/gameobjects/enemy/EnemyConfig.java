package edu.nust.game.scenes.levelscene.gameobjects.enemy;

public enum EnemyConfig
{
    ENEMY_ATTACK_DISTANCE(50),
    BULLET_COLLISION_DISTANCE(40),
    HITS_TO_DEFEAT(3),
    DEFAULT_SIZE(16),
    DEFAULT_MOVEMENT_SPEED(100);

    private final double value;

    EnemyConfig(double value) { this.value = value; }

    public double getValue() { return value; }

    public int getIntValue() { return (int) value; }
}

