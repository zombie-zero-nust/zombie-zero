package edu.nust.engine.core.audio;

import javafx.scene.media.AudioClip;

import java.net.URL;

/// Plays immediately, use for small clips such as Button Clicks etc.
/// <br>
/// **`Use .wav instead of .mp3`**
public final class AudioClipReference
{
    private static long counter = 0;

    private final long id;
    private final URL location;
    private final AudioClip clip;

    // can only be called in this package
    AudioClipReference(URL location, AudioClip clip)
    {
        this.id = counter++;
        this.location = location;
        this.clip = clip;
    }

    /* GETTERS & SETTERS */

    // can only be called in this package
    long getId() { return id; }

    AudioClip getClip() { return clip; }

    public URL getLocation() { return location; }

    /* AUDIO */

    public void play() { clip.play(); }

    public void play(boolean stopExisting)
    {
        if (stopExisting) clip.stop();
        clip.play();
    }

    /// Stops all running clips
    public void stopAll() { clip.stop(); }

    /* UTILITIES */

    /// Gets the filename with extension
    public String getFileName() { return getFileNameFromURL(location); }

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
        AudioClipReference that = (AudioClipReference) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Long.hashCode(id); }
}
