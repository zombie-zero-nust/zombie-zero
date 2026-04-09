package edu.nust.engine.core.audio;

import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import javafx.scene.media.AudioClip;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;

public final class GameAudioManager
{
    private static final GameLogger LOGGER = GameLogger.getLogger(GameAudioManager.class);

    private final HashMap<String, AudioReference> loadedSounds = new HashMap<>();

    /* LOADER */

    /// Relative path from {@code edu/nust/game/assets/audio/}
    public @Nullable AudioReference loadClip(String... relativePath)
    {
        String path = Resources.resolvePath(getAudioPath(relativePath));

        URL url;
        try { url = Resources.getResourceOrThrow(path); }
        catch (FileNotFoundException e)
        {
            LOGGER.error(false, "Attempted to load sound with invalid URL, Check if path exists {}", path);
            LOGGER.logException(e);
            return null;
        }

        String name = AudioReference.getFileNameFromURL(url);

        // if already loaded, return that
        if (loadedSounds.containsKey(name)) return loadedSounds.get(name);

        try
        {
            AudioClip audio = new AudioClip(url.toExternalForm());
            // create reference
            AudioReference ref = new AudioReference(url, audio);

            // store the reference
            loadedSounds.put(name, ref);

            LOGGER.info("Loaded sound \"{}\"", name);
            return ref;
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to load sound at \"{}\"", path);
            LOGGER.logException(e);
            return null;
        }
    }

    /// @param name The filename without extension
    public @Nullable AudioReference getWithName(String name)
    {
        for (AudioReference ref : loadedSounds.values())
            if (ref.getName().equals(name)) return ref;

        LOGGER.error(false, "Cannot find Audio with name \"{}\"", name);
        return null;
    }

    /* PLAY & STOP */

    public void play(AudioReference ref)
    {
        if (ref == null) return;
        ref.getClip().play();
    }

    public void playLooping(AudioReference ref)
    {
        if (ref == null) return;
        ref.getClip().setCycleCount(AudioClip.INDEFINITE);
        ref.getClip().play();
    }

    public void stop(AudioReference ref)
    {
        if (ref == null) return;
        stopAndDispose(ref);
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

    private void stopAndDispose(AudioReference ref)
    {
        if (ref == null) return;
        ref.getClip().stop();
    }
}
