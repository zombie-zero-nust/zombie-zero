package edu.nust.engine.core.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/// Does not play immediately, use for long sounds, such as background music etc.
/// <br>
/// **`Use .wav instead of .mp3`**
public final class MusicTrackReference extends AudioReference
{
    private final MediaPlayer player;

    MusicTrackReference(URL location, Media media)
    {
        super(location);
        this.player = new MediaPlayer(media);
    }

    /* PLAYERS */

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

    public void stop() { player.stop(); }

    public void pause() { player.pause(); }

    public void resume() { player.play(); }

    public void togglePause()
    {
        if (isPlaying()) player.pause();
        else player.play();
    }

    /* STATE */

    private boolean isPlaying() { return player.getStatus() == MediaPlayer.Status.PLAYING; }
}
