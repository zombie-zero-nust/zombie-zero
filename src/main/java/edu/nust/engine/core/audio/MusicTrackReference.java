package edu.nust.engine.core.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/// Does not play immediately, use for long sounds, such as background music etc.
/// <br>
/// **`Use .wav instead of .mp3`**
public final class MusicTrackReference
{
    private static long counter = 0;

    private final long id;
    private final URL location;
    private final Media media;
    private final MediaPlayer player;

    // can only be called in this package
    MusicTrackReference(URL location, Media media)
    {
        this.id = counter++;
        this.location = location;
        this.media = media;
        this.player = new MediaPlayer(this.media);
    }

    /* GETTERS & SETTERS */

    // can only be called in this package
    long getId() { return id; }

    Media getMedia() { return media; }

    public URL getLocation() { return location; }

    /* AUDIO */

    public void play()
    {
        player.seek(player.getStartTime());
        player.play();
    }

    public void play(boolean stopExisting)
    {
        if (stopExisting) stop();
        play();
    }

    /// Stops all running clips
    public void stop() { player.stop(); }

    public void pause() { player.pause(); }

    public void resume() { player.play(); }

    public void togglePause()
    {
        if (player.getStatus() == MediaPlayer.Status.PLAYING) player.pause();
        else player.play();
    }

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
        MusicTrackReference that = (MusicTrackReference) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Long.hashCode(id); }
}
