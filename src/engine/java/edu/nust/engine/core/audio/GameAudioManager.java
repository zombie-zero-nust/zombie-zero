package edu.nust.engine.core.audio;

import edu.nust.engine.core.GameWorld;
import edu.nust.engine.core.files.URLUtils;
import edu.nust.engine.logger.GameLogger;
import edu.nust.engine.resources.Resources;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.jetbrains.annotations.Nullable;

import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Manages all in-game audio. Linked to the {@link GameWorld}. Loaded Clips are only unloaded when game exits.
 * {@code MusicTracks} and {@code AudioClips}, <b>{@code persists}</b> when switching scenes.
 * <br><br>
 * Handles two categories of audio:
 * <ul>
 *     <li>{@link SoundEffectReference} -> short, such as button clicks or hit effects</li>
 *     <li>{@link MusicTrackReference} -> long, such as background music tracks</li>
 * </ul>
 * All loaded references are cached by full relative path; loading the same asset twice returns the existing reference.
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

    private double globalVolume = 1.0;
    private double volumeBeforeMute = 1.0;
    private boolean muted = false;

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
    public Optional<SoundEffectReference> loadSoundEffect(String... relativePath)
    {
        return tryLoadAudioReference(
                SoundEffectReference.class,
                SoundEffectReference::new,
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
    public Optional<SoundEffectReference> getSoundEffectByName(String name)
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
     * @return The optional loaded {@link MusicTrackReference}, or {@link Optional#empty()} if the file could not be
     * found or loaded
     */
    public Optional<MusicTrackReference> loadMusicTrack(String... relativePath)
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
     * @return The optional loaded {@link MusicTrackReference}, or {@link Optional#empty()} if the file could not be
     * found or loaded
     */
    public Optional<MusicTrackReference> getMusicTrackByName(String name)
    {
        return getReferenceByName(MusicTrackReference.class, name, loadedMusicTracks);
    }

    /* CACHE QUERIES */

    /**
     * Returns whether a sound effect with the given filename has been loaded into the cache.
     *
     * @param filename The filename with extension, e.g. {@code "click.wav"}
     *
     * @return {@code true} if the sound effect is loaded
     */
    public boolean isSoundEffectLoaded(String filename)
    {
        return loadedSoundEffects.values().stream().anyMatch(ref -> ref.getFileName().equals(filename));
    }

    /**
     * Returns whether a music track with the given filename has been loaded into the cache.
     *
     * @param filename The filename with extension, e.g. {@code "bg_music.wav"}
     *
     * @return {@code true} if the music track is loaded
     */
    public boolean isMusicTrackLoaded(String filename)
    {
        return loadedMusicTracks.values().stream().anyMatch(ref -> ref.getFileName().equals(filename));
    }

    /* UNLOAD */

    /**
     * Removes the sound effect with the given filename from the cache and notifies its listeners. If no such effect is
     * loaded, this is a no-op.
     *
     * @param filename The filename with extension to unload
     */
    public void unloadSoundEffect(String filename)
    {
        SoundEffectReference ref = removeReferenceByFilename(filename, loadedSoundEffects);
        if (ref != null)
        {
            LOGGER.info("Unloaded [SoundEffectReference] \"{}\"", ref.getFileName());
        }
    }

    /**
     * Removes the music track with the given filename from the cache, notifies its listeners, and disposes the
     * underlying {@link MediaPlayer}. If no such track is loaded, this is a no-op.
     *
     * @param filename The filename with extension to unload
     */
    public void unloadMusicTrack(String filename)
    {
        MusicTrackReference ref = removeReferenceByFilename(filename, loadedMusicTracks);
        if (ref != null)
        {
            ref.dispose();
            LOGGER.info("Unloaded [MusicTrackReference] \"{}\"", filename);
        }
    }

    /**
     * Removes and releases all loaded sound effects.
     */
    public void unloadAllSoundEffects()
    {
        loadedSoundEffects.clear();
        LOGGER.info("Unloaded all SoundEffectReferences");
    }

    /**
     * Removes and releases loaded sound effects whose cache key does not match the given predicate.
     *
     * @param keepLoaded {@link Predicate} that should returns {@code true} for cache keys that should remain loaded
     */
    public void unloadSoundEffectsExcept(Predicate<String> keepLoaded)
    {
        Iterator<Map.Entry<String, SoundEffectReference>> iterator = loadedSoundEffects.entrySet().iterator();
        int unloadedCount = 0;

        while (iterator.hasNext())
        {
            Map.Entry<String, SoundEffectReference> entry = iterator.next();
            if (keepLoaded.test(entry.getKey())) continue;

            iterator.remove();
            unloadedCount++;
        }

        LOGGER.info("Unloaded {} SoundEffectReferences", unloadedCount);
    }

    /**
     * Removes and disposes all loaded music tracks. underlying {@link MediaPlayer} is disposed.
     */
    public void unloadAllMusicTracks()
    {
        for (MusicTrackReference ref : loadedMusicTracks.values())
        {
            ref.dispose();
        }
        loadedMusicTracks.clear();
        LOGGER.info("Unloaded all MusicTrackReferences");
    }

    /**
     * Unloads all audio resources; equivalent to calling {@link #unloadAllSoundEffects()} and
     * {@link #unloadAllMusicTracks()}.
     */
    public void unloadAll()
    {
        unloadAllSoundEffects();
        unloadAllMusicTracks();
    }

    /* LISTING */

    /**
     * Returns a list of filenames for all currently loaded sound effects.
     *
     * @return A new {@link List} of filenames; never {@code null}
     */
    public List<String> listLoadedSoundEffectNames() { return new ArrayList<>(loadedSoundEffects.keySet()); }

    /**
     * Returns a list of filenames for all currently loaded music tracks.
     *
     * @return A new {@link List} of filenames; never {@code null}
     */
    public List<String> listLoadedMusicTrackNames() { return new ArrayList<>(loadedMusicTracks.keySet()); }

    /* OPTIONAL GETTERS */

    /**
     * Returns the loaded {@link SoundEffectReference} for the given filename.
     * <br><br>
     * Does not log an error if the effect is not found.
     *
     * @param filename The filename with extension, e.g. {@code "click.wav"}
     *
     * @return The {@link SoundEffectReference}, or {@code null} if not loaded
     */
    public @Nullable SoundEffectReference tryGetSoundEffect(String filename)
    {
        return loadedSoundEffects.values()
                .stream()
                .filter(ref -> ref.getFileName().equals(filename))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the loaded {@link MusicTrackReference} for the given filename.
     * <br><br>
     * Does not log an error if the effect is not found.
     *
     * @param filename The filename with extension, e.g. {@code "click.wav"}
     *
     * @return The {@link MusicTrackReference}, or {@code null} if not loaded
     */
    public @Nullable MusicTrackReference tryGetMusicTrack(String filename)
    {
        return loadedMusicTracks.values()
                .stream()
                .filter(ref -> ref.getFileName().equals(filename))
                .findFirst()
                .orElse(null);
    }

    /* COUNTS */

    /**
     * Returns the number of sound effects currently held in the cache.
     *
     * @return The loaded sound effect count
     */
    public int getLoadedSoundEffectCount() { return loadedSoundEffects.size(); }

    /**
     * Returns the total number of currently active (i.e. playing) {@link Clip} instances across {@code ALL} loaded
     * sound effects.
     *
     * @return Total active sound-effect clip count
     */
    public int getLoadedSoundEffectClipCount()
    {
        return loadedSoundEffects.values().stream().mapToInt(SoundEffectReference::getLoadedClipCount).sum();
    }

    /**
     * Returns the number of music tracks currently held in the cache.
     *
     * @return The loaded music track count
     */
    public int getLoadedMusicTrackCount() { return loadedMusicTracks.size(); }

    /* GLOBAL VOLUME */

    /**
     * Sets the master volume for all {@link AudioReference}. Individual {@link AudioReference} volumes are multiplied
     * by this value.
     *
     * @param volume Master volume between {@code 0.0} (silent) and {@code 1.0} (full); clamped automatically
     */
    public void setGlobalVolume(double volume)
    {
        this.globalVolume = Math.clamp(volume, 0.0, 1.0);
        if (muted) return; // mute is applied on top; don't propagate until unmuted
        applyGlobalVolumeToAll();
    }

    /// Returns the current master volume, unaffected by the mute state.
    public double getGlobalVolume() { return globalVolume; }

    /* MUTE */

    /**
     * Mutes all audio by setting the effective global volume to {@code 0.0}. Stores the current volume so it can be
     * restored by {@link #unmuteAll()}. Has no effect if already muted.
     */
    public void muteAll()
    {
        if (muted) return;
        volumeBeforeMute = globalVolume;
        muted = true;
        applyGlobalVolumeToAll();
    }

    /**
     * Restores audio to the volume that was active before {@link #muteAll()} was called. Has no effect if not muted.
     */
    public void unmuteAll()
    {
        if (!muted) return;
        muted = false;
        globalVolume = volumeBeforeMute;
        applyGlobalVolumeToAll();
    }

    /**
     * Returns whether all audio is currently muted.
     *
     * @return {@code true} if muted
     */
    public boolean isMuted() { return muted; }

    /* FADE */

    /**
     * Smoothly fades out the given music track over the specified duration. Delegates to
     * {@link MusicTrackReference#fadeOut(Duration)}.
     *
     * @param ref      The {@link MusicTrackReference} to fade out; must not be {@code null}
     * @param duration The fade duration; must not be {@code null}
     */
    public void fadeOutMusic(MusicTrackReference ref, Duration duration) { ref.fadeOut(duration); }

    /**
     * Smoothly fades in the given music track over the specified duration. Delegates to
     * {@link MusicTrackReference#fadeIn(Duration)}.
     *
     * @param ref      The {@link MusicTrackReference} to fade in; must not be {@code null}
     * @param duration The fade duration; must not be {@code null}
     */
    public void fadeInMusic(MusicTrackReference ref, Duration duration) { ref.fadeIn(duration); }

    /* STOP ALL */

    /**
     * Stops playback of all loaded music tracks, respecting any fade-on-stop configuration set on individual tracks.
     */
    public void stopAllMusic()
    {
        for (MusicTrackReference ref : loadedMusicTracks.values()) ref.stop();
    }

    /**
     * Stops all currently playing instances of all loaded sound effects.
     */
    public void stopAllSoundEffects()
    {
        for (SoundEffectReference ref : loadedSoundEffects.values()) ref.stopAll();
    }

    /* HELPERS */

    /// <b>{@code INTERNAL}</b> Adds {@code assets/audio} to the given path.
    private String[] getAudioPath(String... path)
    {
        String[] fullPath = new String[2 + path.length];
        fullPath[0] = "assets";
        fullPath[1] = "audio";
        System.arraycopy(path, 0, fullPath, 2, path.length);
        return fullPath;
    }

    /**
     * <b>{@code INTERNAL}</b>
     *
     * @param caller       The {@link AudioReference} subclass; used for <b>logging</b>
     * @param onSuccess    Function to call On Successful loading audio from given path ({@link URL})
     * @param cachedList   The cache map to store the reference
     * @param relativePath Path relative to {@code edu/nust/game/assets/audio/}
     *
     * @return The reference of type {@code T}, or {@code null} on failure
     */
    @Nullable
    private <T extends AudioReference> Optional<T> tryLoadAudioReference(Class<T> caller, Function<URL, T> onSuccess, HashMap<String, T> cachedList, String... relativePath)
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
            return Optional.empty();
        }

        String name = URLUtils.getFileNameFromURL(url);

        // if already loaded, return that
        if (cachedList.containsKey(name)) return Optional.of(cachedList.get(name));

        try
        {
            T ref = onSuccess.apply(url);

            // store the reference
            cachedList.put(cacheKey, ref);

            LOGGER.info("Loaded [{}] \"{}\"", caller.getSimpleName(), name);
            return Optional.of(ref);
        }
        catch (Exception e)
        {
            LOGGER.error(false, "Failed to load [{}] at \"{}\"", caller.getSimpleName(), path);
            LOGGER.logException(e);
            return Optional.empty();
        }
    }


    /// <b>{@code INTERNAL}</b>
    private <T extends AudioReference> @Nullable T removeReferenceByFilename(String filename, HashMap<String, T> cachedList)
    {
        Iterator<Map.Entry<String, T>> iterator = cachedList.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, T> entry = iterator.next();
            if (!entry.getValue().getFileName().equals(filename)) continue;

            iterator.remove();
            return entry.getValue();
        }
        return null;
    }

    /**
     * <b>{@code INTERNAL}</b>
     *
     * @param caller     The {@link AudioReference} subclass; used for <b>logging</b>
     * @param name       The filename with extension
     * @param cachedList The cache map
     *
     * @return The optional reference of type {@link T}, or {@link Optional#empty()} if the file could not be found or
     * loaded
     */
    private <T extends AudioReference> Optional<T> getReferenceByName(Class<T> caller, String name, HashMap<String, T> cachedList)
    {
        for (T ref : cachedList.values())
            if (ref.getFileName().equals(name)) return Optional.of(ref);

        LOGGER.error(false, "Cannot find [{}] with name \"{}\"", caller.getSimpleName(), name);
        return Optional.empty();
    }

    /// <b>{@code INTERNAL}</b> Propagates the effective global volume to all loaded references. When muted, applies
    /// {@code 0.0}; otherwise applies the configured {@link #globalVolume}.
    private void applyGlobalVolumeToAll()
    {
        double effective = muted ? 0.0 : globalVolume;
        for (SoundEffectReference ref : loadedSoundEffects.values()) ref.applyGlobalVolumeToSelf(effective);
        for (MusicTrackReference ref : loadedMusicTracks.values()) ref.applyGlobalVolumeToSelf(effective);
    }
}