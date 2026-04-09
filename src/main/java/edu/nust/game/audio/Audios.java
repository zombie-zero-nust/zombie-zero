package edu.nust.game.audio;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.audio.AudioReference;

import java.util.function.Consumer;

public class Audios
{
    private static GameWorld world;

    private static final String[] AUDIOS = {  //
            "testAudio.wav",
            // .
            // .
            // add more here
    };

    /* SETUP */

    public static void setWorld(GameWorld world) { Audios.world = world; }

    public static void forEach(Consumer<String> action) { for (String audio : AUDIOS) action.accept(audio); }

    /* REFERENCES */

    public static AudioReference testAudioRef()
    {
        return world.getAudioWithName(AUDIOS[0]);
    }
}
