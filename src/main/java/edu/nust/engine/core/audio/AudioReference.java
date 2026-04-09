package edu.nust.engine.core.audio;

import javafx.scene.media.AudioClip;

import java.net.URL;

public final class AudioReference
{
    private static long counter = 0;

    private final long id;
    private final URL location;
    private final AudioClip clip;

    // can only be called in this package
    AudioReference(URL location, AudioClip clip)
    {
        this.id = counter++;
        this.location = location;
        this.clip = clip;
    }

    /* GETTERS & SETTERS */

    // can only be called in this package
    long getId() { return id; }

    public AudioClip getClip() { return clip; }

    public URL getLocation() { return location; }

    /* UTILITIES */

    /// Gets the filename with extension
    public String getName() { return getFileNameFromURL(location); }

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

    public boolean equals(AudioReference other)
    {
        return this.id == other.id;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(id);
    }
}
