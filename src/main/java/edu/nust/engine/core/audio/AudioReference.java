package edu.nust.engine.core.audio;

import java.net.URL;

/// **`INTERNAL`**
abstract sealed class AudioReference permits SoundEffectReference, MusicTrackReference
{
    private static long counter = 0;

    private final long id;
    private final URL location;

    AudioReference(URL location)
    {
        this.id = counter++;
        this.location = location;
    }

    /* GETTERS */

    long getId() { return id; }

    public URL getLocation() { return location; }

    /* UTILS */

    /// Gets the file name of the {@link  AudioReference}, e.g. `foo.wav`
    public String getFileName() { return getFileNameFromURL(location); }

    /// Gets the file name with extension from the URL, e.g. `foo.txt`
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
