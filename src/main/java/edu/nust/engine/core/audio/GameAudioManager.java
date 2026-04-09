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

    private final HashMap<String, AudioClipReference> loadedClipReferences = new HashMap<>();
    private final HashMap<String, LongAudioReference> loadedLongReferences = new HashMap<>();

    /* Audio Clip */

    /// Use for small clips such as Button Clicks etc.
    /// <br>
    /// <br>
    /// **`Use .wav instead of .mp3`**
    ///
    /// @param relativePath Relative path from {@code edu/nust/game/assets/audio/}
    ///
    /// @see AudioClipReference
    public @Nullable AudioClipReference loadAudioClip(String... relativePath)
    {
        String path = Resources.resolvePath(getAudioPath(relativePath));

        URL url;
        try { url = Resources.getResourceOrThrow(path); }
        catch (FileNotFoundException e)
        {
            LOGGER.error(false, "Attempted to load Audio Clip with invalid URL, Check if path exists {}", path);
            LOGGER.logException(e);
            return null;
        }

        String name = AudioClipReference.getFileNameFromURL(url);

        // if already loaded, return that
        if (loadedClipReferences.containsKey(name)) return loadedClipReferences.get(name);

        try
        {
            AudioClip audio = new AudioClip(url.toExternalForm());
            // create reference
            AudioClipReference ref = new AudioClipReference(url, audio);

            // store the reference
            loadedClipReferences.put(name, ref);

            LOGGER.info("Loaded Audio Clip \"{}\"", name);
            return ref;
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to load Audio Clip at \"{}\"", path);
            LOGGER.logException(e);
            return null;
        }
    }

    /// @param name The filename without extension
    public @Nullable AudioClipReference getClipWithName(String name)
    {
        for (AudioClipReference ref : loadedClipReferences.values())
            if (ref.getFileName().equals(name)) return ref;

        LOGGER.error(false, "Cannot find Audio Clip with name \"{}\"", name);
        return null;
    }

    /* LONG AUDIO */

    /// Use for small clips such as Button Clicks etc.
    /// <br>
    /// <br>
    /// **`Use .wav instead of .mp3`**
    ///
    /// @param relativePath Relative path from {@code edu/nust/game/assets/audio/}
    ///
    /// @see AudioClipReference
    public @Nullable LongAudioReference loadLongAudio(String... relativePath)
    {
        String path = Resources.resolvePath(getAudioPath(relativePath));

        URL url;
        try { url = Resources.getResourceOrThrow(path); }
        catch (FileNotFoundException e)
        {
            LOGGER.error(false, "Attempted to load Long Audio with invalid URL, Check if path exists {}", path);
            LOGGER.logException(e);
            return null;
        }

        String name = LongAudioReference.getFileNameFromURL(url);

        // if already loaded, return that
        if (loadedLongReferences.containsKey(name)) return loadedLongReferences.get(name);

        try
        {
            Media media = new Media(url.toExternalForm());
            // create reference
            LongAudioReference ref = new LongAudioReference(url, media);

            // store the reference
            loadedLongReferences.put(name, ref);

            LOGGER.info("Loaded Long Audio \"{}\"", name);
            return ref;
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to load Long Audio at \"{}\"", path);
            LOGGER.logException(e);
            return null;
        }
    }

    /// @param name The filename without extension
    public @Nullable LongAudioReference getLongAudioWithName(String name)
    {
        for (LongAudioReference ref : loadedLongReferences.values())
            if (ref.getFileName().equals(name)) return ref;

        LOGGER.error(false, "Cannot find Long Audio with name \"{}\"", name);
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
