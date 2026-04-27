package edu.nust.engine.core.audio;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/// Use for short, one-shot sounds such as button clicks, footsteps, or hit effects.
/// <br><br>
/// Plays immediately when {@link SoundEffectReference#play()} is called. Overlap is bounded by
/// {@link #setMaxConcurrentPlays(int)} to avoid exhausting JavaFX's implementation-dependent mixer channels.
/// Individual overlapping instances still cannot be stopped independently.
/// <br><br>
/// Do not instantiate directly; use {@link GameAudioManager#loadSoundEffect(String...)} instead.
/// <br><br>
/// <b>{@code Use .wav instead of .mp3}</b>
///
/// @see MusicTrackReference
/// @see GameAudioManager
public final class SoundEffectReference extends AudioReference
{
    private final List<AudioClip> clipPool = new ArrayList<>();
    private double userVolume = 1.0;
    private double effectiveGlobalVolume = 1.0;
    private boolean spatialEnabled = false;
    private Duration cooldown = Duration.ZERO;
    private long lastPlayTime = 0L;
    private int maxConcurrentPlays = 1;

    /// <b>{@code INTERNAL}</b> Use {@link GameAudioManager#loadSoundEffect(String...)} instead
    SoundEffectReference(URL location, AudioClip clip)
    {
        super(location);
        clipPool.add(clip);
        applyDefaults(clip);
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
        if (!isCooldownComplete()) return;

        AudioClip clip = prepareClip(stopExisting);
        if (clip == null) return;

        clip.play(getEffectivePlaybackVolume(), clip.getBalance(), clip.getRate(), clip.getPan(), getPriority());
        lastPlayTime = System.currentTimeMillis();
    }

    /// Plays the sound effect with a random pitch
    /// <br><br>
    /// Does not <b>{@code modify}</b> this pitch of the clip.
    public void playWithRandomPitch(boolean stopExisting)
    {
        if (!isCooldownComplete()) return;

        AudioClip clip = prepareClip(stopExisting);
        if (clip == null) return;

        clip.play(getEffectivePlaybackVolume(), clip.getBalance(), 0.9 + Math.random() * 0.2, clip.getPan(), getPriority());
        lastPlayTime = System.currentTimeMillis();
    }

    /// Plays the sound effect with a random pitch
    /// <br><br>
    /// Does not <b>{@code modify}</b> this pitch of the clip.
    public void playWithRandomPitch() { playWithRandomPitch(false); }

    /// Stops <b>{@code all}</b> currently playing instances of this sound effect.
    public void stopAll() { forEachClip(AudioClip::stop); }

    /* STATE */

    public boolean isPlaying()
    {
        for (AudioClip clip : clipPool)
            if (clip.isPlaying()) return true;
        return false;
    }

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
        forEachClip(this::applyDefaults);
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
    public void setPitch(double pitch)
    {
        double clampedPitch = Math.max(0.125, pitch);
        forEachClip(clip -> clip.setRate(clampedPitch));
    }

    /// Returns the current playback pitch multiplier.
    ///
    /// @return The current pitch multiplier
    public double getPitch() { return clipPool.getFirst().getRate(); }

    /* BALANCE */

    /// Sets the stereo balance for this sound effect.
    ///
    /// @param balance Stereo balance between {@code -1.0} (full left) and {@code 1.0} (full right); clamped
    /// automatically
    public void setBalance(double balance)
    {
        double clampedBalance = Math.clamp(balance, -1.0, 1.0);
        forEachClip(clip -> clip.setBalance(clampedBalance));
    }

    /// Returns the current stereo balance.
    ///
    /// @return Balance between {@code -1.0} and {@code 1.0}
    public double getBalance() { return clipPool.getFirst().getBalance(); }

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
        if (!spatialEnabled || !isCooldownComplete()) return;

        AudioClip clip = prepareClip(false);
        if (clip == null) return;

        double balance = Math.clamp(x / 500.0, -1.0, 1.0);
        double distance = Math.sqrt(x * x + y * y + z * z);
        double volume = Math.clamp(1.0 - distance / 1000.0, 0.0, 1.0) * getEffectivePlaybackVolume();

        clip.play(volume, balance, clip.getRate(), clip.getPan(), getPriority());
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

