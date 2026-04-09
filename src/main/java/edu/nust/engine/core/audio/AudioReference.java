package edu.nust.engine.core.audio;

import javafx.scene.media.Media;

public final class AudioReference
{
    private static long counter = 0;

    private final long id;
    private final String location;
    private final Media media;

    // can only be called in this package
    AudioReference(String location, Media media)
    {
        this.id = counter++;
        this.location = location;
        this.media = media;
    }

    /* GETTERS */

    // can only be called in this package
    long getId() { return id; }

    public String getLocation() { return location; }

    public Media getMedia() { return media; }

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
