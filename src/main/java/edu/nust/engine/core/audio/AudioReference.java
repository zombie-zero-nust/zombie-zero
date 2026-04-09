package edu.nust.engine.core.audio;

import java.net.URL;

/// **`INTERNAL`** Base class for all audio references. Subclasses represent specific audio types and provide playback control.
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

    /* UTILITIES */

    /// Gets the filename with extension of this audio file, e.g. {@code foo.wav}.
    ///
    /// @return The filename with extension
    public String getFileName() { return getFileNameFromURL(location); }

    /// Extracts the filename with extension from the given URL, e.g. {@code foo.wav}.
    ///
    /// @param url The URL to extract the filename from
    ///
    /// @return The filename with extension
    public static String getFileNameFromURL(URL url)
    {
        String path = url.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

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
