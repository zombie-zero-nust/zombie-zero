package edu.nust.engine.core.audio;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;

public final class GameAudioManager
{
    private static final GameLogger LOGGER = GameLogger.getLogger(GameAudioManager.class);

    private final HashMap<String, SoundEffectReference> loadedSoundEffects = new HashMap<>();
    private final HashMap<String, MusicTrackReference> loadedMusicTracks = new HashMap<>();

    /* SOUND EFFECT */

    /// Use for small clips such as Button Clicks etc.
    /// <br>
    /// <br>
    /// **`Use .wav instead of .mp3`**
    ///
    /// @param relativePath Relative path from {@code edu/nust/game/assets/audio/}
    ///
    /// @see SoundEffectReference
    public @Nullable SoundEffectReference loadSoundEffect(String... relativePath)
    {
        String path = Resources.resolvePath(getAudioPath(relativePath));

        URL url;
        try { url = Resources.getResourceOrThrow(path); }
        catch (FileNotFoundException e)
        {
            LOGGER.error(false, "Attempted to load Sound Effect with invalid URL, Check if path exists {}", path);
            LOGGER.logException(e);
            return null;
        }

        String name = SoundEffectReference.getFileNameFromURL(url);

        // if already loaded, return that
        if (loadedSoundEffects.containsKey(name)) return loadedSoundEffects.get(name);

        try
        {
            AudioClip audio = new AudioClip(url.toExternalForm());
            // create reference
            SoundEffectReference ref = new SoundEffectReference(url, audio);

            // store the reference
            loadedSoundEffects.put(name, ref);

            LOGGER.info("Loaded Sound Effect \"{}\"", name);
            return ref;
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to load Sound Effect at \"{}\"", path);
            LOGGER.logException(e);
            return null;
        }
    }

    /// @param name The filename without extension
    public @Nullable SoundEffectReference getSoundEffectByName(String name)
    {
        for (SoundEffectReference ref : loadedSoundEffects.values())
            if (ref.getFileName().equals(name)) return ref;

        LOGGER.error(false, "Cannot find Sound Effect with name \"{}\"", name);
        return null;
    }

    /* MUSIC TRACK */

    /// Use for small clips such as Button Clicks etc.
    /// <br>
    /// <br>
    /// **`Use .wav instead of .mp3`**
    ///
    /// @param relativePath Relative path from {@code edu/nust/game/assets/audio/}
    ///
    /// @see SoundEffectReference
    public @Nullable MusicTrackReference loadMusicTrack(String... relativePath)
    {
        String path = Resources.resolvePath(getAudioPath(relativePath));

        URL url;
        try { url = Resources.getResourceOrThrow(path); }
        catch (FileNotFoundException e)
        {
            LOGGER.error(false, "Attempted to load Music Track with invalid URL, Check if path exists {}", path);
            LOGGER.logException(e);
            return null;
        }

        String name = MusicTrackReference.getFileNameFromURL(url);

        // if already loaded, return that
        if (loadedMusicTracks.containsKey(name)) return loadedMusicTracks.get(name);

        try
        {
            Media media = new Media(url.toExternalForm());
            // create reference
            MusicTrackReference ref = new MusicTrackReference(url, media);

            // store the reference
            loadedMusicTracks.put(name, ref);

            LOGGER.info("Loaded Music Track \"{}\"", name);
            return ref;
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to load Music Track at \"{}\"", path);
            LOGGER.logException(e);
            return null;
        }
    }

    /// @param name The filename without extension
    public @Nullable MusicTrackReference getMusicTrackByName(String name)
    {
        for (MusicTrackReference ref : loadedMusicTracks.values())
            if (ref.getFileName().equals(name)) return ref;

        LOGGER.error(false, "Cannot find Music Track with name \"{}\"", name);
        return null;
    }

    /* HELPERS */

    /// Gets the path relative to {@code `edu/nust/game/assets/audio/`}
    private String[] getAudioPath(String... path)
    {
        String[] fullPath = new String[2 + path.length];
        fullPath[0] = "assets";
        fullPath[1] = "audio";
        System.arraycopy(path, 0, fullPath, 2, path.length);
        return fullPath;
    }
}
