package edu.nust.game.systems.audio;

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
            "music/MainMenu.wav",
            "music/levelScene.wav",
            // .
            // .
            // add more here
    };

    /* REFERENCES */

    public static Optional<SoundEffectReference> testSoundRef() { return manager.getSoundEffectByName("testSound.wav"); }

    public static Optional<MusicTrackReference> backgroundMusicRef() { return manager.getMusicTrackByName("background.wav"); }

    public static Optional<MusicTrackReference> mainMenuMusicRef() { return manager.getMusicTrackByName("MainMenu.wav"); }

    public static Optional<MusicTrackReference> levelSceneMusicRef() { return manager.getMusicTrackByName("levelScene.wav"); }

    /* SETUP */

    public static void setManager(GameWorld world) { Audios.manager = world.getAudioManager(); }

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
