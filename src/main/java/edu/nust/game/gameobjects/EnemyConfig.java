package edu.nust.game.gameobjects;

public enum EnemyConfig
{
    PLAYER_COLLISION_DISTANCE(50),
    BULLET_COLLISION_DISTANCE(40),
    HITS_TO_DEFEAT(3),
    DEFAULT_SIZE(50),
    DEFAULT_MOVEMENT_SPEED(100);

    private final double value;

    EnemyConfig(double value)
    {
        this.value = value;
    }

    public double getValue()
    {
        return value;
    }

    public int getIntValue()
    {
        return (int) value;
    }
}

