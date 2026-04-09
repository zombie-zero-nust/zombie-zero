package edu.nust.engine.core.audio;

import javafx.scene.media.MediaPlayer;

import java.net.URL;

public final class AudioReference
{
    private static long counter = 0;

    private final long id;
    private final URL location;
    private final MediaPlayer player;

    // can only be called in this package
    AudioReference(URL location, MediaPlayer player)
    {
        this.id = counter++;
        this.location = location;
        this.player = player;
    }

    /* GETTERS */

    // can only be called in this package
    long getId() { return id; }

    MediaPlayer getPlayer() { return player; }

    public URL getLocation() { return location; }

    /* UTILITIES */

    /// Gets the filename without extension
    public String getName() { return getFileNameFromURL(location); }

    public static String getFileNameFromURL(URL url)
    {
        String path = url.getPath();
        String filename = path.substring(path.lastIndexOf('/') + 1);
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
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
    public int hashCode()
    {
        return Long.hashCode(id);
    }
}
