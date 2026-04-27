package edu.nust.engine.core.audio;

import edu.nust.engine.core.files.URLUtils;

import java.net.URL;

/// **`INTERNAL`** Base class for all audio references. Subclasses represent specific audio types and provide playback
/// control.
/// <br><br>
/// Use {@link SoundEffectReference} for short one-shot sounds, and {@link MusicTrackReference} for background music.
/// <br><br>
/// Instances are created and managed by {@link GameAudioManager}; do not instantiate directly.
abstract sealed class AudioReference permits SoundEffectReference, MusicTrackReference
{
    private static long counter = 0;

    private final long id;
    private final URL location;

    /// **`INTERNAL`** Creates a new {@link AudioReference} with the given location.
    ///
    /// @param location The URL of the audio file
    AudioReference(URL location)
    {
        this.id = counter++;
        this.location = location;
    }

    /* GETTERS */

    /// **`INTERNAL`** Returns the unique ID of this {@link AudioReference}.
    long getId() { return id; }

    /// Gets the URL location of the audio file.
    ///
    /// @return The URL of the audio file
    public URL getLocation() { return location; }

    /* PATH & METADATA */

    /// The Path of this {@code AudioReference}.
    public String getPath() { return location.getPath(); }

    /// Returns the file extension of this audio file without the leading dot, e.g. {@code "wav"} or {@code "mp3"}.
    /// Returns an empty string if no extension is present.
    public String getExtension()
    {
        String name = getFileName();
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : "";
    }

    /// Checks if this is same as the given {@code AudioReference}
    public boolean isSame(AudioReference other)
    {
        return location.toExternalForm().equals(other.getLocation().toExternalForm());
    }

    /* INTERNAL */

    /// **`INTERNAL`** Applies the manager's effective global volume to this reference's playback volume. Called by
    /// {@link GameAudioManager} whenever the global volume or mute state changes. Implementations must multiply their
    /// {@code userVolume} by {@code globalVolume} and apply it to the clip.
    ///
    /// @param globalVolume The new global volume multiplier, clamped between {@code 0.0} and {@code 1.0}
    abstract void applyGlobalVolumeToSelf(double globalVolume);

    /* UTILITIES */

    /// Gets the filename with extension of this audio file, e.g. {@code foo.wav}.
    ///
    /// @return The filename with extension
    public String getFileName() { return URLUtils.getFileNameFromURL(location); }

    /* OVERRIDES */

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AudioReference that = (AudioReference) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Long.hashCode(id); }
}
