package edu.nust.engine.core.audio;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.HashMap;
import java.util.function.Function;

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
    @Nullable
    public SoundEffectReference loadSoundEffect(String... relativePath)
    {
        return (SoundEffectReference) tryLoadAudioReference(
                SoundEffectReference.class,
                url -> new SoundEffectReference(url, new AudioClip(url.toExternalForm())),
                loadedSoundEffects,
                relativePath
        );
    }

    /// @param name The filename without extension
    @Nullable
    public SoundEffectReference getSoundEffectByName(String name)
    {
        return (SoundEffectReference) getReferenceByName(SoundEffectReference.class, name, loadedSoundEffects);
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
    @Nullable
    public MusicTrackReference loadMusicTrack(String... relativePath)
    {
        return (MusicTrackReference) tryLoadAudioReference(
                MusicTrackReference.class,
                url -> new MusicTrackReference(url, new Media(url.toExternalForm())),
                loadedMusicTracks,
                relativePath
        );
    }

    /// @param name The filename without extension
    @Nullable
    public MusicTrackReference getMusicTrackByName(String name)
    {
        return (MusicTrackReference) getReferenceByName(MusicTrackReference.class, name, loadedMusicTracks);
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

    @Nullable
    private <T extends AudioReference> AudioReference tryLoadAudioReference(Class<T> caller, Function<URL, T> onSuccess, HashMap<String, T> cachedList, String... relativePath)
    {
        String path = Resources.resolvePath(getAudioPath(relativePath));

        URL url;
        try { url = Resources.getResourceOrThrow(path); }
        catch (Exception e)
        {
            LOGGER.error(
                    false,
                    "Attempted to load [{}] with invalid URL, Check if path exists {}",
                    caller.getSimpleName(),
                    path
            );
            LOGGER.logException(e);
            return null;
        }

        String name = MusicTrackReference.getFileNameFromURL(url);

        // if already loaded, return that
        if (cachedList.containsKey(name)) return cachedList.get(name);

        try
        {
            T ref = onSuccess.apply(url);

            // store the reference
            cachedList.put(name, ref);

            LOGGER.info("Loaded [{}] \"{}\"", caller.getSimpleName(), name);
            return ref;
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to load [{}] at \"{}\"", caller.getSimpleName(), path);
            LOGGER.logException(e);
            return null;
        }
    }

    @Nullable
    private <T extends AudioReference> AudioReference getReferenceByName(Class<T> caller, String name, HashMap<String, T> cachedList)
    {
        for (AudioReference ref : cachedList.values())
            if (ref.getFileName().equals(name)) return ref;

        LOGGER.error(false, "Cannot find [{}] with name \"{}\"", caller.getSimpleName(), name);
        return null;
    }
}
