package edu.nust.engine.core.audio;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Manages all in-game audio. Linked to the {@link GameWorld}. Loaded Clips are only unloaded when game exits.
 * {@code MusicTracks} and {@code AudioClips}, <b>{@code persists}</b> when switching scenes.
 * <br><br>
 * Handles two categories of audio:
 * <ul>
 *     <li>{@link SoundEffectReference} -> short, such as button clicks or hit effects</li>
 *     <li>{@link MusicTrackReference} -> long, such as background music tracks</li>
 * </ul>
 * All loaded references are cached by filename; loading the same file twice returns the existing reference.
 * <br><br>
 * All paths are resolved relative to {@code edu/nust/game/assets/audio/}.
 * <br><br>
 *
 * @see GameWorld#getAudioManager()
 */
public final class GameAudioManager
{
    private static final GameLogger LOGGER = GameLogger.getLogger(GameAudioManager.class);

    private final HashMap<String, SoundEffectReference> loadedSoundEffects = new HashMap<>();
    private final HashMap<String, MusicTrackReference> loadedMusicTracks = new HashMap<>();

    /* SOUND EFFECT */

    /**
     * Loads a {@link SoundEffectReference} from the given path relative to {@code edu/nust/game/assets/audio/}. If the
     * file has already been loaded, the cached reference is returned.
     * <br><br>
     * <b>{@code Use .wav instead of .mp3}</b>
     *
     * @param relativePath Path relative to {@code edu/nust/game/assets/audio/} split, e.g. {@code ("sfx", "click.wav")}
     *                     for {@code edu/nust/game/assets/audio/sfx/click.wav}
     *
     * @return The loaded {@link SoundEffectReference}, or {@code null} if the file could not be found or loaded
     */
    @Nullable
    public SoundEffectReference loadSoundEffect(String... relativePath)
    {
        return tryLoadAudioReference(
                SoundEffectReference.class,
                url -> new SoundEffectReference(url, new AudioClip(url.toExternalForm())),
                loadedSoundEffects,
                relativePath
        );
    }

    /**
     * Retrieves a loaded {@link SoundEffectReference} by filename (with extension).
     *
     * @param name The filename with extension, e.g. {@code "click.wav"}
     *
     * @return The {@link SoundEffectReference}, or {@code null} if not found
     */
    @Nullable
    public SoundEffectReference getSoundEffectByName(String name)
    {
        return getReferenceByName(SoundEffectReference.class, name, loadedSoundEffects);
    }

    /* MUSIC TRACK */

    /**
     * Loads a {@link MusicTrackReference} from the given path relative to {@code edu/nust/game/assets/audio/}. If the
     * file has already been loaded, the cached reference is returned.
     * <br><br>
     * <b>{@code Use .wav instead of .mp3}</b>
     *
     * @param relativePath Path relative to {@code edu/nust/game/assets/audio/} split, e.g.
     *                     {@code ("sfx", "bg_music.wav")} for {@code edu/nust/game/assets/audio/sfx/click.wav}
     *
     * @return The loaded {@link MusicTrackReference}, or {@code null} if the file could not be found or loaded
     */
    @Nullable
    public MusicTrackReference loadMusicTrack(String... relativePath)
    {
        return tryLoadAudioReference(
                MusicTrackReference.class,
                url -> new MusicTrackReference(url, new Media(url.toExternalForm())),
                loadedMusicTracks,
                relativePath
        );
    }

    /**
     * Retrieves a loaded {@link MusicTrackReference} by filename (with extension).
     *
     * @param name The filename with extension, e.g. {@code "bg_music.wav"}
     *
     * @return The {@link MusicTrackReference}, or {@code null} if not found
     */
    @Nullable
    public MusicTrackReference getMusicTrackByName(String name)
    {
        return getReferenceByName(MusicTrackReference.class, name, loadedMusicTracks);
    }

    /* HELPERS */

    /// **`INTERNAL`** Adds {@code assets/audio} to the given path.
    private String[] getAudioPath(String... path)
    {
        String[] fullPath = new String[2 + path.length];
        fullPath[0] = "assets";
        fullPath[1] = "audio";
        System.arraycopy(path, 0, fullPath, 2, path.length);
        return fullPath;
    }

    /**
     * **`INTERNAL`**
     *
     * @param caller       The {@link AudioReference} subclass; used for <b>logging</b>
     * @param onSuccess    Function to call On Successful loading audio from given path ({@link URL})
     * @param cachedList   The cache map to store the reference
     * @param relativePath Path relative to {@code edu/nust/game/assets/audio/}
     *
     * @return The reference of type {@code T}, or {@code null} on failure
     */
    @Nullable
    private <T extends AudioReference> T tryLoadAudioReference(Class<T> caller, Function<URL, T> onSuccess, HashMap<String, T> cachedList, String... relativePath)
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

        String name = AudioReference.getFileNameFromURL(url);

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

    /**
     * **`INTERNAL`**
     *
     * @param caller     The {@link AudioReference} subclass; used for <b>logging</b>
     * @param name       The filename with extension
     * @param cachedList The cache map
     *
     * @return The reference of type {@link T}, or {@code null} if not found
     */
    @Nullable
    private <T extends AudioReference> T getReferenceByName(Class<T> caller, String name, HashMap<String, T> cachedList)
    {
        for (T ref : cachedList.values())
            if (ref.getFileName().equals(name)) return ref;

        LOGGER.error(false, "Cannot find [{}] with name \"{}\"", caller.getSimpleName(), name);
        return null;
    }
}
