package edu.nust.game.audio;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.audio.GameAudioManager;
import edu.nust.engine.core.audio.MusicTrackReference;
import edu.nust.engine.core.audio.SoundEffectReference;

import java.util.Optional;
import java.util.function.Consumer;

public class Audios
{
    private static GameAudioManager manager;

    private static final String[] SOUND_EFFECTS = {  //
            "testSound.wav",
            // .
            // .
            // add more here
    };

    private static final String[] MUSIC_TRACKS = {  //
            "music/background.wav",
            // .
            // .
            // add more here
    };

    /* REFERENCES */

    public static Optional<SoundEffectReference> testSoundRef() { return manager.getSoundEffectByName(SOUND_EFFECTS[0]); }

    public static Optional<MusicTrackReference> backgroundMusicRef() { return manager.getMusicTrackByName(MUSIC_TRACKS[0]); }

    /* SETUP */

    public static void setManagerFromWorld(GameWorld world) { Audios.manager = world.getAudioManager(); }

    public static void forEachSoundEffect(Consumer<String> action)
    {
        for (String audio : SOUND_EFFECTS)
            action.accept(audio);
    }

    public static void forEachMusicTrack(Consumer<String> action)
    {
        for (String audio : MUSIC_TRACKS)
            action.accept(audio);
    }
}
