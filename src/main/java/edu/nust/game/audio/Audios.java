package edu.nust.game.audio;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.audio.AudioClipReference;
import edu.nust.engine.core.audio.GameAudioManager;
import edu.nust.engine.core.audio.LongAudioReference;

import java.util.function.Consumer;

public class Audios
{
    private static GameAudioManager manager;

    private static final String[] AUDIO_CLIPS = {  //
            "testAudio.wav",
            // .
            // .
            // add more here
    };

    private static final String[] LONG_AUDIOS = {  //
            "testMusic.wav",
            // .
            // .
            // add more here
    };

    /* SETUP */

    public static void setManagerFromWorld(GameWorld world) { Audios.manager = world.getAudioManager(); }

    public static void forEachClip(Consumer<String> action) { for (String audio : AUDIO_CLIPS) action.accept(audio); }

    public static void forEachLong(Consumer<String> action) { for (String audio : LONG_AUDIOS) action.accept(audio); }

    /* REFERENCES */

    public static AudioClipReference testAudioClipRef() { return manager.getClipWithName(AUDIO_CLIPS[0]); }

    public static LongAudioReference testLongAudioRef() { return manager.getLongAudioWithName(LONG_AUDIOS[0]); }
}
