package edu.nust.engine.core.audio;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

/// Use for short, one-shot sounds such as button clicks, footsteps, or hit effects.
/// <br><br>
/// Plays immediately when {@link SoundEffectReference#play()} is called. Multiple instances of the same clip can play
/// simultaneously. But cannot be stopped individually.
/// <br><br>
/// Do not instantiate directly; use {@link GameAudioManager#loadSoundEffect(String...)} instead.
/// <br><br>
/// <b>{@code Use .wav instead of .mp3}</b>
///
/// @see MusicTrackReference
/// @see GameAudioManager
public final class SoundEffectReference extends AudioReference
{
    private final AudioClip clip;
    private double userVolume = 1.0;
    private boolean spatialEnabled = false;
    private Duration cooldown = Duration.ZERO;
    private long lastPlayTime = 0L;
    private int maxConcurrentPlays = Integer.MAX_VALUE;

    /// <b>{@code INTERNAL}</b> Use {@link GameAudioManager#loadSoundEffect(String...)} instead
    SoundEffectReference(URL location, AudioClip clip)
    {
        super(location);
        this.clip = clip;
    }

    /* PLAYBACK */

    /// Plays the sound effect. If the clip is already playing, a new overlapping instance starts. Respects any
    /// configured cooldown
    public void play() { play(false); }

    /// Plays the sound effect, optionally stopping any currently playing instances first. Respects any configured
    /// cooldown
    ///
    /// @param stopExisting If {@code true}, stops all currently playing instances before playing
    public void play(boolean stopExisting)
    {
        if (!canPlay()) return;
        if (stopExisting) stopAll();
        clip.play();
        lastPlayTime = System.currentTimeMillis();
    }

    /// Plays the sound effect with a random pitch
    /// <br><br>
    /// Does not <b>{@code modify}</b> this pitch of the clip.
    public void playWithRandomPitch(boolean stopExisting)
    {
        double originalPitch = getPitch();
        setPitch(0.9 + Math.random() * 0.2); // Random pitch between 0.9 and 1.1
        play(stopExisting);
        setPitch(originalPitch);
    }

    /// Plays the sound effect with a random pitch
    /// <br><br>
    /// Does not <b>{@code modify}</b> this pitch of the clip.
    public void playWithRandomPitch() { playWithRandomPitch(false); }

    /// Stops <b>{@code all}</b> currently playing instances of this sound effect.
    public void stopAll() { clip.stop(); }

    /* STATE */

    public boolean isPlaying() { return clip.isPlaying(); }

    /* VOLUME */

    /// Sets the base volume of this sound effect. The effective playback volume is this value multiplied by the
    /// manager's global volume.
    ///
    /// @param volume Volume between {@code 0.0} (silent) and {@code 1.0} (full); clamped automatically
    ///
    /// @see GameAudioManager#setGlobalVolume(double)
    public void setVolume(double volume)
    {
        this.userVolume = Math.clamp(volume, 0.0, 1.0);
        clip.setVolume(userVolume);
    }

    /// Returns the base volume set via {@link #setVolume(double)}, independent of the global volume.
    ///
    /// @return The base volume between {@code 0.0} and {@code 1.0}
    ///
    /// @see GameAudioManager#setGlobalVolume(double)
    public double getVolume() { return userVolume; }

    /* PITCH */

    /// Sets the playback pitch multiplier. {@code 1.0} is normal pitch; {@code 2.0} doubles the pitch and halves the
    /// duration.
    ///
    /// @param pitch Pitch multiplier; clamped to a minimum of {@code 0.0}
    public void setPitch(double pitch) { clip.setRate(Math.max(0.0, pitch)); }

    /// Returns the current playback pitch multiplier.
    ///
    /// @return The current pitch multiplier
    public double getPitch() { return clip.getRate(); }

    /* BALANCE */

    /// Sets the stereo balance for this sound effect.
    ///
    /// @param balance Stereo balance between {@code -1.0} (full left) and {@code 1.0} (full right); clamped
    /// automatically
    public void setBalance(double balance) { clip.setBalance(Math.clamp(balance, -1.0, 1.0)); }

    /// Returns the current stereo balance.
    ///
    /// @return Balance between {@code -1.0} and {@code 1.0}
    public double getBalance() { return clip.getBalance(); }

    /* SPATIAL */

    /// Plays this sound effect with simple positional attenuation derived from the given world coordinates. The x-axis
    /// maps to stereo balance; Euclidean distance from the origin attenuates volume. Respects cooldown the same as
    /// {@link #play()}.
    /// <br><br>
    /// Reference distances: balance saturates at {@code 500} units from origin; volume reaches zero at {@code 1000}
    /// units from origin.
    ///
    /// @param x X coordinate in world space
    /// @param y Y coordinate in world space
    /// @param z Z coordinate in world space
    public void playAt(double x, double y, double z)
    {
        if (!canPlay() || !spatialEnabled) return;

        double balance = Math.clamp(x / 500.0, -1.0, 1.0);
        double distance = Math.sqrt(x * x + y * y + z * z);
        double volume = Math.clamp(1.0 - distance / 1000.0, 0.0, 1.0) * userVolume;

        clip.play(volume, balance, clip.getRate(), clip.getPan(), 1);
        lastPlayTime = System.currentTimeMillis();
    }

