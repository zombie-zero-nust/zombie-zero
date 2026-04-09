package edu.nust.game.audio;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.audio.AudioReference;

public class Audios
{
    private static GameWorld world;

    public static void setWorld(GameWorld world)
    {
        Audios.world = world;
    }

    public static AudioReference bookClose()
    {
        return world.getSoundWithName("abc");
    }
}