    /// Sets the maximum number of overlapping instances that may play simultaneously for this clip.
    ///
    /// @param max Maximum concurrent plays; values less than {@code 1} are clamped to {@code 1}
    public void setMaxConcurrentPlays(int max)
    {
        this.maxConcurrentPlays = Math.max(1, max);

        // Shrink: stop and discard excess clips.
        while (clipPool.size() > maxConcurrentPlays)
        {
            AudioClip removed = clipPool.removeLast();
            removed.stop();
        }

        // Grow: eagerly create clips so no AudioClip construction happens on the game-loop thread.
        while (clipPool.size() < maxConcurrentPlays)
        {
            AudioClip clip = new AudioClip(getLocation().toExternalForm());
            applyDefaults(clip);
            clipPool.add(clip);
        }
    }

    /// Returns the configured maximum number of overlapping instances for this sound effect.
    ///
    /// @return Maximum concurrent plays, always at least {@code 1}
    public int getMaxConcurrentPlays() { return maxConcurrentPlays; }

    /// Returns whether this sound effect can currently be played. Returns {@code false} if the configured cooldown
    /// period has not yet elapsed since the last play.
    ///
    /// @return {@code true} if the cooldown has elapsed or no cooldown is set
    public boolean canPlay()
    {
        return isCooldownComplete();
    }

    /* FADE */

    /// Fades in this sound effect over the specified duration. Starts the clip from silence and animates volume up to
    /// the configured base volume. Starts playback immediately if not already playing.
    ///
    /// @param duration The fade-in duration; must not be {@code null}
    @Override
    public void fadeIn(Duration duration)
    {
        AudioClip clip = prepareClip(true);
        if (clip == null) return;

        clip.setVolume(0.0);
        clip.play(0.0, clip.getBalance(), clip.getRate(), clip.getPan(), getPriority());
        lastPlayTime = System.currentTimeMillis();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(clip.volumeProperty(), 0.0)),
            new KeyFrame(duration, new KeyValue(clip.volumeProperty(), getEffectivePlaybackVolume()))
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
        for (AudioClip clip : clipPool)
        {
            if (!clip.isPlaying()) continue;

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(clip.volumeProperty(), clip.getVolume())),
                    new KeyFrame(duration, new KeyValue(clip.volumeProperty(), 0.0))
            );
            timeline.setOnFinished(e -> {
                clip.stop();
                clip.setVolume(getEffectivePlaybackVolume());
            });
            timeline.play();
        }
    }

    @Override
    void applyGlobalVolumeToSelf(double globalVolume)
    {
        this.effectiveGlobalVolume = Math.clamp(globalVolume, 0.0, 1.0);
        forEachClip(this::applyDefaults);
    }

    @Override
    public void setPriority(int priority)
    {
        super.setPriority(priority);
        forEachClip(clip -> clip.setPriority(priority));
    }

    private boolean isCooldownComplete()
    {
        if (cooldown.equals(Duration.ZERO)) return true;
        return (System.currentTimeMillis() - lastPlayTime) >= (long) cooldown.toMillis();
    }

    private double getEffectivePlaybackVolume()
    {
        return userVolume * Math.clamp(effectiveGlobalVolume, 0.0, 1.0);
    }

    private AudioClip prepareClip(boolean stopExisting)
    {
        if (stopExisting)
        {
            stopAll();
            return clipPool.getFirst();
        }

        // Prefer an idle clip. If isPlaying() is stuck (a known JavaFX / macOS quirk after stop()
        // or end-of-clip), fall through to the first clip rather than silently dropping the request.
        AudioClip idleClip = findIdleClip();
        return idleClip != null ? idleClip : clipPool.getFirst();
    }

    private AudioClip findIdleClip()
    {
        for (AudioClip clip : clipPool)
            if (!clip.isPlaying()) return clip;

        return null;
    }

    private void applyDefaults(AudioClip clip)
    {
        clip.setVolume(getEffectivePlaybackVolume());
        clip.setBalance(clipPool.getFirst().getBalance());
        clip.setPan(clipPool.getFirst().getPan());
        clip.setRate(clipPool.getFirst().getRate());
        clip.setPriority(getPriority());
    }

    private void forEachClip(java.util.function.Consumer<AudioClip> action)
    {
        for (AudioClip clip : clipPool)
            action.accept(clip);
    }
}