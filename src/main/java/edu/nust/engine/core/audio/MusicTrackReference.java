package edu.nust.engine.core.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/// Use for long-running audio such as background music.
/// <br><br>
/// Unlike {@link SoundEffectReference}, playback may **`NOT`** begin automatically, but will begin with a short delay;
/// call {@link MusicTrackReference#play()} explicitly. Only one instance of the track plays at a time.
/// <br><br>
/// Do not instantiate directly; use {@link GameAudioManager#loadMusicTrack(String...)}} instead.
/// <br><br>
/// **`Use .wav instead of .mp3`**
///
/// @see SoundEffectReference
/// @see GameAudioManager
public final class MusicTrackReference extends AudioReference
{
    private final MediaPlayer player;

    /// **`INTERNAL`**
    MusicTrackReference(URL location, Media media)
    {
        super(location);
        this.player = new MediaPlayer(media);
    }

    /* PLAYBACK */

    /// Plays the track from the beginning. If it is already playing, it restarts from the start.
    public void play()
    {
        player.seek(player.getStartTime());
        player.play();
    }

    public void stop() { player.stop(); }

    /// Pauses playback at the current position.
    public void pause() { player.pause(); }

    /// Resumes playback from the current position. If the track is not currently paused, then just plays instead
    public void resume() { player.play(); }

    /// Toggles between paused and playing. If currently playing, pauses; otherwise resumes.
    public void togglePause()
    {
        if (isPlaying()) pause();
        else resume();
    }

    /* STATE */

    /// Returns whether the track is currently playing.
    ///
    /// @return {@code true} if the track is playing, {@code false} otherwise
    private boolean isPlaying() { return player.getStatus() == MediaPlayer.Status.PLAYING; }
}