    /// Plays this sound effect with simple positional attenuation derived from the given world coordinates. The x-axis
    /// maps to stereo balance; Euclidean distance from the origin attenuates volume. Respects cooldown the same as
    /// {@link #play()}.
    /// <br><br>
    /// Reference distances: balance saturates at {@code 500} units from origin; volume reaches zero at {@code 1000}
    /// units from origin.
    ///
    /// @param x X coordinate in world space
    /// @param y Y coordinate in world space
    public void playAt(double x, double y) { playAt(x, y, 0); }

    /// Enables or disables 3D Audio for this sound effect. When disabled, {@link #playAt(double, double, double)}
    /// doesn't work
    ///
    /// @param enabled {@code true} to mark this effect as spatial
    public void setSpatialEnabled(boolean enabled) { this.spatialEnabled = enabled; }

    /// Returns whether spatialization is enabled for this sound effect.
    ///
    /// @return {@code true} if spatialization is enabled
    public boolean isSpatialEnabled() { return spatialEnabled; }

    /* SCHEDULING */

    /// Schedules this sound effect to play once after the given delay.
    ///
    /// @param delay        The delay before playback begins; must not be {@code null}
    /// @param stopExisting If {@code true}, stops all currently playing instances before playing
    public void schedulePlay(@NotNull Duration delay, boolean stopExisting)
    {
        if (!delay.greaterThanOrEqualTo(Duration.ZERO))
        {
            play(stopExisting);
            return;
        }
        Timeline timeline = new Timeline(new KeyFrame(delay, e -> play()));
        timeline.play();
    }

    /// Schedules this sound effect to play once after the given delay.
    ///
    /// @param delay The delay before playback begins; must not be {@code null}
    public void schedulePlay(@NotNull Duration delay) { schedulePlay(delay, false); }


    /// Schedules all currently playing instances to stop after the given delay.
    ///
    /// @param delay The delay before stopping; must not be {@code null}
    public void scheduleStopAll(@NotNull Duration delay)
    {
        if (!delay.greaterThanOrEqualTo(Duration.ZERO))
        {
            stopAll();
            return;
        }
        Timeline timeline = new Timeline(new KeyFrame(delay, e -> stopAll()));
        timeline.play();
    }

    /* COOLDOWN */

    /// Sets a minimum time that must elapse between successive plays. Calls to {@link #play()} while the cooldown is
    /// active are silently ignored.
    ///
    /// @param cooldown Minimum interval between plays; {@code null} is treated as no cooldown
    public void setCooldown(Duration cooldown) { this.cooldown = cooldown != null ? cooldown : Duration.ZERO; }

    /// Returns whether this sound effect can currently be played. Returns {@code false} if the configured cooldown
    /// period has not yet elapsed since the last play.
    ///
    /// @return {@code true} if the cooldown has elapsed or no cooldown is set
    public boolean canPlay()
    {
        if (cooldown.equals(Duration.ZERO)) return true;
        return (System.currentTimeMillis() - lastPlayTime) >= (long) cooldown.toMillis();
    }

    /* FADE */

    /// Fades in this sound effect over the specified duration. Starts the clip from silence and animates volume up to
    /// the configured base volume. Starts playback immediately if not already playing.
    ///
    /// @param duration The fade-in duration; must not be {@code null}
    @Override
    public void fadeIn(Duration duration)
    {
        clip.setVolume(0.0);
        clip.play();
        lastPlayTime = System.currentTimeMillis();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(clip.volumeProperty(), 0.0)),
                new KeyFrame(duration, new KeyValue(clip.volumeProperty(), userVolume))
        );
        timeline.play();
    }

    /// Fades out this sound effect over the specified duration, from the current volume to silence. Stops all instances
    /// once the fade completes. Does not modify the configured base volume.
    ///
    /// @param duration The fade-out duration; must not be {@code null}
    @Override
    public void fadeOut(Duration duration)
    {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(clip.volumeProperty(), clip.getVolume())),
                new KeyFrame(duration, new KeyValue(clip.volumeProperty(), 0.0))
        );
        timeline.setOnFinished(e -> stopAll());
        timeline.play();
    }

    @Override
    void applyGlobalVolumeToSelf(double globalVolume)
    {
        clip.setVolume(userVolume * Math.clamp(globalVolume, 0.0, 1.0));
    }
}