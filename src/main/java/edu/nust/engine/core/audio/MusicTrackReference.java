package edu.nust.engine.core.audio;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.function.Consumer;

/// Use for long-running audio such as background music.
/// <br><br>
/// Unlike {@link SoundEffectReference}, playback may <b>{@code NOT}</b> begin automatically, but will begin with a
/// short delay; call {@link MusicTrackReference#play()} explicitly. Only one instance of the track plays at a time.
/// <br><br>
/// Do not instantiate directly; use {@link GameAudioManager#loadMusicTrack(String...)}} instead.
/// <br><br>
/// <b>{@code Use .wav instead of .mp3}</b>
///
/// @see SoundEffectReference
/// @see GameAudioManager
public final class MusicTrackReference extends AudioReference
{
    private final MediaPlayer player;
    private double userVolume = 1.0;
    private boolean fadeOnStop = false;
    private Duration fadeOnStopDuration = Duration.ZERO;

    /// <b>{@code INTERNAL}</b> Use {@link GameAudioManager#loadMusicTrack(String...)} instead
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

    /// Stops playback. If a fade-on-stop has been configured via {@link #setFadeOnStop(boolean, Duration)}, the track
    /// fades out over the configured duration before stopping.
    public void stop()
    {
        if (fadeOnStop && !fadeOnStopDuration.equals(Duration.ZERO))
        {
            Timeline tl = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(player.volumeProperty(), player.getVolume())),
                    new KeyFrame(fadeOnStopDuration, new KeyValue(player.volumeProperty(), 0.0))
            );
            tl.setOnFinished(e -> {
                player.stop();
                player.setVolume(userVolume);
            });
            tl.play();
        }
        else
        {
            player.stop();
        }
    }

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
    public boolean isPlaying() { return player.getStatus() == MediaPlayer.Status.PLAYING; }

    /* PLAYBACK RATE */

    /// Sets the playback speed of this track. {@code 1.0} is normal speed; {@code 2.0} plays at double speed.
    ///
    /// @param rate Playback rate multiplier; clamped to a minimum of {@code 0.0}
    public void setPlaybackRate(double rate) { player.setRate(Math.max(0.0, rate)); }

    /// Returns the current playback rate.
    ///
    /// @return The current playback rate multiplier
    public double getPlaybackRate() { return player.getRate(); }

    /* LOOPING */

    /// Controls whether this track loops indefinitely after reaching the end.
    ///
    /// @param loop {@code true} to loop indefinitely; {@code false} to play once
    public void setLooping(boolean loop) { player.setCycleCount(loop ? MediaPlayer.INDEFINITE : 1); }

    /// Returns whether this track is set to loop indefinitely.
    ///
    /// @return {@code true} if the track loops
    public boolean isLooping() { return player.getCycleCount() == MediaPlayer.INDEFINITE; }

    /* POSITION */

    /// Returns the current playhead position within the track.
    ///
    /// @return The current position as a {@link Duration}
    public Duration getPlayheadPosition() { return player.getCurrentTime(); }

    /// Seeks the playhead to the given position within the track.
    ///
    /// @param position Target position; clamped by the underlying {@link MediaPlayer}
    public void seek(Duration position) { player.seek(position); }

    /// Returns the total duration of the media. May return {@link Duration#UNKNOWN} if the duration has not yet been
    /// determined (media not ready).
    ///
    /// @return Total media duration, or {@link Duration#UNKNOWN}
    public Duration getDuration() { return player.getTotalDuration(); }

    /* LOOP POINTS */

    /// Sets loop points inside the track so playback repeats between {@code start} and {@code end} indefinitely.
    /// Implicitly enables looping. Call {@link #clearLoopPoints()} to remove.
    ///
    /// @param start The loop start position
    /// @param end   The loop end position; must be after {@code start}
    public void setLoopBetween(Duration start, Duration end)
    {
        player.setStartTime(start);
        player.setStopTime(end);
        player.setCycleCount(MediaPlayer.INDEFINITE);
    }

    /// Removes any loop points set via {@link #setLoopBetween(Duration, Duration)}, restoring the full track duration.
    /// Does not change the cycle count.
    public void clearLoopPoints()
    {
        player.setStartTime(Duration.ZERO);
        player.setStopTime(player.getMedia().getDuration());
    }

    /* CALLBACKS */

    /// Registers a callback to be invoked when the track reaches the end of media.
    ///
    /// @param callback The {@link Runnable} to invoke; replaces any previously set callback
    public void setOnEndOfMedia(Runnable callback) { player.setOnEndOfMedia(callback); }

    /// Registers a callback to be invoked when the {@link MediaPlayer} transitions to {@code READY} status, meaning
    /// metadata such as duration has been loaded.
    ///
    /// @param callback The {@link Runnable} to invoke; replaces any previously set callback
    public void setOnReady(Runnable callback) { player.setOnReady(callback); }

    /// Registers a callback to be invoked when a playback or loading error occurs. The callback receives the underlying
    /// {@link javafx.scene.media.MediaException}.
    ///
    /// @param callback A {@link Consumer} receiving the error; replaces any previously set callback
    public void setOnError(Consumer<Throwable> callback)
    {
        player.setOnError(() -> callback.accept(player.getError()));
    }

    /* VOLUME */

    /// Sets the base volume of this track. The effective playback volume is this value multiplied by the manager's
    /// global volume.
    ///
    /// @param volume Volume between {@code 0.0} (silent) and {@code 1.0} (full); clamped automatically
    public void setVolume(double volume)
    {
        this.userVolume = Math.clamp(volume, 0.0, 1.0);
        player.setVolume(userVolume);
    }

    /// Returns the base volume set via {@link #setVolume(double)}, independent of the global volume.
    ///
    /// @return The base volume between {@code 0.0} and {@code 1.0}
    public double getVolume() { return userVolume; }

    /// Smoothly animates the track volume to {@code newVolume} over the given duration. Updates the configured base
    /// volume to the target value once the animation completes.
    ///
    /// @param newVolume Target volume between {@code 0.0} and {@code 1.0}; clamped automatically
    /// @param duration  The transition duration; must not be {@code null}
    public void fadeToVolume(double newVolume, Duration duration)
    {
        double target = Math.clamp(newVolume, 0.0, 1.0);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(player.volumeProperty(), player.getVolume())),
                new KeyFrame(duration, new KeyValue(player.volumeProperty(), target))
        );
        timeline.setOnFinished(e -> this.userVolume = target);
        timeline.play();
    }

    /* PAN */

    /// Sets the stereo pan of this track, mapped to the underlying {@link MediaPlayer}'s balance property.
    ///
    /// @param pan Pan between {@code -1.0} (full left) and {@code 1.0} (full right); clamped automatically
    public void setPan(double pan) { player.setBalance(Math.clamp(pan, -1.0, 1.0)); }

    /// Returns the current stereo pan of this track.
    ///
    /// @return Pan value between {@code -1.0} and {@code 1.0}
    public double getPan() { return player.getBalance(); }

    /* FADE ON STOP */

    /// Configures whether stopping this track should fade out automatically before the actual stop occurs. When
    /// enabled, calling {@link #stop()} will fade out over {@code duration} before stopping.
    ///
    /// @param enabled  {@code true} to enable fade on stop
    /// @param duration The fade duration; {@code null} is treated as no fade
    public void setFadeOnStop(boolean enabled, Duration duration)
    {
        this.fadeOnStop = enabled;
        this.fadeOnStopDuration = duration != null ? duration : Duration.ZERO;
    }

    /* FADE */

    /// Fades in this track over the specified duration, starting from silence and animating up to the configured base
    /// volume. Starts playback immediately.
    ///
    /// @param duration The fade-in duration; must not be {@code null}
    @Override
    public void fadeIn(Duration duration)
    {
        player.setVolume(0.0);
        player.play();
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(player.volumeProperty(), 0.0)),
                new KeyFrame(duration, new KeyValue(player.volumeProperty(), userVolume))
        );
        timeline.play();
    }

    /// Fades out this track over the specified duration, from the current volume to silence. Does not stop playback or
    /// modify the configured base volume.
    ///
    /// @param duration The fade-out duration; must not be {@code null}
    @Override
    public void fadeOut(Duration duration)
    {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(player.volumeProperty(), player.getVolume())),
                new KeyFrame(duration, new KeyValue(player.volumeProperty(), 0.0))
        );
        timeline.play();
    }

    @Override
    void applyGlobalVolumeToSelf(double globalVolume)
    {
        player.setVolume(userVolume * Math.clamp(globalVolume, 0.0, 1.0));
    }

    /* INTERNAL */

    /// <b>{@code INTERNAL}</b> Disposes the underlying {@link MediaPlayer}, releasing all media resources. Called by
    /// {@link GameAudioManager} when this track is unloaded. Do not call directly.
    void dispose() { player.dispose(); }
}