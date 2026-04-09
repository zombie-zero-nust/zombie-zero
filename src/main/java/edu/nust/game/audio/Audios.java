package edu.nust.game.audio;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.audio.SoundEffectReference;
import edu.nust.engine.core.audio.GameAudioManager;
import edu.nust.engine.core.audio.MusicTrackReference;

import java.util.function.Consumer;

public class Audios
{
    private static GameAudioManager manager;

    private static final String[] AUDIO_CLIPS = {  //
            "testEffect.wav",
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

    /* REFERENCES */

    public static SoundEffectReference testAudioClipRef() { return manager.getSoundEffectByName(AUDIO_CLIPS[0]); }

    public static MusicTrackReference testLongAudioRef() { return manager.getMusicTrackByName(LONG_AUDIOS[0]); }

    /* SETUP */

    public static void setManagerFromWorld(GameWorld world) { Audios.manager = world.getAudioManager(); }

    public static void forEachSoundEffect(Consumer<String> action)
    {
        for (String audio : AUDIO_CLIPS)
            action.accept(audio);
    }

    public static void forEachMusicTrack(Consumer<String> action)
    {
        for (String audio : LONG_AUDIOS)
            action.accept(audio);
    }
}
